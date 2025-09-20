-- Create database
CREATE DATABASE IF NOT EXISTS sweetshop;

-- Create user if not exists
CREATE USER IF NOT EXISTS 'sweetshop_user'@'localhost' IDENTIFIED BY 'Mysql@1234';

-- Grant all privileges on sweetshop database to the user
GRANT ALL PRIVILEGES ON sweetshop.* TO 'sweetshop_user'@'localhost';

-- Flush privileges to ensure changes take effect
FLUSH PRIVILEGES;

-- Use the database
USE sweetshop;

-- Show tables (should be empty initially)
SHOW TABLES;