-- V10__Change_customer_model.sql
-- Cambiar el modelo de customers para permitir customer_id duplicados por empresa

-- Primero, eliminar la FK en predictions
ALTER TABLE predictions DROP CONSTRAINT IF EXISTS fk_prediction_customer;

-- Quitar PRIMARY KEY de customer_id
ALTER TABLE customers DROP CONSTRAINT customers_pkey;

-- Agregar nueva columna id
ALTER TABLE customers ADD COLUMN id SERIAL;

-- Establecer id como PRIMARY KEY
ALTER TABLE customers ADD CONSTRAINT customers_pkey PRIMARY KEY (id);

-- Cambiar predictions.customer_id de VARCHAR UNIQUE a INTEGER FK a customers.id
ALTER TABLE predictions DROP CONSTRAINT IF EXISTS predictions_customer_id_key;

ALTER TABLE predictions DROP COLUMN customer_id;
ALTER TABLE predictions ADD COLUMN customer_id INTEGER;

ALTER TABLE predictions ADD CONSTRAINT fk_prediction_customer FOREIGN KEY (customer_id) REFERENCES customers(id);