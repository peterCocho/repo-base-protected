from typing import Optional
import os
from fastapi import FastAPI, UploadFile, File
from pydantic import BaseModel
import joblib
import numpy as np
import pandas as pd


# Inicializa la aplicación FastAPI
app = FastAPI()


# Carga el modelo entrenado desde el archivo pickle

MODEL_PATH = os.path.join(os.path.dirname(__file__), "hackaton_churn_v2.pkl")
pipeline = joblib.load(MODEL_PATH)["model"]
print(f"[DEBUG] Tipo de pipeline cargado: {type(pipeline)}")
if hasattr(pipeline, "best_estimator_"):
    pipeline = pipeline.best_estimator_


# Lista de features en el mismo orden que usó el equipo de Data Science
feature_names = [
    "watch_hours", "last_login_days", "number_of_profiles", "monthly_fee"
]
feature_names = [
    "age", "watch_hours", "last_login_days", "number_of_profiles", "monthly_fee",
    "avg_watch_time_per_day", "subscription_type", "region", "device", "payment_method", "favorite_genre", "gender"
]


# Modelo de datos para la petición individual de predicción
class PredictionRequest(BaseModel):
    customer_id: Optional[str] = None  # Opcional, solo para referencia
    watch_hours: float
    last_login_days: int
    number_of_profiles: int
    monthly_fee: float
    age: Optional[int] = None
    avg_watch_time_per_day: Optional[float] = None
    subscription_type: Optional[str] = None
    region: Optional[str] = None
    device: Optional[str] = None
    payment_method: Optional[str] = None
    favorite_genre: Optional[str] = None
    gender: Optional[str] = None


# Endpoint para predicción individual
# Recibe un JSON con los datos de un cliente y retorna la predicción, probabilidad y el factor principal
@app.post("/predict")
def predict(request: PredictionRequest):
    # Convertir la petición a un DataFrame para el pipeline, excluyendo customer_id
    data = pd.DataFrame(
        [{k: v for k, v in request.dict().items() if k != 'customer_id'}])
    print("[DEBUG] DataFrame de entrada para predicción:")
    req = request.dict()
    data_row = {col: req.get(col) for col in feature_names}
    data = pd.DataFrame([data_row])
    print(data)
    # Predecir probabilidad de churn
    # proba = pipeline.predict_proba(data)[0][1]
    proba = np.round(pipeline.predict_proba(data)[0][1], 2)
    # proba = np.round(pipeline.predict_proba(data.values)[0][1], 2)
    pred = int(proba >= 0.5)
    # Calcular el main_factor (feature más influyente)
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
    monthly_fee = request.monthly_fee if hasattr(
        request, 'monthly_fee') else None
    return {
        "prediction": "Churn" if pred == 1 else "No Churn",
        "probability": proba,
        "mainFactor": main_factor,
        "monthlyFee": monthly_fee
    }


# Endpoint para predicción masiva por CSV
# Recibe un archivo CSV con múltiples registros y retorna una lista de predicciones
@app.post("/predict-csv")
async def predict_csv(file: UploadFile = File(...)):
    # Leer el archivo CSV subido
    df = pd.read_csv(file.file)
    print("[DEBUG] Columnas recibidas en CSV:", list(df.columns))
    # Validar que las columnas requeridas estén presentes
    missing_cols = [col for col in feature_names if col not in df.columns]
    if missing_cols:
        print(f"[ERROR] Faltan columnas requeridas: {missing_cols}")
        return {"error": f"Faltan columnas requeridas: {missing_cols}"}
    # Validar tipos y valores nulos
    for col in feature_names:
        if df[col].isnull().any():
            print(f"[ERROR] Columna '{col}' contiene valores nulos en el CSV")

    # Predecir probabilidades y clases para todos los registros
    probas = pipeline.predict_proba(df)[:, 1]
    preds = (probas >= 0.5).astype(int)

    # Calcular main_factor para cada fila
    results = []
    for i, row in df.iterrows():
        try:
            classifier = pipeline.named_steps['classifier']
            preprocessor = pipeline.named_steps['preprocessor']
            X_trans = preprocessor.transform([row.values])
            importancias = np.abs(classifier.coef_[0] * X_trans[0])
            idx_max = np.argmax(importancias)
            # Obtener nombres de las columnas transformadas
            try:
                feature_names_out = preprocessor.get_feature_names_out()
            except Exception:
                feature_names_out = [str(j) for j in range(X_trans.shape[1])]
            main_factor = feature_names_out[idx_max]
        except Exception as e:
            main_factor = f"No disponible: {e}"
        # Agregar el campo monthly_fee a la respuesta
        monthly_fee = float(
            row["monthly_fee"]) if "monthly_fee" in row else None
        results.append({
            "prediction": int(preds[i]),
            "probability": float(probas[i]),
            "main_factor": main_factor,
            "monthly_fee": monthly_fee
        })
    return {"results": results}
