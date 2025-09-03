-- Tạo bảng customers
CREATE TABLE IF NOT EXISTS customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(45),
    email VARCHAR(45),
    address VARCHAR(45)
);

-- Utility table 0..999
CREATE TABLE IF NOT EXISTS util_numbers (n INT PRIMARY KEY);

TRUNCATE util_numbers;

INSERT INTO util_numbers(n)
SELECT d0.u + 10*d1.u + 100*d2.u
FROM generate_series(0,9) d0(u)
CROSS JOIN generate_series(0,9) d1(u)
CROSS JOIN generate_series(0,9) d2(u)
ORDER BY 1;

-- Function thay cho procedure (Postgres không có WHILE LOOP kiểu MySQL)
CREATE OR REPLACE FUNCTION seed_customers(total INT, batch_size INT)
RETURNS VOID AS $$
DECLARE
    i INT := 1;
BEGIN
    WHILE i <= total LOOP
        INSERT INTO customers(name, email, address)
        SELECT  CONCAT('Customer_', i + n.n),
                CONCAT('customer', i + n.n, '@example.com'),
                CONCAT('Address ', i + n.n, ' Street')
        FROM util_numbers n
        WHERE (i + n.n) <= total
        LIMIT batch_size;
        i := i + batch_size;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Gọi hàm
SELECT seed_customers(100000, 1000);

-- Kiểm tra số lượng
SELECT COUNT(*) FROM customers;
