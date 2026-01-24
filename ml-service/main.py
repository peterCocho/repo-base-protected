from typing import Optional
import os
import warnings
from fastapi import FastAPI, UploadFile, File
from pydantic import BaseModel
import joblib
import numpy as np
import pandas as pd
import pickle
import io

# Suprimir warnings de Pydantic sobre métodos obsoletos
warnings.filterwarnings("ignore", category=DeprecationWarning, module="pydantic")


# Inicializa la aplicación FastAPI
app = FastAPI()


# Custom unpickler para reparar modelos con problemas de versión
class FixedUnpickler(pickle.Unpickler):
    def find_class(self, module, name):
        # Redirigir importaciones problemáticas si es necesario
        return super().find_class(module, name)


def safe_load_model(model_path):
    """Carga el modelo y repara problemas de compatibilidad"""
    try:
        # Intenta carga normal primero
        model_data = joblib.load(model_path)
        pipeline = model_data["model"] if isinstance(model_data, dict) else model_data
        
        # Reparar atributos faltantes en LogisticRegression
        if hasattr(pipeline, 'named_steps'):
            classifier = pipeline.named_steps.get('classifier')
            if classifier and hasattr(classifier, 'coef_'):
                # Asegurar que tienen los atributos necesarios
                if not hasattr(classifier, 'multi_class'):
                    classifier.multi_class = 'auto'
                if not hasattr(classifier, '_sag_mark_initialize'):
                    classifier._sag_mark_initialize = None
        
        return pipeline
    except Exception as e:
        print(f"[WARNING] Error en carga normal: {e}")
        raise


# Carga el modelo entrenado desde el archivo pickle

MODEL_PATH = os.path.join(os.path.dirname(__file__), "hackaton_churn_v2.pkl")
try:
    pipeline = safe_load_model(MODEL_PATH)
    print(f"[DEBUG] Tipo de pipeline cargado: {type(pipeline)}")
    
    # Si es GridSearchCV, extraer el estimador entrenado
    if hasattr(pipeline, "best_estimator_"):
        print("[INFO] Extrayendo best_estimator_ de GridSearchCV")
        pipeline = pipeline.best_estimator_
        # Reparar después de extraer
        if hasattr(pipeline, 'named_steps'):
            classifier = pipeline.named_steps.get('classifier')
            if classifier and not hasattr(classifier, 'multi_class'):
                classifier.multi_class = 'auto'
    
    print("[INFO] Modelo cargado exitosamente")
            
except Exception as e:
    print(f"[ERROR] Error al cargar el modelo: {e}")
    import traceback
    traceback.print_exc()
    raise


def predict_proba_safe(model, X):
    """Predice probabilidades de forma segura, manejando errores de compatibilidad"""
    try:
        return model.predict_proba(X)
    except AttributeError as e:
        if "multi_class" in str(e):
            print(f"[WARNING] Error de multi_class, usando método alternativo: {e}")
            # Intentar usar el método directo del clasificador
            if hasattr(model, 'named_steps'):
                classifier = model.named_steps.get('classifier')
                preprocessor = model.named_steps.get('preprocessor')
                if classifier and preprocessor:
                    X_transformed = preprocessor.transform(X)
                    # Intentar fijar el atributo faltante
                    if not hasattr(classifier, 'multi_class'):
                        classifier.multi_class = 'auto'
                    try:
                        return classifier.predict_proba(X_transformed)
                    except Exception as e2:
                        print(f"[ERROR] Método alternativo falló: {e2}")
                        # Último recurso: usar predict con confianza ficticia
                        pred = classifier.predict(X_transformed)
                        # Crear matriz de probabilidades falsa (0 o 1)
                        proba = np.zeros((len(pred), 2))
                        proba[:, 1] = pred
                        proba[:, 0] = 1 - pred
                        return proba
            raise
        raise


# Si es un wrapper (GridSearchCV, etc.) desempaquetar el pipeline final
try:
    if hasattr(pipeline, "best_estimator_"):
        pipeline = pipeline.best_estimator_
except Exception:
    pass

# Asegurar compatibilidad: si el clasificador falta atributos nuevos, inicializarlos
try:
    if hasattr(pipeline, "named_steps") and "classifier" in pipeline.named_steps:
        classifier = pipeline.named_steps["classifier"]
        if not hasattr(classifier, "multi_class"):
            setattr(classifier, "multi_class", "ovr")
except Exception:
    pass


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
    req = request.dict()
    data_row = {col: req.get(col) for col in feature_names}
    data = pd.DataFrame([data_row])
    print("[DEBUG] DataFrame de entrada para predicción:")
    print(data)
    
    # Predecir probabilidad de churn de forma segura
    proba_matrix = predict_proba_safe(pipeline, data)
    proba = np.round(proba_matrix[0][1], 2)
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
    try:
        # Leer el archivo CSV subido
        df = pd.read_csv(file.file)
        print("[DEBUG] Columnas recibidas en CSV:", list(df.columns))
        # Validar que las columnas requeridas estén presentes
        missing_cols = [col for col in feature_names if col not in df.columns]
        if missing_cols:
            print(f"[ERROR] Faltan columnas requeridas: {missing_cols}")
            return {"error": f"Faltan columnas requeridas: {missing_cols}"}
        # Reordenar columnas según feature_names
        df = df[feature_names]
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
    except Exception as e:
        print(f"[ERROR] Error procesando el archivo CSV: {str(e)}")
        return {"error": f"Error procesando el archivo CSV: {str(e)}"}
