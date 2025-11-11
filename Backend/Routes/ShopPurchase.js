const express = require('express');
const router = express.Router();
const crypto = require('crypto');
const db = require('../Config/Connector');


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

// Utility: Generate a modular transaction note based on merchant
function generateTransactionNote(merchantName, listingName, listingPrice, recipientDetails) {
    switch (merchantName) {
        case 'Valve Corp':
            const email = recipientDetails.steamEmail || 'N/A';
            const price = listingPrice.toFixed(2);
            return `Steam Gift Card ($${price}) Code sent to ${email}`;
        
        case 'Kuro Games':
            const uid = recipientDetails.wuwaUserID || 'N/A';
            return `${listingName} Sent to ${uid}`;

        // Add more merchants here in the future
        default:
            return `Purchase of ${listingName}`;
    }
}


// --- Stage 1: Prepare Transaction ---
router.post('/prepare', async (req, res) => {

    const { UserNumber, Listing_ID, recipientDetails } = req.body;

    // 1. Sanitization & Input Validation
    if (!UserNumber || !Listing_ID || !recipientDetails) {
        return res.status(400).json({
            success: false,
            // Error message updated to be correct
            error: 'UserNumber, Listing_ID, and recipientDetails are required'
        });
    }

    try {
        // 2. Check if Listing exists and User has a wallet
        const query = `
            SELECT 
                l.Listing_Name, 
                l.Listing_Price, 
                l.Merchant_Name, 
                w.Wallet_Balance 
            FROM Listings AS l
            JOIN Wallet AS w ON w.Account_Number = ?
            WHERE l.Listing_ID = ?
        `;
        const [results] = await db.promise().query(query, [UserNumber, Listing_ID]);

        if (results.length === 0) {
            return res.status(404).json({
                success: false,
                error: 'Listing or User Wallet not found'
            });
        }

        const data = results[0];
        const initialBalance = parseFloat(data.Wallet_Balance);
        const cost = parseFloat(data.Listing_Price);

        // 3. Validate the shop-specific recipientDetails
        if (data.Merchant_Name === 'Valve Corp' && !recipientDetails.steamEmail) {
            return res.status(400).json({
                success: false,
                error: 'Steam Email (recipientDetails.steamEmail) is required for this purchase'
            });
        }
        if (data.Merchant_Name === 'Kuro Games' && !recipientDetails.wuwaUserID) {
            return res.status(400).json({
                success: false,
                error: 'Wuwa UserID (recipientDetails.wuwaUserID) is required for this purchase'
            });
        }

        // 4. Check if wallet balance is sufficient
        if (initialBalance < cost) {
            return res.status(400).json({
                success: false,
                error: 'Insufficient funds',
                initialBalance: initialBalance,
                cost: cost
            });
        }

        // 5. Success! Return the preview
        const projectedNewBalance = initialBalance - cost;
        return res.status(200).json({
            success: true,
            merchantName: data.Merchant_Name,
            listingName: data.Listing_Name,
            initialBalance: initialBalance,
            cost: cost,
            projectedNewBalance: projectedNewBalance
        });

    } catch (error) {
        console.error('Shop Prepare error:', error);
        return res.status(500).json({
            success: false,
            error: 'Server error during purchase preparation'
        });
    }
});


// --- Stage 2: Commit Transaction ---
router.post('/commit', async (req, res) => {
    const { UserNumber, Listing_ID, recipientDetails } = req.body;

    // 1. Sanitization & Input Validation (again)
    if (!UserNumber || !Listing_ID || !recipientDetails) {
        return res.status(400).json({
            success: false,
            error: 'UserNumber, Listing_ID, and recipientDetails are required'
        });
    }

    const connection = await db.promise().getConnection();

    try {
        await connection.beginTransaction();

        // 2. Re-verify all data, but this time lock the wallet row
        const query = `
            SELECT 
                l.Listing_Name, 
                l.Listing_Price, 
                l.Merchant_Name, 
                w.Wallet_Balance 
            FROM Listings AS l
            JOIN Wallet AS w ON w.Account_Number = ?
            WHERE l.Listing_ID = ?
            FOR UPDATE
        `;
        const [results] = await connection.query(query, [UserNumber, Listing_ID]);

        if (results.length === 0) {
            await connection.rollback();
            return res.status(404).json({
                success: false,
                error: 'Listing or User Wallet not found'
            });
        }

        const data = results[0];
        const initialBalance = parseFloat(data.Wallet_Balance);
        const cost = parseFloat(data.Listing_Price);

        // 3. Re-validate shop-specific recipientDetails
        if (data.Merchant_Name === 'Valve Corp' && !recipientDetails.steamEmail) {
            await connection.rollback();
            return res.status(400).json({
                success: false,
                error: 'Steam Email (recipientDetails.steamEmail) is required'
            });
        }
        if (data.Merchant_Name === 'Kuro Games' && !recipientDetails.wuwaUserID) {
            await connection.rollback();
            return res.status(400).json({
                success: false,
                error: 'Wuwa UserID (recipientDetails.wuwaUserID) is required'
            });
        }

        // 4. Re-check if wallet balance is sufficient
        if (initialBalance < cost) {
            await connection.rollback();
            return res.status(400).json({
                success: false,
                error: 'Insufficient funds'
            });
        }

        // 5. All checks passed. Proceed with transaction.
        const referenceID = generateReferenceID();
        const note = generateTransactionNote(data.Merchant_Name, data.Listing_Name, cost, recipientDetails);

        // Step 5a: Write to Transactions table
        const insertTxQuery = `
            INSERT INTO Transactions 
                (Reference_ID, Sender_Type, Recipient_Type, Sender_Number, Merchant_Name, Amount, Note)
            VALUES 
                (?, 'USER', 'MERCHANT', ?, ?, ?, ?)
        `;
        await connection.query(insertTxQuery, [
            referenceID,
            UserNumber,
            data.Merchant_Name,
            cost,
            note
        ]);

        // Step 5b: Deduct balance from Wallet
        const updateWalletQuery = `
            UPDATE Wallet 
            SET Wallet_Balance = Wallet_Balance - ? 
            WHERE Account_Number = ?
        `;
        await connection.query(updateWalletQuery, [cost, UserNumber]);

        // 6. Commit the transaction
        await connection.commit();

        // 7. Success!
        return res.status(200).json({
            success: true,
            message: 'Purchase successful',
            referenceID: referenceID,
            newBalance: initialBalance - cost
        });

    } catch (error) {
        // If anything fails, roll back all changes
        await connection.rollback();
        console.error('Shop Commit error:', error);
        return res.status(500).json({
            success: false,
            error: 'Server error during purchase commitment'
        });
    } finally {
        if (connection) {
            connection.release();
        }
    }
});


module.exports = router;