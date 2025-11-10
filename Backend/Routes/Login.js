// Login Route
const express = require('express');
const router = express.Router();
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const db = require('../Config/Connector');

// Login Route
router.post('/', async (req, res) => {
  const { LoginNumber, LoginPin } = req.body;

  // Input validation - check if all fields exist
  if (!LoginNumber || !LoginPin) {
    return res.status(400).json({
      success: false,
      error: 'Account number and PIN are required'
    });
  }

  // Basic sanitization - trim whitespace
  const sanitizedData = {
    number: LoginNumber.toString().trim(),
    pin: LoginPin.toString().trim()
  };

  // Validate PIN length
  if (sanitizedData.pin.length < 6) {
    return res.status(400).json({
      success: false,
      error: 'Invalid PIN format'
    });
  }

  try {
    // Query database for account
    const [results] = await db.promise().query(
      `SELECT Account_Number, Account_Pin, Account_FirstName, Account_LastName, 
              Account_Username, Account_Email, Is_Active 
       FROM Accounts 
       WHERE Account_Number = ?`,
      [sanitizedData.number]
    );

    // Check if account exists
    if (results.length === 0) {
      return res.status(401).json({
        success: false,
        error: 'Account details are incorrect'
      });
    }

    const account = results[0];

    // Check if account is active
    if (!account.Is_Active) {
      return res.status(403).json({
        success: false,
        error: 'Account is inactive'
      });
    }

    // Verify PIN using bcrypt
    const pinMatch = await bcrypt.compare(sanitizedData.pin, account.Account_Pin);

    if (!pinMatch) {
      return res.status(401).json({
        success: false,
        error: 'Account details are incorrect'
      });
    }

    // Generate JWT token (valid for 7 days)
    const token = jwt.sign(
      { accountNumber: account.Account_Number },
      process.env.JWT_SECRET,
      { expiresIn: '7d' }
    );

    // Success response
    return res.status(200).json({
      success: true,
      message: 'Login successful',
      token: token,
      user: {
        accountNumber: account.Account_Number,
        firstName: account.Account_FirstName,
        lastName: account.Account_LastName,
        username: account.Account_Username,
        email: account.Account_Email
      }
    });

  } catch (error) {
    console.error('Login error:', error);
    return res.status(500).json({
      success: false,
      error: 'Server error occurred during login'
    });
  }
});

module.exports = router;