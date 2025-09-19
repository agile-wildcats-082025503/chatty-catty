CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE documents (
    id SERIAL PRIMARY KEY,
    content TEXT,
    embedding vector(1536)  -- size depends on embedding model
);

CREATE TABLE users (
  id serial PRIMARY KEY,
  username text UNIQUE NOT NULL,
  password_hash text NOT NULL
);
CREATE TABLE users_roles (
  users_id bigint not null,
  roles varchar not null
);

-- Change in production using bcrypt-cli hash yourpassword
INSERT INTO users (username, password_hash)
VALUES ('admin', '$2a$10$7QjE2fC7UZY0uF6Z/8sX1OHkM1Y3Q7JmP.nQixuQ3hlUL3Q5X9wuy') -- bcrypt("admin")
ON CONFLICT DO NOTHING;

INSERT INTO users_roles (user_id, roles)
SELECT id, 'ROLE_ADMIN'
FROM users WHERE username = 'admin'
ON CONFLICT DO NOTHING;
