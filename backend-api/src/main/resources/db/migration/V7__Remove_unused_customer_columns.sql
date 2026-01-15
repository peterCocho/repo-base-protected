-- V7__Remove_unused_customer_columns.sql
-- Elimina columnas innecesarias de la tabla customers, dejando solo las relevantes

ALTER TABLE customers DROP COLUMN IF EXISTS age;
ALTER TABLE customers DROP COLUMN IF EXISTS gender;
ALTER TABLE customers DROP COLUMN IF EXISTS subscription_type;
ALTER TABLE customers DROP COLUMN IF EXISTS region;
ALTER TABLE customers DROP COLUMN IF EXISTS device;
ALTER TABLE customers DROP COLUMN IF EXISTS payment_method;
ALTER TABLE customers DROP COLUMN IF EXISTS avg_watch_time_per_day;
ALTER TABLE customers DROP COLUMN IF EXISTS favorite_genre;
