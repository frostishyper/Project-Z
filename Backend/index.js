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
//  -
//
//  =====================================
const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');
const mysql = require('mysql2');



//  =====================================
//  Server Launch Procedures
//  - Server Executes These Instrcutions On Launch
//  =====================================
const app = express();
app.use(cors());
app.use(express.json());



//  =====================================
//  Server Config & Launch Confirmation
//  =====================================
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
