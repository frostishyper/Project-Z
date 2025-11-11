// Cash-In Route Via Card
const express = require('express');
const router = express.Router();
const db = require('../Config/Connector');
const crypto = require('crypto');

// Utility: Generate Reference ID (7 random lowercase letters + DDMMYY)
function generateReferenceID() {
  const letters = crypto.randomBytes(4).toString('hex').slice(0, 7).toLowerCase();
  const now = new Date();
  const dateStr =
    String(now.getDate()).padStart(2, '0') +
    String(now.getMonth() + 1).padStart(2, '0') +
    String(now.getFullYear()).slice(2);
  return `${letters}${dateStr}`;
}

// Utility: Simple sanitization
function sanitizeInput(input) {
  if (typeof input === 'string') return input.trim().replace(/[^\w\s@.-]/g, '');
  return input;
}

// Utility: Validate card number format by type (updated for modern ranges)
function validateCardFormat(type, number) {
  const patterns = {
    Visa: /^4\d{12,18}$/, // Visa: starts with 4, 13–19 digits
    MasterCard: /^(5[1-5]\d{14}|2[2-7]\d{14})$/, // MasterCard: 51–55 or 2221–2720
    'American Express': /^3[47]\d{13}$/, // Amex: 34 or 37
    'Japan Credit Bureau': /^(?:35\d{14,17})$/, // JCB: 35 + 16–19 digits
    Discover: /^6(?:011|5\d{2}|4[4-9]\d|22\d{2})\d{12,15}$/, // Discover
  };

  // Fallback: any 13–19 digit number if unknown type
  const pattern = patterns[type] || /^\d{13,19}$/;
  return pattern.test(number);
}

// --- Main Route ---
router.post('/', async (req, res) => {
  const connection = await db.promise().getConnection();
  try {
    let {
      AccountNumber,
      CardType,
      CardNumberInput,
      ExpiryMonthInput,
      ExpiryYearInput,
      CVVInput,
      CashInAmountInput,
    } = req.body;

    // --- Step 1: Sanitize Inputs ---
    AccountNumber = sanitizeInput(AccountNumber);
    CardType = sanitizeInput(CardType);
    CardNumberInput = sanitizeInput(CardNumberInput);
    ExpiryMonthInput = parseInt(sanitizeInput(ExpiryMonthInput));
    ExpiryYearInput = parseInt(sanitizeInput(ExpiryYearInput));
    CVVInput = sanitizeInput(CVVInput);
    CashInAmountInput = parseFloat(CashInAmountInput);

    // --- Step 2: Validate inputs ---
    if (!AccountNumber || isNaN(CashInAmountInput) || CashInAmountInput <= 0) {
      connection.release();
      return res.status(400).json({ error: 'Invalid or missing input fields.' });
    }

    // --- Step 3: Check wallet existence ---
    const [walletCheck] = await connection.query(
      'SELECT * FROM Wallet WHERE Account_Number = ?',
      [AccountNumber]
    );

    if (walletCheck.length === 0) {
      connection.release();
      return res.status(404).json({ error: 'Wallet not found for this account.' });
    }

    // --- Step 4: Validate card details ---
    if (!CardNumberInput || !validateCardFormat(CardType, CardNumberInput)) {
      connection.release();
      return res.status(400).json({ error: 'Card number missing or invalid.', detectedBrand: null });
    }

    if (!/^\d{3,4}$/.test(CVVInput)) {
      connection.release();
      return res.status(400).json({ error: 'Invalid CVV format.' });
    }

    // --- Step 5: Check expiry ---
    const now = new Date();
    const currentMonth = now.getMonth() + 1;
    const currentYear = parseInt(now.getFullYear().toString().slice(-2));

    if (
      ExpiryYearInput < currentYear ||
      (ExpiryYearInput === currentYear && ExpiryMonthInput < currentMonth)
    ) {
      connection.release();
      return res.status(400).json({ error: 'Card is expired.' });
    }

    // --- Step 6: Begin transaction ---
    await connection.beginTransaction();

    const Reference_ID = generateReferenceID();
    const amount = parseFloat(CashInAmountInput); // amount to credit
    const fee = parseFloat((amount * 0.03).toFixed(2)); // 3% service fee
    const chargedTotal = parseFloat((amount + fee).toFixed(2)); // total to charge card
    const note = `Card Balance Transfer From ${CardType}`;

    // --- Step 7: Insert into Transactions ---
    const insertTransactionQuery = `
      INSERT INTO Transactions (
        Reference_ID, Sender_Type, Recipient_Type,
        Sender_Number, Recipient_Number, Merchant_Name,
        Amount, Fee, Note
      )
      VALUES (?, 'MERCHANT', 'USER', NULL, ?, ?, ?, ?, ?)
    `;

    await connection.query(insertTransactionQuery, [
      Reference_ID,
      AccountNumber,
      CardType,
      amount,
      fee,
      note,
    ]);

    // --- Step 8: Update wallet balance (credit full amount) ---
    const [walletUpdate] = await connection.query(
      `
      UPDATE Wallet
      SET Wallet_Balance = Wallet_Balance + ?
      WHERE Account_Number = ?
    `,
      [amount, AccountNumber]
    );

    if (walletUpdate.affectedRows === 0) {
      throw new Error('Failed to update wallet balance.');
    }

    // --- Step 9: Commit transaction ---
    await connection.commit();
    connection.release();

    // --- Step 10: Send response ---
    res.status(200).json({
      success: true,
      message: `Success! ₱${amount.toFixed(2)} credited to wallet.`,
      Reference_ID: Reference_ID,
      credited: amount,
      fee: fee,
      chargedTotal: chargedTotal,
    });

  } catch (err) {
    console.error('Server error:', err.message);
    try {
      await connection.rollback();
    } catch (rollbackErr) {
      console.error('Rollback failed:', rollbackErr.message);
    }
    connection.release();
    res.status(500).json({ error: 'Internal server error.' });
  }
});

module.exports = router;
