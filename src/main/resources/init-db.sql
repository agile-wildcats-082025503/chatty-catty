CREATE EXTENSION IF NOT EXISTS vector;

\c ragdb

CREATE TABLE documents (id SERIAL PRIMARY KEY, source TEXT, file_path TEXT, content TEXT, embedding TEXT);
