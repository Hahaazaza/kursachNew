-- Сложный безопасный SQL-код
-- Пример использования функций и подзапросов

CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    order_date DATE NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL
);

CREATE TABLE order_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL
);

-- Вставка данных с использованием функций и сложных выражений
INSERT INTO orders (user_id, order_date, total_amount)
VALUES (
    (SELECT id FROM users WHERE username = 'john_doe'),
    CURDATE(),
    (SELECT SUM(price * quantity) FROM order_items WHERE order_id = 1)
);

-- Пример сложного запроса с подзапросами и функциями
SELECT o.order_id, o.order_date, o.total_amount, u.username, u.email
FROM orders o
JOIN users u ON o.user_id = u.id
WHERE o.order_date > DATE_SUB(CURDATE(), INTERVAL 1 MONTH)
AND o.total_amount > (SELECT AVG(total_amount) FROM orders);
