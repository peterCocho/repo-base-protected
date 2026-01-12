-- V6__Add_premium_role_to_user.sql
-- Asigna el rol premium a un usuario específico (ajusta el correo si es necesario)

INSERT INTO user_profiles (user_id, profile)
SELECT id, 'ROLE_PREMIUM' FROM users WHERE email = 'usuario4@gmail.com';
