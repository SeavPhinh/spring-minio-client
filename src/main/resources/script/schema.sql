CREATE TABLE files
(
    id        SERIAL PRIMARY KEY NOT NULL,
    file_name VARCHAR(50) UNIQUE NOT NULL
);

SELECT * FROM files
WHERE file_name = '40542.jpg'
LIMIT 1