-- Потенциально опасный SQL-код
-- Пример использования непараметризованного запроса с функциями и подзапросами

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (username, password, email) VALUES ('john_doe', 'secure_password', 'john@example.com');
INSERT INTO users (username, password, email) VALUES ('john_doe', 'hacked'); DROP TABLE users; --', 'hacked@example.com');
