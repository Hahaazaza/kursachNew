-- Потенциально вредоносный SQL-код
-- Пример использования непараметризованного запроса
-- Вставка данных с использованием непараметризованного запроса

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL
);

INSERT INTO users (username, password) VALUES ('john_doe', 'secure_password');
INSERT INTO users (username, password) VALUES ('john_doe'); DROP TABLE users; --', 'hacked');