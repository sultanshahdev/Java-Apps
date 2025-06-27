
create database finance_tracker;
use  finance_tracker;
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    weekly_pocket_money DOUBLE NOT NULL,
    savings DOUBLE DEFAULT 0,  -- Updated as leftover is added after each day
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Table to track daily expenses (need or fun)
CREATE TABLE expenses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    expense_date DATE NOT NULL,
    day_num INT NOT NULL, -- 1 = Monday, ... 7 = Sunday (or however you map)
    category ENUM('need','fun') NOT NULL,
    amount DOUBLE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

