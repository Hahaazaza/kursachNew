-- Сложная потенциально опасная SQL-инъекция
-- Пример использования непараметризованного запроса с функциями и подзапросами

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Вставка данных с использованием непараметризованного запроса и функций
INSERT INTO users (username, password, email) VALUES ('john_doe', 'secure_password', 'john@example.com');
INSERT INTO users (username, password, email) VALUES ('john_doe', 'hacked'); UPDATE users SET password = 'hacked' WHERE username = 'admin'; --', 'hacked@example.com');

-- Пример сложного запроса с подзапросами и функциями
SELECT u.username, u.email, o.total_amount
FROM users u
JOIN (
    SELECT user_id, SUM(price * quantity) AS total_amount
    FROM order_items
    GROUP BY user_id
) o ON u.id = o.user_id
WHERE u.created_at > DATE_SUB(CURDATE(), INTERVAL 1 MONTH);
