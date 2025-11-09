const express = require('express');
const crypto = require('crypto');
const router = express.Router();
const db = require('../Config/Connector');

// Step 1: Validate and prepare transaction
router.post('/prepare', async (req, res) => {
  const { UserNumber, RecipientNumber, SendAmount, Note } = req.body;

  // Input validation
  if (!UserNumber || !RecipientNumber || !SendAmount) {
    return res.status(400).json({
      success: false,
      error: 'User number, recipient number, and amount are required'
    });
  }

  // Sanitization
  const sanitizedData = {
    userNumber: UserNumber.toString().trim(),
    recipientNumber: RecipientNumber.toString().trim(),
    amount: parseFloat(SendAmount),
    note: Note ? Note.toString().trim().substring(0, 50) : '' // Max 50 chars
  };

  // Validate amount
  if (isNaN(sanitizedData.amount) || sanitizedData.amount <= 0) {
    return res.status(400).json({
      success: false,
      error: 'Invalid amount'
    });
  }

  // Check if sending to self
  if (sanitizedData.userNumber === sanitizedData.recipientNumber) {
    return res.status(400).json({
      success: false,
      error: 'Cannot send money to yourself'
    });
  }

  try {
    // Check sender wallet exists and is active
    const [senderCheck] = await db.promise().query(
      `SELECT w.Wallet_Balance, a.Account_Username, a.Is_Active
       FROM Wallet w
       JOIN Accounts a ON w.Account_Number = a.Account_Number
       WHERE w.Account_Number = ?`,
      [sanitizedData.userNumber]
    );

    if (senderCheck.length === 0) {
      return res.status(404).json({
        success: false,
        error: 'Sender account not found'
      });
    }

    if (!senderCheck[0].Is_Active) {
      return res.status(403).json({
        success: false,
        error: 'Sender account is inactive'
      });
    }

    // Check recipient wallet exists and is active
    const [recipientCheck] = await db.promise().query(
      `SELECT w.Wallet_Balance, a.Account_Username, a.Is_Active
       FROM Wallet w
       JOIN Accounts a ON w.Account_Number = a.Account_Number
       WHERE w.Account_Number = ?`,
      [sanitizedData.recipientNumber]
    );

    if (recipientCheck.length === 0) {
      return res.status(404).json({
        success: false,
        error: 'Recipient account not found'
      });
    }

    if (!recipientCheck[0].Is_Active) {
      return res.status(403).json({
        success: false,
        error: 'Recipient account is inactive'
      });
    }

    const senderBalance = parseFloat(senderCheck[0].Wallet_Balance);
    const fee = 1.00;
    const totalRequired = sanitizedData.amount + fee;

    // Check if sender has sufficient balance
    if (senderBalance < totalRequired) {
      return res.status(400).json({
        success: false,
        error: 'Insufficient balance',
        currentBalance: senderBalance.toFixed(2),
        required: totalRequired.toFixed(2)
      });
    }

    const projectedBalance = senderBalance - totalRequired;

    // Success response with transaction preview
    return res.status(200).json({
      success: true,
      recipientNumber: sanitizedData.recipientNumber,
      recipientUsername: recipientCheck[0].Account_Username,
      transferAmount: sanitizedData.amount.toFixed(2),
      transferFee: fee.toFixed(2),
      totalAmount: totalRequired.toFixed(2),
      note: sanitizedData.note,
      projectedBalance: projectedBalance.toFixed(2)
    });

  } catch (error) {
    console.error('Prepare transfer error:', error);
    return res.status(500).json({
      success: false,
      error: 'Server error occurred while preparing transfer'
    });
  }
});

