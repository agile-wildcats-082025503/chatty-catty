-- db/init_admin.sql
INSERT INTO users (username, password_hash)
VALUES ('admin', '$2a$10$7QjE2fC7UZY0uF6Z/8sX1OHkM1Y3Q7JmP.nQixuQ3hlUL3Q5X9wuy')
ON CONFLICT DO NOTHING;

-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create the table to store our documents and embeddings
CREATE TABLE IF NOT EXISTS vector_store (
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    content text,
    metadata json,
    embedding vector(768)
);