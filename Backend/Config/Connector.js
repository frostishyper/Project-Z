//  =====================================
//  _ (1.0)
//  (PLEASE UPDATE VERSION IF YOUR UPDATING)
//  
//  Digest:
//  Connects To DB 
//  Checks If Connection Is Established
//  Exports Connection For User
//  
//
//  ====================================

const mysql = require('mysql2');
const dotenv = require('dotenv');

dotenv.config();

const db = mysql.createConnection({
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASS,
  database: process.env.DB_NAME
});

db.connect((err) => {
  if (err) {
    console.error('❌ Database connection failed:', err.message);
    process.exit(1);
  }
  console.log('✅ Connected to MySQL database');
});

module.exports = db;
