-- Script para asignar géneros aleatorios a customers sin gender definido
-- Ejecutar en PostgreSQL para el usuario test1@example.com

-- Primero, ver la distribución actual
SELECT
    CASE
        WHEN c.gender IS NULL THEN 'NULL'
        ELSE c.gender
    END as gender_value,
    COUNT(*) as count
FROM customers c
LEFT JOIN users u ON u.id = c.user_id
WHERE u.email = 'test1@example.com'
GROUP BY c.gender;

-- Actualizar customers con gender NULL asignando géneros aleatorios
-- Usamos ROW_NUMBER y MOD para distribuir aproximadamente 50/50
WITH customers_to_update AS (
    SELECT
        c.id,
        CASE
            WHEN ROW_NUMBER() OVER (ORDER BY c.id) % 2 = 1 THEN 'M'
            ELSE 'F'
        END as random_gender
    FROM customers c
    LEFT JOIN users u ON u.id = c.user_id
    WHERE u.email = 'test1@example.com' AND c.gender IS NULL
)
UPDATE customers
SET gender = customers_to_update.random_gender
FROM customers_to_update
WHERE customers.id = customers_to_update.id;

-- Verificar el resultado final
SELECT
    c.gender,
    COUNT(*) as count
FROM customers c
LEFT JOIN users u ON u.id = c.user_id
WHERE u.email = 'test1@example.com'
GROUP BY c.gender
ORDER BY c.gender;