-- Безопасный SQL-код
-- Пример использования параметризованного запроса с функциями и подзапросами

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (username, password, email) VALUES (?, ?, ?);

-- Пример сложного запроса с подзапросами и функциями
SELECT u.username, u.email, o.total_amount
FROM users u
JOIN (
    SELECT user_id, SUM(price * quantity) AS total_amount
    FROM order_items
    GROUP BY user_id
) o ON u.id = o.user_id
WHERE u.created_at > DATE_SUB(CURDATE(), INTERVAL 1 MONTH);
