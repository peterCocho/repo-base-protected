from typing import Optional
import os
from fastapi import FastAPI
from pydantic import BaseModel
import joblib
import numpy as np
import pandas as pd


app = FastAPI()

MODEL_PATH = os.path.join(os.path.dirname(__file__), "hackaton_churn_v1.pkl")
model_dict = joblib.load(MODEL_PATH)
pipeline = model_dict["model"]

# Lista de features en el mismo orden que usó el equipo de Data Science
feature_names = [
    "age", "watch_hours", "last_login_days", "number_of_profiles", "monthly_fee",
    "avg_watch_time_per_day", "subscription_type", "region", "device", "payment_method", "favorite_genre", "gender"
]


class PredictionRequest(BaseModel):
    customer_id: Optional[str] = None
    age: int
    watch_hours: float
    last_login_days: int
    number_of_profiles: int
    monthly_fee: float
    avg_watch_time_per_day: float
    subscription_type: str
    region: str
    device: str
    payment_method: str
    favorite_genre: str
    gender: str


@app.post("/predict")
def predict(request: PredictionRequest):
    # Convertir la petición a un DataFrame para el pipeline, excluyendo customer_id
    data = pd.DataFrame(
        [{k: v for k, v in request.dict().items() if k != 'customer_id'}])
    # Predecir probabilidad de churn
    proba = pipeline.predict_proba(data)[0][1]
    pred = int(proba >= 0.5)

    # Calcular el main_factor (feature más influyente)
    # Usamos los coeficientes del clasificador y la transformación del preprocesador
    try:
        # Obtener el clasificador y el preprocesador
        classifier = pipeline.named_steps['classifier']
        preprocessor = pipeline.named_steps['preprocessor']
        # Transformar la fila de entrada
        X_trans = preprocessor.transform(data)
        # Obtener coeficientes y calcular importancia
        importancias = np.abs(classifier.coef_[0] * X_trans[0])
        idx_max = np.argmax(importancias)
        # Obtener nombres de las columnas transformadas
        try:
            feature_names_out = preprocessor.get_feature_names_out()
        except Exception:
            feature_names_out = [str(i) for i in range(X_trans.shape[1])]
        main_factor = feature_names_out[idx_max]
    except Exception as e:
        main_factor = f"No disponible: {e}"

    return {"prediction": pred,
            "probability": proba,
            "main_factor": main_factor
            }
