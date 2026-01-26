-- Tabla de usuarios/empresas
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    status BOOLEAN DEFAULT FALSE,
    company_name VARCHAR(255)
);

-- Tabla de perfiles de usuario (enum como texto)
CREATE TABLE user_profiles (
    user_id INTEGER NOT NULL,
    profile VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, profile),
    CONSTRAINT fk_user_profiles_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabla de códigos de verificación
CREATE TABLE verification_code (
    id SERIAL PRIMARY KEY,
    code VARCHAR(255) NOT NULL,
    expiration TIMESTAMP NOT NULL,
    user_id INTEGER NOT NULL,
    CONSTRAINT fk_verification_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabla de clientes
CREATE TABLE customers (
    customer_id VARCHAR(50) PRIMARY KEY,
    age INTEGER NOT NULL,
    gender VARCHAR(20),
    subscription_type VARCHAR(255),
    watch_hours DOUBLE PRECISION,
    last_login_days INTEGER,
    region VARCHAR(255),
    device VARCHAR(255),
    monthly_fee DOUBLE PRECISION,
    payment_method VARCHAR(255),
    number_of_profiles INTEGER,
    avg_watch_time_per_day DOUBLE PRECISION,
    favorite_genre VARCHAR(255),
    user_id INTEGER,
    CONSTRAINT fk_customer_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabla de predicciones (relación uno a uno con customer)
CREATE TABLE predictions (
    id SERIAL PRIMARY KEY,
    resultado VARCHAR(255),
    probabilidad DOUBLE PRECISION,
    factor_principal VARCHAR(255),
    customer_id VARCHAR(50) UNIQUE,
    CONSTRAINT fk_prediction_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);
