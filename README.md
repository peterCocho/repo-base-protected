
# 📊 ChurnInsight

## 🚀 Descripción

ChurnInsight es un MVP desarrollado en un hackathon para predecir la probabilidad de cancelación de clientes en servicios de suscripción (telecomunicaciones, fintech, streaming, e-commerce).

El sistema combina **Data Science (Python, scikit-learn)** y **Back-End (Java + Spring Boot)** para ofrecer predicciones vía API REST.

---

## 🛠️ Tecnologías utilizadas

- **Python 3.13+**: Pandas, Matplotlib, Seaborn, scikit-learn, joblib
- **Java 17**: Spring Boot, Maven
- **Herramientas opcionales**: Docker, PostgreSQL/H2, Streamlit

---

## 📂 Estructura del proyecto

```text
data-science/
├── notebook.ipynb        # EDA, features, entrenamiento y métricas
├── model.pkl             # Modelo serializado

backend/
└── src/main/java/com/churninsight/backend/
    ├── ChurnInsightApiApplication.java
    ├── controller/
    ├── dto/
    └── service/
```

---

## ⚙️ Instalación y ejecución

### 1. Clonar repositorio

```bash
git clone https://github.com/peterCocho/repo-base-protected.git
```

### 2. Entrenar modelo y lanzar API

```bash
cd data-science
python train_model.py
```

### 3. Ejecutar el backend

```bash
cd ../backend
mvn spring-boot:run
```

---

## 📡 Endpoint principal

**POST** `/predict`

**Ejemplo de request:**

```json
{
  "tiempo_contrato_meses": 12,
  "retrasos_pago": 2,
  "uso_mensual": 14.5,
  "plan": "Premium"
}
```

**Ejemplo de respuesta:**

```json
{
  "prevision": "Va a cancelar",
  "probabilidad": 0.81
}
```

---

## 👥 Equipo

- **Data Science:** Python, Pandas, scikit-learn
- **Back-End:** Java, Spring Boot
- **Integración:** API REST + modelo serializado

---

## 📜 Licencia

Este proyecto se distribuye bajo la licencia MIT.

---