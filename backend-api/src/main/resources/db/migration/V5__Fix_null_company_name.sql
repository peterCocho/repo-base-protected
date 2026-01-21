-- V5__Fix_null_company_name.sql

-- Elimina predicciones cuyos clientes no están asociados a ningún usuario
DELETE FROM predictions
WHERE customer_id IN (
	SELECT customer_id FROM customers WHERE user_id IS NULL
);
