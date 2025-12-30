CREATE TABLE customers (
    customer_id VARCHAR(50) PRIMARY KEY,
    age INTEGER,
    gender VARCHAR(20),
    subscription_type VARCHAR(50),
    watch_hours DOUBLE PRECISION,
    last_login_days INTEGER,
    region VARCHAR(100),
    device VARCHAR(50),
    monthly_fee DOUBLE PRECISION,
    churned BOOLEAN, -- TRUE si canceló, FALSE si continúa
    payment_method VARCHAR(50),
    number_of_profiles INTEGER,
    avg_watch_time_per_day DOUBLE PRECISION,
    favorite_genre VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);