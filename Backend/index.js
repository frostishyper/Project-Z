// Backend Main

//  =====================================
//  Imports / Packages
//
//  ADD PACKAGES HERE (And Explain Brief Function & Usage)
//  
//  - Express (Server Functionality)
//  - Cors (Allow Mobile Apps To Connect)
//  - MySQL2 (For MySQL Database Connectivity)
//  - Dotenv (For .env Configurations And Loading)
//  - BCrypt (For Encryption & Security)
//  - JWT (For User/Client Auth)
//  
//  
//  IMPORTS/REQUIRES
//
//  - db (Databse Connection)
//  - RegisterRoute (Route For Creating Accounts)
//  - LoginRoute (Route For Login Validation & Auth)
//  - WalletRoute (Route For Getting Wallet Balance)
//  - TransactionsRoute (Route For Getting Transaction History)
//  - CardCashInRoute (Route For Cashing-In Using Cards)
//  - SendMoneyRoute (Route For Sending Money From One User To Another)
//
//  =====================================
const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');
const mysql = require('mysql2');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');


const db = require('./Config/Connector');
const RegisterRoute = require('./Routes/Register');
const LoginRoute = require('./Routes/Login');
const WalletRoute = require('./Routes/Wallet');
const TransactionsRoute = require('./Routes/Transactions');
const CardCashInRoute = require('./Routes/CardCash-In');
const SendMoneyRoute = require('./Routes/SendMoney');


//  =====================================
//  Server Launch Procedures
//  - Server Executes These Instrcutions On Launch
//  =====================================
const app = express();
app.use(cors());
app.use(express.json());

//  =====================================
//  Routes
//  =====================================
app.use('/api/register', RegisterRoute);
app.use('/api/login', LoginRoute);
app.use('/api/wallet', WalletRoute);
app.use('/api/CardCashIn', CardCashInRoute);
app.use('/api/transactions', TransactionsRoute);
app.use('/api/sendmoney', SendMoneyRoute); // Two End Points Inside



//  =====================================
//  Server Config & Launch Confirmation
//  =====================================
const PORT = process.env.PORT || 3000;
app.listen(PORT, '0.0.0.0', () => {
  console.log(`🚀 Server running on port ${PORT}`);
});
