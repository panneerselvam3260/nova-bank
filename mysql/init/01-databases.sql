CREATE DATABASE IF NOT EXISTS bank_auth;
CREATE DATABASE IF NOT EXISTS bank_account;
CREATE DATABASE IF NOT EXISTS bank_transaction;
GRANT ALL PRIVILEGES ON bank_auth.* TO 'bank'@'%';
GRANT ALL PRIVILEGES ON bank_account.* TO 'bank'@'%';
GRANT ALL PRIVILEGES ON bank_transaction.* TO 'bank'@'%';
FLUSH PRIVILEGES;
