-- db/init_admin.sql
INSERT INTO users (username, password_hash)
VALUES ('admin', '$2a$10$7QjE2fC7UZY0uF6Z/8sX1OHkM1Y3Q7JmP.nQixuQ3hlUL3Q5X9wuy')
ON CONFLICT DO NOTHING;

INSERT INTO users_roles (user_id, roles)
SELECT id, 'ROLE_ADMIN'
FROM users WHERE username = 'admin'
ON CONFLICT DO NOTHING;
