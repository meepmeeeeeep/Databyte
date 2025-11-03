CREATE TABLE `cart_items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `transaction_id` varchar(50) DEFAULT NULL,
  `item_id` varchar(50) DEFAULT NULL,
  `item_name` varchar(100) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `vat_type` varchar(255) DEFAULT NULL,
  `vat_inclusive_price` decimal(10,2) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `transaction_id` (`transaction_id`),
  CONSTRAINT `cart_items_ibfk_1` FOREIGN KEY (`transaction_id`) REFERENCES `transaction_history` (`transaction_id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO cart_items (id, transaction_id, item_id, item_name, category, price, vat_type, vat_inclusive_price, quantity) VALUES (8, '110220250001', 'VAT120INC', 'Test Item - With VAT', 'Test', 120.00, 'VATABLE', 134.40, 2);
INSERT INTO cart_items (id, transaction_id, item_id, item_name, category, price, vat_type, vat_inclusive_price, quantity) VALUES (9, '110220250001', 'NOVAT100', 'Test Item - No VAT', 'Test', 100.00, 'ZERO-RATED', 100.00, 3);
INSERT INTO cart_items (id, transaction_id, item_id, item_name, category, price, vat_type, vat_inclusive_price, quantity) VALUES (10, '110220250002', 'NOVAT100', 'Test Item - No VAT', 'Test', 100.00, 'ZERO-RATED', 100.00, 2);
INSERT INTO cart_items (id, transaction_id, item_id, item_name, category, price, vat_type, vat_inclusive_price, quantity) VALUES (11, '110220250002', 'VAT120INC', 'Test Item - With VAT', 'Test', 120.00, 'VATABLE', 134.40, 2);
INSERT INTO cart_items (id, transaction_id, item_id, item_name, category, price, vat_type, vat_inclusive_price, quantity) VALUES (12, '110220250003', 'VAT120INC', 'Test Item - With VAT', 'Test', 120.00, 'VATABLE', 134.40, 4);
INSERT INTO cart_items (id, transaction_id, item_id, item_name, category, price, vat_type, vat_inclusive_price, quantity) VALUES (13, '110220250003', 'NOVAT100', 'Test Item - No VAT', 'Test', 100.00, 'ZERO-RATED', 100.00, 7);
INSERT INTO cart_items (id, transaction_id, item_id, item_name, category, price, vat_type, vat_inclusive_price, quantity) VALUES (14, '110320250001', 'NOVAT100', 'Test Item - No VAT', 'Test', 100.00, 'ZERO-RATED', 100.00, 7);
INSERT INTO cart_items (id, transaction_id, item_id, item_name, category, price, vat_type, vat_inclusive_price, quantity) VALUES (15, '110320250001', 'VAT120INC', 'Test Item - With VAT', 'Test', 120.00, 'VATABLE', 134.40, 1);
INSERT INTO cart_items (id, transaction_id, item_id, item_name, category, price, vat_type, vat_inclusive_price, quantity) VALUES (16, '110320250002', 'VAT120INC', 'Test Item - With VAT', 'Test', 120.00, 'VATABLE', 134.40, 2);
INSERT INTO cart_items (id, transaction_id, item_id, item_name, category, price, vat_type, vat_inclusive_price, quantity) VALUES (17, '110320250003', 'VAT120INC', 'Test Item - With VAT', 'Test', 120.00, 'VATABLE', 134.40, 2);
INSERT INTO cart_items (id, transaction_id, item_id, item_name, category, price, vat_type, vat_inclusive_price, quantity) VALUES (18, '110320250003', 'NOVAT100', 'Test Item - No VAT', 'Test', 100.00, 'ZERO-RATED', 100.00, 2);
INSERT INTO cart_items (id, transaction_id, item_id, item_name, category, price, vat_type, vat_inclusive_price, quantity) VALUES (19, '110320250004', 'VAT120INC', 'Test Item - With VAT', 'Test', 120.00, 'VATABLE', 134.40, 4);
INSERT INTO cart_items (id, transaction_id, item_id, item_name, category, price, vat_type, vat_inclusive_price, quantity) VALUES (20, '110320250004', 'NOVAT100', 'Test Item - No VAT', 'Test', 100.00, 'ZERO-RATED', 100.00, 2);
INSERT INTO cart_items (id, transaction_id, item_id, item_name, category, price, vat_type, vat_inclusive_price, quantity) VALUES (21, '110320250005', 'VAT120INC', 'Test Item - With VAT', 'Test', 120.00, 'VATABLE', 134.40, 4);
INSERT INTO cart_items (id, transaction_id, item_id, item_name, category, price, vat_type, vat_inclusive_price, quantity) VALUES (22, '110320250006', 'VAT120INC', 'Test Item - With VAT', 'Test', 120.00, 'VATABLE', 134.40, 3);
INSERT INTO cart_items (id, transaction_id, item_id, item_name, category, price, vat_type, vat_inclusive_price, quantity) VALUES (23, '110320250007', 'VAT120INC', 'Test Item - With VAT', 'Test', 120.00, 'VATABLE', 134.40, 3);
INSERT INTO cart_items (id, transaction_id, item_id, item_name, category, price, vat_type, vat_inclusive_price, quantity) VALUES (24, '110320250008', 'RAMHYPX320016225', 'RAM 16GB', 'Computer Parts', 2100.00, 'VATABLE', 2352.00, 2);
INSERT INTO cart_items (id, transaction_id, item_id, item_name, category, price, vat_type, vat_inclusive_price, quantity) VALUES (25, '110320250008', 'MOU125061', 'Mouse Test', 'Peripherals', 750.00, 'VATABLE', 840.00, 1);
INSERT INTO cart_items (id, transaction_id, item_id, item_name, category, price, vat_type, vat_inclusive_price, quantity) VALUES (26, '110320250009', 'LENLEG515ARH', 'LENOVO 15ARH015', 'Laptop', 55000.00, 'VATABLE', 61600.00, 1);

