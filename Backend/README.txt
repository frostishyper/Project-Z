BACKEND WORKING PROCEDURES

For After Each Pull, Open Terminal And Run These Command(s) First
- cd Backend
- npm install
[This Will Ensure All Necessary NPM Package Dependencies Are Updated Or Installed On Your Local Instance]

To Run Backend Server (Read package.json)
- npm run dev

MySQL DB Structure

(Inside One Schema):

<------------------------------------------------------------------------------------------------>

CREATE TABLE Accounts (
    Account_ID INT AUTO_INCREMENT PRIMARY KEY,   -- Internal unique ID (best practice)
    Account_Number VARCHAR(15) NOT NULL UNIQUE,  -- e.g., phone number (stored as string to preserve leading zeros)
    Account_Email VARCHAR(255) NOT NULL UNIQUE,  -- enforce unique emails
    Account_FirstName VARCHAR(100) NOT NULL,
    Account_LastName VARCHAR(100) NOT NULL,
    Account_Username VARCHAR(50) NOT NULL UNIQUE,
    Account_Pin CHAR(64) NOT NULL,               -- store SHA-256 hash as fixed-length hex string
    Created_At DATETIME DEFAULT CURRENT_TIMESTAMP,
    Updated_At DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    Is_Active BOOLEAN DEFAULT TRUE
);

<------------------------------------------------------------------------------------------------>

CREATE TABLE Wallet (
    Wallet_ID INT AUTO_INCREMENT PRIMARY KEY,
    Account_Number VARCHAR(15) NOT NULL,
    Wallet_Balance DECIMAL(15,2) DEFAULT 0.00,
    Created_At DATETIME DEFAULT CURRENT_TIMESTAMP,
    Updated_At DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_wallet_account
        FOREIGN KEY (Account_Number)
        REFERENCES Accounts(Account_Number)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

<------------------------------------------------------------------------------------------------>

(MERCHANTS TABLE):

CREATE TABLE Merchants (
    Merchant_ID INT AUTO_INCREMENT PRIMARY KEY,
    Merchant_Name VARCHAR(100) NOT NULL UNIQUE,
    Created_At DATETIME DEFAULT CURRENT_TIMESTAMP,
    Updated_At DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

<------------------------------------------------------------------------------------------------>

(TRANSACTIONS TABLE):

CREATE TABLE Transactions (
    Transaction_ID INT AUTO_INCREMENT PRIMARY KEY,
    Reference_ID VARCHAR(13) NOT NULL UNIQUE,

    -- Transaction actors
    Sender_Type ENUM('USER', 'MERCHANT') NOT NULL,
    Recipient_Type ENUM('USER', 'MERCHANT') NOT NULL,

    -- Optional depending on type
    Sender_Number VARCHAR(15) NULL,
    Recipient_Number VARCHAR(15) NULL,
    Merchant_Name VARCHAR(100) NULL,

    -- Financials
    Amount DECIMAL(15,2) NOT NULL,
    Fee DECIMAL(15,2) DEFAULT 0.00,
    Note VARCHAR(50),
    Created_At DATETIME DEFAULT CURRENT_TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_sender
        FOREIGN KEY (Sender_Number)
        REFERENCES Accounts(Account_Number)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    CONSTRAINT fk_recipient
        FOREIGN KEY (Recipient_Number)
        REFERENCES Accounts(Account_Number)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    CONSTRAINT fk_merchant
        FOREIGN KEY (Merchant_Name)
        REFERENCES Merchants(Merchant_Name)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

<------------------------------------------------------------------------------------------------>


(LISTINGS TABLE):

-- This table stores the main "product" or "bundle" sold in the store.
CREATE TABLE Listings (
    Listing_ID INT AUTO_INCREMENT PRIMARY KEY,
    
    -- Foreign key linking to the Merchants table by name
    Merchant_Name VARCHAR(100) NOT NULL,
    
    -- The display name of the listing, e.g., "Radiant Package"
    Listing_Name VARCHAR(255) NOT NULL UNIQUE,
    
    -- The price of the listing
    Listing_Price DECIMAL(15, 2) DEFAULT 0.00,
    
    -- The name of the icon for the frontend, e.g., "radiant_package_icon"
    Listing_Icon_Name VARCHAR(100) NULL,
    
    -- Timestamps for tracking
    Created_At DATETIME DEFAULT CURRENT_TIMESTAMP,
    Updated_At DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Defines the foreign key relationship
    CONSTRAINT fk_listing_merchant
        FOREIGN KEY (Merchant_Name)
        REFERENCES Merchants(Merchant_Name)
        ON DELETE RESTRICT 
        ON UPDATE CASCADE   
);

<------------------------------------------------------------------------------------------------>


(LISTINGS INCLUSIONS TABLE):

-- This table stores all the individual items inside a listing.
-- This creates the one-to-many relationship (1 Listing -> Many Inclusions).
CREATE TABLE Listing_Inclusions (
    Inclusion_ID INT AUTO_INCREMENT PRIMARY KEY,
    
    -- Foreign key linking to the main Listings table
    Listing_ID INT NOT NULL,
    
    Inclusion_Name VARCHAR(255) NOT NULL,
    
    -- The quantity of this item, e.g., 1, 10, or 600
    Quantity INT NOT NULL,
    
    -- Defines the foreign key relationship
    CONSTRAINT fk_inclusion_listing
        FOREIGN KEY (Listing_ID)
        REFERENCES Listings(Listing_ID)
        ON DELETE CASCADE 
        ON UPDATE CASCADE  
);

<------------------------------------------------------------------------------------------------>