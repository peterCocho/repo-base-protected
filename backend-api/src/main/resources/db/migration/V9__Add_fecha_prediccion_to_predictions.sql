-- Añadir columna para la fecha de la predicción
ALTER TABLE predictions ADD fecha_prediccion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;