INSERT INTO users (user_name, email, password, status, company_name)
VALUES ('admin_pedro', 'peterxmen3@gmail.com', '$2a$12$/dFoFrqYoy1ZuG.oyd8ocuuLgYI39Hqj1HAdxQYxOGj2kMOmgweNq', true, 'Grupo_53');

INSERT INTO user_profiles (user_id, profile)
SELECT id, 'ROLE_ADMIN' FROM users WHERE email = 'peterxmen3@gmail.com';
