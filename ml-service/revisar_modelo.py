
import joblib
import sys
try:
    import sklearn
    print(f"scikit-learn versión instalada: {sklearn.__version__}")
except ImportError:
    print("scikit-learn no está instalado.")
    sklearn = None

try:
    modelo = joblib.load("hackaton_churn_v1.pkl")
    print(type(modelo))
except ModuleNotFoundError as e:
    print(f"ModuleNotFoundError: {e}")
    print("Falta instalar o definir el módulo/clase:", e.name)
except AttributeError as e:
    print(f"AttributeError: {e}")
    print("Puede que falte una clase personalizada o un problema de versión de scikit-learn. Revisa el mensaje anterior.")
    if sklearn:
        print(f"Versión actual de scikit-learn: {sklearn.__version__}")
        print("Si el modelo fue entrenado con otra versión, instala la versión correcta, por ejemplo:")
        print("pip install scikit-learn==1.6.1")
except Exception as e:
    print(f"Error al cargar el modelo: {e}")
print(modelo)

def sumar():
    """ suma 2 numeros"""
    return 2 + 2

print(sumar().__doc__)
