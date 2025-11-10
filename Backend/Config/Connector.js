// DB Connector 
const mysql = require('mysql2');
const dotenv = require('dotenv');

dotenv.config();

// ✅ Create a pool (For Concurrency)
const pool = mysql.createPool({
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASS,
  database: process.env.DB_NAME,
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0,
});

// ✅ Test initial connection
pool.getConnection((err, connection) => {
  if (err) {
    console.error('❌ Database connection failed:', err.message);
    process.exit(1);
  }
  console.log('✅ Connected to MySQL database (via pool)');
  connection.release();
});

//Export the pool (so db.promise() still works)
pool.promisePool = pool.promise();

module.exports = pool;
