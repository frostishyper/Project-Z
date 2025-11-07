const express = require('express');
const router = express.Router();
const db = require('../Config/Connector');

// Get Transaction History
router.post('/', async (req, res) => {
  const { AccountNumber } = req.body;

  // Input validation
  if (!AccountNumber) {
    return res.status(400).json({
      success: false,
      error: 'Account number is required'
    });
  }

  // Sanitization
  const sanitizedNumber = AccountNumber.toString().trim();

  if (sanitizedNumber.length === 0) {
    return res.status(400).json({
      success: false,
      error: 'Invalid account number format'
    });
  }

  try {
    // Verify account exists and is active
    const [accountCheck] = await db.promise().query(
      'SELECT Account_Number FROM Accounts WHERE Account_Number = ? AND Is_Active = TRUE',
      [sanitizedNumber]
    );

    if (accountCheck.length === 0) {
      return res.status(404).json({
        success: false,
        error: 'Account not found or inactive'
      });
    }

    // Get all transactions where user is sender OR recipient
    const [transactions] = await db.promise().query(
      `SELECT 
        Transaction_ID,
        Reference_ID,
        Sender_Type,
        Recipient_Type,
        Sender_Number,
        Recipient_Number,
        Merchant_Name,
        Amount,
        Fee,
        Note,
        Created_At,
        CASE 
          WHEN Sender_Number = ? THEN 'OUTGOING'
          WHEN Recipient_Number = ? THEN 'INCOMING'
        END AS Direction
      FROM Transactions
      WHERE Sender_Number = ? OR Recipient_Number = ?
      ORDER BY Created_At DESC`,
      [sanitizedNumber, sanitizedNumber, sanitizedNumber, sanitizedNumber]
    );

    // Format response
    const formattedTransactions = transactions.map(tx => ({
      transactionId: tx.Transaction_ID,
      referenceId: tx.Reference_ID,
      direction: tx.Direction,
      senderType: tx.Sender_Type,
      senderNumber: tx.Sender_Number,
      recipientType: tx.Recipient_Type,
      recipientNumber: tx.Recipient_Number,
      merchantName: tx.Merchant_Name,
      amount: parseFloat(tx.Amount).toFixed(2),
      fee: parseFloat(tx.Fee).toFixed(2),
      note: tx.Note,
      date: tx.Created_At
    }));

    return res.status(200).json({
      success: true,
      transactionCount: formattedTransactions.length,
      transactions: formattedTransactions
    });

  } catch (error) {
    console.error('Transaction history error:', error);
    return res.status(500).json({
      success: false,
      error: 'Server error occurred while fetching transactions'
    });
  }
});

module.exports = router;