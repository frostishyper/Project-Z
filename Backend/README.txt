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
    
    isTransfer BOOLEAN NOT NULL DEFAULT FALSE,
    isPurchase BOOLEAN NOT NULL DEFAULT FALSE,
    
    Sender_Number VARCHAR(15),
    Recipient_Number VARCHAR(15),
    Merchant_Name VARCHAR(100),
    
    Fee DECIMAL(15,2) DEFAULT 0.00,
    Amount DECIMAL(15,2) NOT NULL,
    Created_At DATETIME DEFAULT CURRENT_TIMESTAMP,
    
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
        ON UPDATE CASCADE,
        
    CONSTRAINT chk_transaction_type
        CHECK (
            (isTransfer = TRUE AND isPurchase = FALSE)
            OR (isTransfer = FALSE AND isPurchase = TRUE)
        )
);

<------------------------------------------------------------------------------------------------>



<------------------------------------------------------------------------------------------------>