CREATE TABLE `discount_codes` (
  `code` varchar(20) NOT NULL,
  `discount_percentage` decimal(5,2) NOT NULL,
  `valid_from` date NOT NULL,
  `valid_until` date NOT NULL,
  `max_uses` int(11) NOT NULL,
  `current_uses` int(11) DEFAULT 0,
  `is_active` tinyint(1) DEFAULT 1,
  `minimum_purchase` decimal(10,2) NOT NULL DEFAULT 0.00,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO discount_codes (code, discount_percentage, valid_from, valid_until, max_uses, current_uses, is_active, minimum_purchase) VALUES ('SAVE20', 20.00, '2025-01-01', '2025-12-31', 50, 7, true, 0.00);
INSERT INTO discount_codes (code, discount_percentage, valid_from, valid_until, max_uses, current_uses, is_active, minimum_purchase) VALUES ('SPECIAL30', 30.00, '2025-01-01', '2025-12-31', 25, 1, true, 0.00);
INSERT INTO discount_codes (code, discount_percentage, valid_from, valid_until, max_uses, current_uses, is_active, minimum_purchase) VALUES ('WELCOME10', 10.00, '2025-01-01', '2025-12-31', 100, 2, true, 0.00);

CREATE TABLE `inventory` (
  `item_no` int(11) NOT NULL AUTO_INCREMENT,
  `item_id` varchar(100) NOT NULL,
  `item_name` varchar(100) NOT NULL,
  `category` varchar(50) DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `vat_type` varchar(20) DEFAULT 'VATABLE',
  `vat_inclusive_price` decimal(10,2) GENERATED ALWAYS AS (case when `vat_type` = 'VATABLE' then `price` * 1.12 else `price` end) STORED,
  PRIMARY KEY (`item_id`),
  UNIQUE KEY `item_no` (`item_no`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO inventory (item_no, item_id, item_name, category, quantity, price, vat_type) VALUES (8, 'LENLEG515ARH', 'LENOVO 15ARH015', 'Laptop', 4, 55000.00, 'VATABLE');
INSERT INTO inventory (item_no, item_id, item_name, category, quantity, price, vat_type) VALUES (6, 'MOU125061', 'Mouse Test', 'Peripherals', 20, 750.00, 'VATABLE');
INSERT INTO inventory (item_no, item_id, item_name, category, quantity, price, vat_type) VALUES (5, 'NOVAT100', 'Test Item - No VAT', 'Test', 31, 100.00, 'ZERO-RATED');
INSERT INTO inventory (item_no, item_id, item_name, category, quantity, price, vat_type) VALUES (7, 'RAMHYPX320016225', 'RAM 16GB', 'Computer Parts', 9, 2100.00, 'VATABLE');
INSERT INTO inventory (item_no, item_id, item_name, category, quantity, price, vat_type) VALUES (4, 'VAT120INC', 'Test Item - With VAT', 'Test', 14, 120.00, 'VATABLE');

CREATE TABLE `resupply_history` (
  `resupply_id` int(11) NOT NULL AUTO_INCREMENT,
  `item_id` varchar(20) NOT NULL,
  `item_name` varchar(100) NOT NULL,
  `quantity` int(11) NOT NULL,
  `supplier_name` varchar(100) NOT NULL,
  `supplier_address` varchar(200) DEFAULT NULL,
  `supplier_contact` varchar(50) DEFAULT NULL,
  `unit_cost` decimal(10,2) NOT NULL,
  `total_cost` decimal(10,2) NOT NULL,
  `resupply_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`resupply_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO resupply_history (resupply_id, item_id, item_name, quantity, supplier_name, supplier_address, supplier_contact, unit_cost, total_cost, resupply_date) VALUES (6, 'NOVAT100', 'Test Item - No VAT', 19, 'Test Supplier', '', '', 75.00, 1425.00, '2025-11-02 18:34:45.0');
INSERT INTO resupply_history (resupply_id, item_id, item_name, quantity, supplier_name, supplier_address, supplier_contact, unit_cost, total_cost, resupply_date) VALUES (7, 'VAT120INC', 'Test Item - With VAT', 5, 'Test 2', '', '', 80.00, 400.00, '2025-11-02 18:35:06.0');
INSERT INTO resupply_history (resupply_id, item_id, item_name, quantity, supplier_name, supplier_address, supplier_contact, unit_cost, total_cost, resupply_date) VALUES (8, 'VAT120INC', 'Test Item - With VAT', 12, 'Supplier', '', '', 100.00, 1200.00, '2025-11-03 00:38:32.0');
INSERT INTO resupply_history (resupply_id, item_id, item_name, quantity, supplier_name, supplier_address, supplier_contact, unit_cost, total_cost, resupply_date) VALUES (9, 'VAT120INC', 'Test Item - With VAT', 10, 'Test', '', '', 20.00, 200.00, '2025-11-03 01:50:39.0');
INSERT INTO resupply_history (resupply_id, item_id, item_name, quantity, supplier_name, supplier_address, supplier_contact, unit_cost, total_cost, resupply_date) VALUES (10, 'NOVAT100', 'Test Item - No VAT', 10, 'Testset', '', '', 50.00, 500.00, '2025-11-03 01:50:49.0');
INSERT INTO resupply_history (resupply_id, item_id, item_name, quantity, supplier_name, supplier_address, supplier_contact, unit_cost, total_cost, resupply_date) VALUES (11, 'VAT120INC', 'Test Item - With VAT', 10, 'test', '', '', 75.00, 750.00, '2025-11-03 01:54:03.0');
INSERT INTO resupply_history (resupply_id, item_id, item_name, quantity, supplier_name, supplier_address, supplier_contact, unit_cost, total_cost, resupply_date) VALUES (12, 'NOVAT100', 'Test Item - No VAT', 20, 'Test Scroll', '', '', 50.00, 1000.00, '2025-11-03 01:54:23.0');

CREATE TABLE `transaction_history` (
  `transaction_id` varchar(20) NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp(),
  `customer_name` varchar(100) DEFAULT NULL,
  `customer_address` varchar(255) DEFAULT NULL,
  `customer_email` varchar(100) DEFAULT NULL,
  `customer_phone` varchar(20) DEFAULT NULL,
  `payment_amount` decimal(10,2) DEFAULT NULL,
  `payment_method` varchar(20) DEFAULT NULL,
  `discount_code` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO transaction_history (transaction_id, total_price, date, customer_name, customer_address, customer_email, customer_phone, payment_amount, payment_method, discount_code) VALUES ('110220250001', 568.80, '2025-11-02 18:29:19.0', 'John', '', '', '', 570.00, 'Cash', NULL);
INSERT INTO transaction_history (transaction_id, total_price, date, customer_name, customer_address, customer_email, customer_phone, payment_amount, payment_method, discount_code) VALUES ('110220250002', 375.04, '2025-11-02 18:31:54.0', 'Test', '', '', '', 500.00, 'Cash', NULL);
INSERT INTO transaction_history (transaction_id, total_price, date, customer_name, customer_address, customer_email, customer_phone, payment_amount, payment_method, discount_code) VALUES ('110220250003', 990.08, '2025-11-02 18:37:37.0', 'Testing Customer', '', '', '', 1000.00, 'Cash', NULL);
INSERT INTO transaction_history (transaction_id, total_price, date, customer_name, customer_address, customer_email, customer_phone, payment_amount, payment_method, discount_code) VALUES ('110320250001', 667.52, '2025-11-03 00:36:06.0', 'John Smith', 'Address Line 1 aaaaaaaaaaaaaaaaaaaaaaaaaaaaa Address Line 2 aaaaaaaaaaaaaaaaaaaaaaaaaa Address Line 3', 'customeremailtest@test.com', '09123456789', 700.00, 'Cash', 'Save20');
INSERT INTO transaction_history (transaction_id, total_price, date, customer_name, customer_address, customer_email, customer_phone, payment_amount, payment_method, discount_code) VALUES ('110320250002', 215.04, '2025-11-03 00:39:46.0', 'Testing VAT Calculation', '', '', '', 300.00, 'Cash', 'save20');
INSERT INTO transaction_history (transaction_id, total_price, date, customer_name, customer_address, customer_email, customer_phone, payment_amount, payment_method, discount_code) VALUES ('110320250003', 375.04, '2025-11-03 01:34:07.0', 'Testing Payment Details PDF', '', '', '', 500.00, 'Cash', 'save20');
INSERT INTO transaction_history (transaction_id, total_price, date, customer_name, customer_address, customer_email, customer_phone, payment_amount, payment_method, discount_code) VALUES ('110320250004', 590.08, '2025-11-03 01:45:48.0', 'Test Payment Details PDF', '', '', '', 600.00, 'Cash', 'save20');
INSERT INTO transaction_history (transaction_id, total_price, date, customer_name, customer_address, customer_email, customer_phone, payment_amount, payment_method, discount_code) VALUES ('110320250005', 537.60, '2025-11-03 01:47:34.0', 'Test Payment PDF', '', '', '', 600.00, 'Cash', NULL);
INSERT INTO transaction_history (transaction_id, total_price, date, customer_name, customer_address, customer_email, customer_phone, payment_amount, payment_method, discount_code) VALUES ('110320250006', 403.20, '2025-11-03 01:51:21.0', 'Test Payment PDF - Again', '', '', '', 500.00, 'Cash', NULL);
INSERT INTO transaction_history (transaction_id, total_price, date, customer_name, customer_address, customer_email, customer_phone, payment_amount, payment_method, discount_code) VALUES ('110320250007', 403.20, '2025-11-03 01:55:37.0', 'John', '', '', '', 500.00, 'Cash', NULL);
INSERT INTO transaction_history (transaction_id, total_price, date, customer_name, customer_address, customer_email, customer_phone, payment_amount, payment_method, discount_code) VALUES ('110320250008', 4989.60, '2025-11-03 15:02:08.0', 'PC Builder', '', '', '', 5000.00, 'Cash', 'welcome10');
INSERT INTO transaction_history (transaction_id, total_price, date, customer_name, customer_address, customer_email, customer_phone, payment_amount, payment_method, discount_code) VALUES ('110320250009', 61600.00, '2025-11-03 15:37:27.0', 'Dino', '', '', '', 62000.00, 'Cash', NULL);

CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `contact_number` varchar(20) DEFAULT NULL,
  `role` enum('ADMIN','MANAGER','CASHIER','STOCK CLERK') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO users (id, username, password, email, contact_number, role) VALUES (1, 'admin', 'admin', NULL, NULL, 'ADMIN');
INSERT INTO users (id, username, password, email, contact_number, role) VALUES (47, 'cashier', 'cashier', 'cashier@databye.com', '09123456789', 'CASHIER');
INSERT INTO users (id, username, password, email, contact_number, role) VALUES (48, 'manager', 'manager', 'manager@databye.com', '12345678901', 'MANAGER');
INSERT INTO users (id, username, password, email, contact_number, role) VALUES (49, 'stockclerk', 'stockclerk', 'stock@databye.com', '12345678901', 'STOCK CLERK');

