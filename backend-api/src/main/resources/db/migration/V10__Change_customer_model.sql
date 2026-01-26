-- V10__Change_customer_model.sql
-- Limpiar duplicados y agregar constraint de unicidad por empresa

-- Paso 1: Reasignar foreign keys de predictions para que apunten a los customers que mantendremos
-- (el registro con id máximo para cada grupo user_id, customer_id)
UPDATE predictions
SET customer_id = (
    SELECT MAX(c.id)
    FROM customers c
    WHERE c.user_id = (SELECT user_id FROM customers WHERE id = predictions.customer_id)
    AND c.customer_id = (SELECT customer_id FROM customers WHERE id = predictions.customer_id)
)
WHERE customer_id IN (
    SELECT id FROM customers
    WHERE (user_id, customer_id) IN (
        SELECT user_id, customer_id
        FROM customers
        GROUP BY user_id, customer_id
        HAVING COUNT(*) > 1
    )
);

-- Paso 2: Eliminar duplicados de customers (mantener el registro con id más alto)
DELETE FROM customers
WHERE id NOT IN (
    SELECT MAX(id)
    FROM customers
    GROUP BY user_id, customer_id
);

-- Paso 3: Agregar UNIQUE constraint para (user_id, customer_id)
ALTER TABLE customers ADD CONSTRAINT uk_customer_user_customer_id UNIQUE (user_id, customer_id);