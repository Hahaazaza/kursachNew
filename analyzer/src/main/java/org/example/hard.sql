-- Сложный безопасный SQL-код
-- Пример использования подзапросов, функций, условий и объединений

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    order_date DATE NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE order_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

-- Вставка данных с использованием функций и сложных выражений
INSERT INTO users (username, password, email) VALUES ('john_doe', 'secure_password', 'john@example.com');
INSERT INTO users (username, password, email) VALUES ('jane_doe', 'another_password', 'jane@example.com');

INSERT INTO orders (user_id, order_date, total_amount)
VALUES (
    (SELECT id FROM users WHERE username = 'john_doe'),
    CURDATE(),
    (SELECT SUM(price * quantity) FROM order_items WHERE order_id = 1)
);

INSERT INTO order_items (order_id, product_id, price, quantity)
VALUES (
    (SELECT order_id FROM orders WHERE user_id = (SELECT id FROM users WHERE username = 'john_doe')),
    1,
    100.00,
    2
);

-- Пример сложного запроса с подзапросами, функциями и условиями
SELECT u.username, u.email, o.order_id, o.order_date, o.total_amount,
       (SELECT SUM(price * quantity) FROM order_items WHERE order_id = o.order_id) AS total_order_amount
FROM users u
JOIN orders o ON u.id = o.user_id
WHERE o.order_date > DATE_SUB(CURDATE(), INTERVAL 1 MONTH)
AND o.total_amount > (SELECT AVG(total_amount) FROM orders)
AND u.username IN (
    SELECT username
    FROM users
    WHERE created_at > DATE_SUB(CURDATE(), INTERVAL 6 MONTHS)
)
ORDER BY o.order_date DESC;

-- Пример сложного запроса с объединениями и условиями
SELECT u.username, u.email, o.order_id, o.order_date, o.total_amount,
       (SELECT SUM(price * quantity) FROM order_items WHERE order_id = o.order_id) AS total_order_amount
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
WHERE o.order_date > DATE_SUB(CURDATE(), INTERVAL 1 MONTH)
AND o.total_amount > (SELECT AVG(total_amount) FROM orders)
AND u.username IN (
    SELECT username
    FROM users
    WHERE created_at > DATE_SUB(CURDATE(), INTERVAL 6 MONTHS)
)
UNION
SELECT u.username, u.email, o.order_id, o.order_date, o.total_amount,
       (SELECT SUM(price * quantity) FROM order_items WHERE order_id = o.order_id) AS total_order_amount
FROM users u
RIGHT JOIN orders o ON u.id = o.user_id
WHERE o.order_date > DATE_SUB(CURDATE(), INTERVAL 1 MONTH)
AND o.total_amount > (SELECT AVG(total_amount) FROM orders)
AND u.username IN (
    SELECT username
    FROM users
    WHERE created_at > DATE_SUB(CURDATE(), INTERVAL 6 MONTHS)
)
ORDER BY o.order_date DESC;
