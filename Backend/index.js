//  =====================================
//  Backend Version (1.0)
//  (PLEASE UPDATE VERSION IF YOUR UPDATING)
//  
//  FEATURES:
//  #1 - 
//  
//
//  =====================================




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
//  - RegisterRoute
//  
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



//  =====================================
//  Server Config & Launch Confirmation
//  =====================================
const PORT = process.env.PORT || 3000;
app.listen(PORT, '0.0.0.0', () => {
  console.log(`🚀 Server running on port ${PORT}`);
});
