const express = require('express');
const router = express.Router();
const db = require('../Config/Connector');

// Get Wallet Balance Route
router.post('/', async (req, res) => {
  const { AccountNumber } = req.body;

  // Input validation - check if account number exists
  if (!AccountNumber) {
    return res.status(400).json({
      success: false,
      error: 'Account number is required'
    });
  }

  // Basic sanitization - trim whitespace
  const sanitizedNumber = AccountNumber.toString().trim();

  // Validate account number format (not empty after trim)
  if (sanitizedNumber.length === 0) {
    return res.status(400).json({
      success: false,
      error: 'Invalid account number format'
    });
  }

  try {
    // Query wallet balance
    const [results] = await db.promise().query(
      `SELECT w.Wallet_Balance, a.Account_FirstName, a.Account_LastName 
       FROM Wallet w
       JOIN Accounts a ON w.Account_Number = a.Account_Number
       WHERE w.Account_Number = ? AND a.Is_Active = TRUE`,
      [sanitizedNumber]
    );

    // Check if wallet exists
    if (results.length === 0) {
      return res.status(404).json({
        success: false,
        error: 'Wallet not found or account inactive'
      });
    }

    const wallet = results[0];

    // Success response
    return res.status(200).json({
      success: true,
      balance: parseFloat(wallet.Wallet_Balance).toFixed(2),
      accountHolder: `${wallet.Account_FirstName} ${wallet.Account_LastName}`
    });

  } catch (error) {
    console.error('Wallet balance error:', error);
    return res.status(500).json({
      success: false,
      error: 'Server error occurred while fetching balance' 
    });
  }
});

module.exports = router;