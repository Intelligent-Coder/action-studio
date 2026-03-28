-- Payment Service Sample SQL Queries
-- Use these with H2 Console or MySQL client

-- ============================================
-- USER TABLE QUERIES
-- ============================================

-- View all users
SELECT * FROM users;

-- Count users
SELECT COUNT(*) AS total_users FROM users;

-- Find user by email
SELECT * FROM users WHERE email = 'john.doe@example.com';

-- Find user by phone number
SELECT * FROM users WHERE phone_number = '+1234567890';

-- Get users by city
SELECT * FROM users WHERE city = 'New York';

-- ============================================
-- PAYMENT TABLE QUERIES
-- ============================================

-- View all payments
SELECT * FROM payments;

-- Get payments by user
SELECT * FROM payments WHERE user_id = 1;

-- Get payments by status
SELECT * FROM payments WHERE status = 'COMPLETED';

-- Get pending payments
SELECT * FROM payments WHERE status = 'PENDING';

-- Get completed payments with amounts
SELECT id, transaction_id, amount, currency, status, created_at
FROM payments
WHERE status = 'COMPLETED'
ORDER BY created_at DESC;

-- Get total payment amount by status
SELECT status, COUNT(*) as count, SUM(amount) as total_amount
FROM payments
GROUP BY status;

-- Get total payment amount by user
SELECT u.email, COUNT(p.id) as payment_count, SUM(p.amount) as total_amount
FROM users u
LEFT JOIN payments p ON u.id = p.user_id
GROUP BY u.id, u.email;

-- Get payment by transaction ID
SELECT * FROM payments WHERE transaction_id = '550e8400-e29b-41d4-a716-446655440000';

-- Get payments by payment method
SELECT * FROM payments WHERE payment_method = 'CREDIT_CARD';

-- Get refunded payments
SELECT * FROM payments WHERE status = 'REFUNDED';

-- Get failed payments
SELECT * FROM payments WHERE status = 'FAILED';

-- ============================================
-- TRANSACTION TABLE QUERIES
-- ============================================

-- View all transactions
SELECT * FROM transactions;

-- Get transactions for a payment
SELECT * FROM transactions WHERE payment_id = 1 ORDER BY created_at DESC;

-- Get all transactions by type
SELECT * FROM transactions WHERE transaction_type = 'CAPTURE';

-- Get failed transactions
SELECT * FROM transactions WHERE error_message IS NOT NULL;

-- Get transaction history for a user
SELECT t.*, p.amount, p.currency, u.email
FROM transactions t
JOIN payments p ON t.payment_id = p.id
JOIN users u ON p.user_id = u.id
WHERE u.id = 1
ORDER BY t.created_at DESC;

-- ============================================
-- COMPLEX QUERIES
-- ============================================

-- Get payment details with user information
SELECT
    p.id,
    p.transaction_id,
    u.email,
    u.first_name,
    u.last_name,
    p.amount,
    p.currency,
    p.status,
    p.payment_method,
    p.created_at,
    p.processed_at
FROM payments p
JOIN users u ON p.user_id = u.id
ORDER BY p.created_at DESC;

-- Get payment summary by user
SELECT
    u.id,
    u.email,
    u.first_name,
    u.last_name,
    COUNT(p.id) as total_payments,
    SUM(CASE WHEN p.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_payments,
    SUM(CASE WHEN p.status = 'PENDING' THEN 1 ELSE 0 END) as pending_payments,
    SUM(CASE WHEN p.status = 'COMPLETED' THEN p.amount ELSE 0 END) as total_completed_amount,
    SUM(p.amount) as total_amount
FROM users u
LEFT JOIN payments p ON u.id = p.user_id
GROUP BY u.id, u.email, u.first_name, u.last_name;

-- Get daily payment summary
SELECT
    DATE(created_at) as payment_date,
    COUNT(*) as total_payments,
    SUM(amount) as total_amount,
    AVG(amount) as average_amount,
    MIN(amount) as min_amount,
    MAX(amount) as max_amount
FROM payments
GROUP BY DATE(created_at)
ORDER BY payment_date DESC;

-- Get payment status distribution
SELECT
    status,
    COUNT(*) as count,
    ROUND(100.0 * COUNT(*) / (SELECT COUNT(*) FROM payments), 2) as percentage,
    SUM(amount) as total_amount
FROM payments
GROUP BY status
ORDER BY count DESC;

-- Get most common payment methods
SELECT
    payment_method,
    COUNT(*) as count,
    SUM(amount) as total_amount,
    AVG(amount) as average_amount
FROM payments
GROUP BY payment_method
ORDER BY count DESC;

-- Get users with most payments
SELECT
    u.id,
    u.email,
    u.first_name,
    u.last_name,
    COUNT(p.id) as payment_count,
    SUM(p.amount) as total_spent
FROM users u
LEFT JOIN payments p ON u.id = p.user_id
GROUP BY u.id, u.email, u.first_name, u.last_name
ORDER BY payment_count DESC
LIMIT 10;

-- Get refunded payment details
SELECT
    p.id,
    p.transaction_id,
    u.email,
    p.amount,
    p.currency,
    p.created_at as original_payment_date,
    p.processed_at as refund_date,
    p.description
FROM payments p
JOIN users u ON p.user_id = u.id
WHERE p.status = 'REFUNDED'
ORDER BY p.processed_at DESC;

-- ============================================
-- MAINTENANCE QUERIES
-- ============================================

-- Delete old transactions (older than 30 days)
-- DELETE FROM transactions WHERE created_at < DATE_SUB(NOW(), INTERVAL 30 DAY);

-- Delete cancelled payments (older than 90 days)
-- DELETE FROM payments WHERE status = 'CANCELLED' AND created_at < DATE_SUB(NOW(), INTERVAL 90 DAY);

-- Update old transactions to closed status
-- UPDATE transactions SET description = 'Archived' WHERE created_at < DATE_SUB(NOW(), INTERVAL 6 MONTH);

-- ============================================
-- STATISTICS QUERIES
-- ============================================

-- Overall payment statistics
SELECT
    COUNT(*) as total_transactions,
    SUM(amount) as total_amount,
    AVG(amount) as average_amount,
    MIN(amount) as min_amount,
    MAX(amount) as max_amount,
    COUNT(DISTINCT user_id) as unique_users
FROM payments;

-- Payment success rate
SELECT
    ROUND(100.0 * SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) / COUNT(*), 2) as success_rate_percentage,
    SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_count,
    SUM(CASE WHEN status = 'FAILED' THEN 1 ELSE 0 END) as failed_count,
    COUNT(*) as total_count
FROM payments;

-- Currency distribution
SELECT
    currency,
    COUNT(*) as count,
    SUM(amount) as total_amount,
    AVG(amount) as average_amount
FROM payments
GROUP BY currency
ORDER BY total_amount DESC;

-- ============================================
-- NOTES
-- ============================================
-- For H2 Database:
-- - Access H2 Console at: http://localhost:8080/h2-console
-- - JDBC URL: jdbc:h2:mem:paymentdb
-- - Username: sa
-- - Password: (leave blank)

-- For MySQL:
-- - Connect using MySQL client: mysql -u payment_user -p payment_service
-- - Or use MySQL Workbench, DBeaver, or other tools
