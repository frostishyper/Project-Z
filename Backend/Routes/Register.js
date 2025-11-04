//  =====================================
//  _ (1.0)
//  (PLEASE UPDATE VERSION IF YOUR UPDATING)
//  
//  Digest:
//  Register Router, recieves register details from front end andd proccesses them, sanitizes and validated the details. if it passes the checks then 
//  an account is created with a matching wallet.
//  
//  
//
//  =====================================

const express = require('express');
const router = express.Router();
const bcrypt = require('bcrypt');

const db = require('../Config/Connector');

// Register Route
router.post('/', async (req, res) => {
  const {
    RegisterNumber,
    RegisterEmail,
    RegisterFirstname,
    RegisterLastname,
    RegisterUsername,
    RegisterPin
  } = req.body;

  // Input validation - check if all fields exist
  if (!RegisterNumber || !RegisterEmail || !RegisterFirstname || 
      !RegisterLastname || !RegisterUsername || !RegisterPin) {
    return res.status(400).json({
      success: false,
      error: 'All fields are required'
    });
  }

  // Basic sanitization - trim whitespace
  const sanitizedData = {
    number: RegisterNumber.toString().trim(),
    email: RegisterEmail.trim().toLowerCase(),
    firstname: RegisterFirstname.trim(),
    lastname: RegisterLastname.trim(),
    username: RegisterUsername.trim(),
    pin: RegisterPin.toString().trim()
  };

  // Additional validation
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(sanitizedData.email)) {
    return res.status(400).json({
      success: false,
      error: 'Invalid email format'
    });
  }

  if (sanitizedData.pin.length < 6) {
    return res.status(400).json({
      success: false,
      error: 'PIN must be at least 6 characters'
    });
  }

  try {
    // Start transaction
    await db.promise().beginTransaction();

    // Check for existing account number
    const [numberCheck] = await db.promise().query(
      'SELECT Account_Number FROM Accounts WHERE Account_Number = ?',
      [sanitizedData.number]
    );
    if (numberCheck.length > 0) {
      await db.promise().rollback();
      return res.status(409).json({
        success: false,
        error: 'Account number already exists'
      });
    }

    // Check for existing email
    const [emailCheck] = await db.promise().query(
      'SELECT Account_Email FROM Accounts WHERE Account_Email = ?',
      [sanitizedData.email]
    );
    if (emailCheck.length > 0) {
      await db.promise().rollback();
      return res.status(409).json({
        success: false,
        error: 'Email already exists'
      });
    }

    // Check for existing username
    const [usernameCheck] = await db.promise().query(
      'SELECT Account_Username FROM Accounts WHERE Account_Username = ?',
      [sanitizedData.username]
    );
    if (usernameCheck.length > 0) {
      await db.promise().rollback();
      return res.status(409).json({
        success: false,
        error: 'Username already exists'
      });
    }

    // Hash the PIN
    const hashedPin = await bcrypt.hash(sanitizedData.pin, 10);

    // Insert into Accounts table
    const [accountResult] = await db.promise().query(
      `INSERT INTO Accounts 
       (Account_Number, Account_Email, Account_FirstName, Account_LastName, 
        Account_Username, Account_Pin) 
       VALUES (?, ?, ?, ?, ?, ?)`,
      [
        sanitizedData.number,
        sanitizedData.email,
        sanitizedData.firstname,
        sanitizedData.lastname,
        sanitizedData.username,
        hashedPin
      ]
    );

    if (!accountResult.insertId) {
      await db.promise().rollback();
      return res.status(500).json({
        success: false,
        error: 'Failed to create account'
      });
    }

    // Create corresponding Wallet entry
    const [walletResult] = await db.promise().query(
      `INSERT INTO Wallet (Account_Number, Wallet_Balance) 
       VALUES (?, 0.00)`,
      [sanitizedData.number]
    );

    if (!walletResult.insertId) {
      await db.promise().rollback();
      return res.status(500).json({
        success: false,
        error: 'Failed to create wallet'
      });
    }

    // Commit transaction
    await db.promise().commit();

    // Success response
    return res.status(201).json({
      success: true,
      message: 'Account successfully created',
      accountNumber: sanitizedData.number
    });

  } catch (error) {
    // Rollback on any error
    await db.promise().rollback();
    console.error('Signup error:', error);
    return res.status(500).json({
      success: false,
      error: 'Server error occurred during signup'
    });
  }
});

module.exports = router;