// Step 2: Commit the transaction
router.post('/commit', async (req, res) => {
  const { UserNumber, RecipientNumber, SendAmount, Note } = req.body;

  if (!UserNumber || !RecipientNumber || !SendAmount) {
    return res.status(400).json({
      success: false,
      error: 'User number, recipient number, and amount are required'
    });
  }

  const sanitizedData = {
    userNumber: UserNumber.toString().trim(),
    recipientNumber: RecipientNumber.toString().trim(),
    amount: parseFloat(SendAmount),
    note: Note ? Note.toString().trim().substring(0, 50) : ''
  };

  if (isNaN(sanitizedData.amount) || sanitizedData.amount <= 0) {
    return res.status(400).json({
      success: false,
      error: 'Invalid amount'
    });
  }

  const fee = 1.00;
  const totalRequired = sanitizedData.amount + fee;

  const connection = await db.promise().getConnection();

  try {
    await connection.beginTransaction();

    const [senderWallet] = await connection.query(
      `SELECT w.Wallet_Balance, a.Is_Active
       FROM Wallet w
       JOIN Accounts a ON w.Account_Number = a.Account_Number
       WHERE w.Account_Number = ?
       FOR UPDATE`,
      [sanitizedData.userNumber]
    );

    if (senderWallet.length === 0 || !senderWallet[0].Is_Active) {
      await connection.rollback();
      connection.release();
      return res.status(404).json({
        success: false,
        error: 'Sender account not found or inactive'
      });
    }

    const senderBalance = parseFloat(senderWallet[0].Wallet_Balance);

    if (senderBalance < totalRequired) {
      await connection.rollback();
      connection.release();
      return res.status(400).json({
        success: false,
        error: 'Insufficient balance'
      });
    }

    const [recipientWallet] = await connection.query(
      `SELECT w.Wallet_Balance, a.Is_Active
       FROM Wallet w
       JOIN Accounts a ON w.Account_Number = a.Account_Number
       WHERE w.Account_Number = ?
       FOR UPDATE`,
      [sanitizedData.recipientNumber]
    );

    if (recipientWallet.length === 0 || !recipientWallet[0].Is_Active) {
      await connection.rollback();
      connection.release();
      return res.status(404).json({
        success: false,
        error: 'Recipient account not found or inactive'
      });
    }

    const referenceId = generateReferenceId();

    await connection.query(
      'UPDATE Wallet SET Wallet_Balance = Wallet_Balance - ? WHERE Account_Number = ?',
      [totalRequired, sanitizedData.userNumber]
    );

    await connection.query(
      'UPDATE Wallet SET Wallet_Balance = Wallet_Balance + ? WHERE Account_Number = ?',
      [sanitizedData.amount, sanitizedData.recipientNumber]
    );

    await connection.query(
      `INSERT INTO Transactions 
       (Reference_ID, Sender_Type, Recipient_Type, Sender_Number, Recipient_Number, 
        Amount, Fee, Note)
       VALUES (?, 'USER', 'USER', ?, ?, ?, ?, ?)`,
      [
        referenceId,
        sanitizedData.userNumber,
        sanitizedData.recipientNumber,
        sanitizedData.amount,
        fee,
        sanitizedData.note
      ]
    );

    await connection.commit();

    const [updatedBalance] = await connection.query(
      'SELECT Wallet_Balance FROM Wallet WHERE Account_Number = ?',
      [sanitizedData.userNumber]
    );

    connection.release();

    return res.status(200).json({
      success: true,
      message: 'Transfer successful',
      referenceId: referenceId,
      newBalance: parseFloat(updatedBalance[0].Wallet_Balance).toFixed(2),
      amountSent: sanitizedData.amount.toFixed(2),
      feePaid: fee.toFixed(2)
    });

  } catch (error) {
    try {
      await connection.rollback();
    } catch (rollbackError) {
      console.error('Rollback error:', rollbackError);
    }
    connection.release();
    console.error('Commit transfer error:', error);
    return res.status(500).json({
      success: false,
      error: 'Server error occurred during transfer'
    });
  }
});

// Utility: Generate Reference ID (7 random lowercase letters + DDMMYY)
function generateReferenceId() {
  const letters = crypto.randomBytes(4).toString('hex').slice(0, 7).toLowerCase();
  const now = new Date();
  const dateStr =
    String(now.getDate()).padStart(2, '0') +
    String(now.getMonth() + 1).padStart(2, '0') +
    String(now.getFullYear()).slice(2);
  return `${letters}${dateStr}`;
}

module.exports = router;