CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    last_name VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

INSERT INTO users (first_name, last_name, login, password) VALUES
('admin', 'admin', 'admin', 'admin'),
('user', 'user', 'user', 'user');

ALTER TABLE users
    ADD COLUMN role VARCHAR(255) NOT NULL DEFAULT 'ROLE_USER';

UPDATE users SET role = 'ROLE_ADMIN' WHERE login = 'admin';
UPDATE users SET role = 'ROLE_USER' WHERE login = 'user';