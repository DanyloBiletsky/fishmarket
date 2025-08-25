DROP TABLE users;


CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    last_name VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

INSERT INTO users (first_name, last_name, username, password) VALUES
('admin', 'admin', 'admin', 'admin'),
('user', 'user', 'user', 'user');

ALTER TABLE users
    ADD COLUMN role VARCHAR(255) NOT NULL DEFAULT 'ROLE_USER';

UPDATE users SET role = 'ROLE_ADMIN' WHERE username = 'admin';
UPDATE users SET role = 'ROLE_USER' WHERE username = 'user';


UPDATE users
SET password = '{noop}admin'
WHERE username = 'admin';

UPDATE users
SET password = '{noop}user'
WHERE username = 'user';
