const express = require('express');
const router = express.Router();
const db = require('../Config/Connector');

router.post('/', async (req, res) => {
    
    
    const { merchant_name } = req.body; 

    // 1. Input validation
    if (!merchant_name) {
        return res.status(400).json({
            success: false,
            error: 'Merchant name is required in the request body' 
        });
    }

    try {
        // 2. The Query
        // We use a LEFT JOIN to ensure we still get listings even if they have 0 inclusions.
        // We select all fields from Listings and the relevant fields from Listing_Inclusions.
        const query = `
            SELECT 
                l.Listing_ID, 
                l.Listing_Name, 
                l.Listing_Price, 
                l.Listing_Icon_Name,
                l.Created_At AS Listing_Created_At,
                li.Inclusion_ID, 
                li.Inclusion_Name, 
                li.Quantity
            FROM 
                Listings AS l
            LEFT JOIN 
                Listing_Inclusions AS li ON l.Listing_ID = li.Listing_ID
            WHERE 
                l.Merchant_Name = ?
            ORDER BY 
                l.Listing_ID; -- Ordering is important for the processing step
        `;

        const [results] = await db.promise().query(query, [merchant_name]);

        // 3. Check if any listings were found
        if (results.length === 0) {
            // This is not an error, just an empty result set.
            return res.status(200).json({
                success: true,
                merchant: merchant_name,
                listings: [] // Send an empty array
            });
        }

        // 4. Process the flat results into a nested structure (easy for frontend)
        const listingsMap = new Map();

        for (const row of results) {
            // Check if we've already added this listing to our map
            if (!listingsMap.has(row.Listing_ID)) {
                // If not, create the main listing object
                listingsMap.set(row.Listing_ID, {
                    listingId: row.Listing_ID,
                    listingName: row.Listing_Name,
                    listingPrice: row.Listing_Price,
                    listingIconName: row.Listing_Icon_Name,
                    createdAt: row.Listing_Created_At,
                    inclusions: [] // Initialize the inclusions array
                });
            }

            
            // (it might be null from the LEFT JOIN if a listing has no inclusions)
            if (row.Inclusion_ID) {
                listingsMap.get(row.Listing_ID).inclusions.push({
                    inclusionId: row.Inclusion_ID,
                    inclusionName: row.Inclusion_Name,
                    quantity: row.Quantity
                });
            }
        }

        // 5. Convert the map's values into our final array
        const formattedListings = Array.from(listingsMap.values());

        // 6. Success response
        return res.status(200).json({
            success: true,
            merchant: merchant_name,
            listings: formattedListings
        });

    } catch (error) {
        console.error('Shop route error:', error);
        return res.status(500).json({
            success: false,
            error: 'Server error occurred while fetching shop data'
        });
    }
});

module.exports = router;