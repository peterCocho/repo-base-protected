# 📊 ChurnInsight

Objetivo:

Crear un análisis predictivo que muestre el comportamiento de los usuarios en la plataforma de streaming, con ello se busca detectar las posibles causas que provocan la cancelación de suscripciones.
El modelo entrenado entregará un Dashboard con las variables de mayor peso, para el caso de negocio estas insights ayudarán a tomar las mejores decisiones para retener suscriptores.

## 🚀 Descripción

ChurnInsight es un MVP desarrollado en un hackathon para predecir la probabilidad de cancelación de clientes en servicios de suscripción (telecomunicaciones, fintech, streaming, e-commerce).

El sistema combina **Data Science (Python, scikit-learn)** y **Back-End (Java + Spring Boot)** para ofrecer predicciones vía API REST.

---

## 🛠️ Tecnologías utilizadas

### Frontend

- **React 19.2.0** con Vite 7.2.4
- **Material-UI (@mui/material, @mui/icons-material)** para componentes UI
- **React Router DOM** para navegación
- **Axios** para llamadas HTTP
- **Recharts** para gráficos interactivos
- **React Toastify** para notificaciones
- **CSS** con diseño glassmorphism
- **Query String (qs)** para manejo de parámetros

### Backend

- **Java 17** con Spring Boot 3.2.1
- **Maven** para gestión de dependencias
- **PostgreSQL** como base de datos principal
- **H2 Database** para desarrollo/testing
- **JPA/Hibernate** para ORM
- **JWT (JJWT 0.11.5)** para autenticación
- **Spring Security** para control de acceso
- **Spring Mail** para envío de emails
- **Flyway** para migraciones de base de datos
- **Lombok** para reducción de código boilerplate
- **PayPal SDK** para integración de pagos
- **Swagger/OpenAPI** para documentación de APIs

### Machine Learning

- **Python 3.13+** con FastAPI
- **scikit-learn 1.6.1** para modelos ML
- **Pandas, NumPy** para procesamiento de datos
- **Joblib** para serialización de modelos
- **Uvicorn** como servidor ASGI
- **Python-multipart** para manejo de archivos

### DevOps

- **Docker**
- **Git** para control de versiones

---

## 📂 Estructura del proyecto

```
churn-frontend/          # Frontend React + Vite
├── src/
│   ├── components/
│   ├── pages/
│   │   ├── Dashboard/
│   │   ├── Analyzer/
│   │   ├── Login/
│   │   ├── Register/
│   │   ├── PaymentSuccess/
│   │   ├── PaymentCancel/
│   │   ├── History/
│   │   ├── Premium/
│   │   └── Verification/
│   ├── services/
│   ├── context/
│   ├── theme/
│   └── assets/
├── package.json
├── vite.config.js
└── eslint.config.js

backend-api/             # Backend Java Spring Boot
├── src/main/java/com/churninsight_dev/backend_api/
│   ├── controller/
│   │   ├── AuthController.java          # Autenticación y registro
│   │   ├── LoginController.java         # Login adicional
│   │   ├── UserController.java          # Gestión de usuarios
│   │   ├── PredictionController.java    # Predicciones individuales
│   │   ├── PredictionHistoryController.java # Historial y estadísticas
│   │   ├── PaymentController.java       # Integración PayPal
│   │   └── VerificationController.java  # Verificación de email
│   ├── model/
│   ├── repository/
│   ├── service/
│   ├── security/
│   ├── dto/
│   ├── exception/
│   ├── config/
│   └── .env                           # Variables de entorno (vacío)
├── src/main/resources/
│   ├── application.properties         # Configuración principal
│   └── db/migration/                  # Migraciones Flyway
├── pom.xml
└── mvnw

ml-service/              # Microservicio de ML con FastAPI
├── main.py              # API FastAPI principal
├── hackaton_churn_v2.pkl # Modelo entrenado (actualizado)
├── requirements.txt     # Dependencias Python
├── customers_drama.csv  # Datos de ejemplo
├── update_csv_genders.py # Script para actualizar CSVs
├── migrate_gender_data.py # Script para migrar BD
├── fix_gender_data.py   # Script para corregir datos
└── modelo_InsightCore   # Modelo anterior (deprecated)

feature/                 # Documentación y recursos adicionales
└── bootstrap            # Archivos estáticos
```

---

## ⚙️ Configuración del entorno

### Variables de entorno requeridas

Crear un archivo `.env` en el directorio `backend-api/src/main/java/com/churninsight_dev/backend_api/` con las siguientes variables:

```bash
# Base de datos PostgreSQL
DATABASE_USERNAME_P=postgres
DATABASE_PASSWORD_P=tu_password_postgres
DATABASE_URL_CHURNINSIGHT=jdbc:postgresql://localhost:5432/churninsight

# Servicio de email (para verificación de usuarios)
HOST_EMAIL_TEMP=smtp.gmail.com
PORT_EMAIL_TEMP=587
USERNAME_EMAIL_TEMP=tu_email@gmail.com
PASSWORD_EMAIL_TEMP=tu_app_password

# URL del microservicio de ML
DS_SERVICE_URL=http://localhost:8000/predict

# PayPal (opcional - para pagos premium)
PAYPAL_CLIENT_ID=tu_client_id_sandbox
PAYPAL_CLIENT_SECRET=tu_client_secret_sandbox
```

### Configuración de base de datos

1. **Instalar PostgreSQL** (versión 12+ recomendada)
2. **Crear base de datos:**
   ```sql
   CREATE DATABASE churninsight;
   ```
3. **Crear usuario:**
   ```sql
   CREATE USER postgres WITH PASSWORD 'tu_password';
   GRANT ALL PRIVILEGES ON DATABASE churninsight TO postgres;
   ```

### Configuración de email (Gmail)

1. **Habilitar autenticación de 2 factores** en tu cuenta Gmail
2. **Generar una app password:**
   - Ve a [Google Account Settings](https://myaccount.google.com/)
   - Security → 2-Step Verification → App passwords
   - Genera una contraseña para "Mail"
3. **Usar la app password** en la variable `PASSWORD_EMAIL_TEMP`

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

### 2.1 Configurar y ejecutar el microservicio de ML

```bash
cd ml-service
```

#### Crear el entorno virtual

```bash
python -m venv venv
```

#### Activar el entorno virtual

```bash
# Windows
.\venv\Scripts\Activate

# Linux/Mac
source venv/bin/activate
```

#### Instalar librerías

```bash
python -m pip install fastapi uvicorn pydantic numpy joblib pandas
pip install scikit-learn==1.6.0
pip install python-multipart
```

#### Ejecutar el microservicio

```bash
# Producción
uvicorn main:app --host 0.0.0.0 --port 8000

# Desarrollo (con recarga automática)
.\venv\Scripts\python.exe -m uvicorn main:app --reload
```

#### Probar el endpoint desde el navegador

```
http://localhost:8000/docs
```

### 3. Ejecutar el backend Java

```bash
cd backend-api
mvn spring-boot:run
```

### 4. Ejecutar el frontend

```bash
cd churn-frontend
npm install
npm run dev
```

---

## 📡 Endpoints de la API

### Microservicio de ML (FastAPI)

**URL Base:** `http://localhost:8000`

**POST** `/predict`

- Predicción individual de churn
- **Body:** JSON con datos del cliente

**POST** `/predict-csv`

- Predicciones masivas desde archivo CSV
- **Body:** Form-data con archivo CSV

**GET** `/docs`

- Documentación interactiva de la API (Swagger UI)

### Backend Java (Spring Boot)

**URL Base:** `http://localhost:8080`

#### Autenticación

**POST** `/api/auth/login` - Inicio de sesión
**POST** `/api/auth/register` - Registro de usuario

#### Usuarios

**GET** `/api/users/me` - Información del usuario actual
**POST** `/api/users/admin` - Crear usuario administrador

#### Predicciones

**POST** `/api/v1/predictions/predict` - Nueva predicción individual
**GET** `/api/v1/predictions/health` - Health check del servicio
**GET** `/api/v1/predictions/stats` - Estadísticas del dashboard
**POST** `/api/v1/predictions/csv` - Predicciones masivas desde CSV
**GET** `/api/v1/predictions/debug/genders` - Lista de géneros únicos

### Endpoint principal

**POST** `/predict`

**Ejemplo de request:**

```json
{
	"customer_id": "G061",
	"age": 29,
	"gender": "F",
	"subscription_type": "Standard",
	"watch_hours": 42,
	"last_login_days": 7,
	"region": "South America",
	"device": "Mobile",
	"monthly_fee": 9.99,
	"payment_method": "Credit Card",
	"number_of_profiles": 2,
	"avg_watch_time_per_day": 1.4,
	"favorite_genre": "Drama"
}
```

**Ejemplo de respuesta:**

```json
{
	
  "prediction": "Churn",
  "probability": 70.5,
  "mainFactor": "last_login_days",
  "monthlyFee": 9.99
}
```

#### Historial y Estadísticas

**GET** `/api/predictions/history` - Historial de predicciones del usuario
**GET** `/api/predictions/history/statistics` - Estadísticas avanzadas (premium)

#### Pagos (PayPal)

**POST** `/api/payment/create` - Crear orden de pago
**POST** `/api/payment/capture/{orderId}` - Capturar pago
**POST** `/api/payment/confirm` - Confirmar pago completado

#### Verificación

**POST** `/api/verification` - Verificar código de email
**POST** `/api/send-verification-code` - Enviar código de verificación

#### Documentación

**GET** `/swagger-ui.html` - Documentación Swagger del backend
**GET** `/api-docs` - Especificación OpenAPI

---

## ✨ Funcionalidades principales

### 👤 Sistema de autenticación

- **Registro de usuarios** con verificación de email
- **Inicio de sesión** con JWT tokens
- **Sistema de roles** (USER, PREMIUM, ADMIN)
- **Protección de rutas** con Spring Security

### 📊 Dashboard interactivo

- **Estadísticas en tiempo real** de predicciones de churn
- **Filtros dinámicos** por edad, género, región, dispositivo, tipo de suscripción
- **Gráficos interactivos** con Recharts (pie charts, bar charts)
- **Vista premium** con estadísticas avanzadas del historial

### 🤖 Motor de predicción

- **Predicciones individuales** vía formulario web
- **Predicciones masivas** desde archivos CSV
- **Modelo ML entrenado** con scikit-learn (Random Forest)
- **Probabilidades de churn** con mensajes personalizados

### 📧 Verificación de email

- **Envío automático** de códigos de verificación
- **Validación de usuarios** antes del registro completo
- **Configuración SMTP** para diferentes proveedores

### 🔧 Scripts de mantenimiento

- **update_csv_genders.py**: Actualiza archivos CSV con nuevos formatos de género
- **migrate_gender_data.py**: Migra datos de género en la base de datos
- **fix_gender_data.py**: Corrige inconsistencias en datos de género

---

## 🔄 Flujo de datos

- **Frontend (React):** Interfaz de usuario moderna con dashboard interactivo y formularios de análisis
- **Backend (Spring Boot):** API REST que gestiona usuarios, autenticación y coordina con el servicio de ML
- **Machine Learning (FastAPI):** Microservicio especializado en predicciones de churn usando modelos entrenados
- **Base de Datos (PostgreSQL):** Almacenamiento de datos de clientes, predicciones y usuarios

## 🔄 Flujo de datos

1. **Usuario** interactúa con el dashboard en React
2. **Frontend** envía requests al backend Java
3. **Backend** consulta estadísticas o envía datos al microservicio de ML
4. **FastAPI** procesa la predicción usando el modelo scikit-learn
5. **Resultados** fluyen de vuelta al usuario a través del backend

---

## � Solución de problemas

### Problemas comunes

**Error de conexión a PostgreSQL:**

```bash
# Verificar que PostgreSQL esté corriendo
sudo systemctl status postgresql

# Verificar credenciales en application.properties
# Asegurarse de que la base de datos existe
psql -U postgres -d churninsight
```

**Error al enviar emails:**

```bash
# Verificar configuración SMTP
# Para Gmail: usar app password, no contraseña normal
# Verificar que el puerto 587 no esté bloqueado
```

**Microservicio ML no responde:**

```bash
# Verificar que el puerto 8000 esté disponible
netstat -tulpn | grep :8000

# Verificar logs del microservicio
cd ml-service && python main.py
```

**Error de CORS en el frontend:**

```bash
# Verificar que el backend esté corriendo en el puerto 8080
# Verificar configuración de CORS en Spring Security
```

**Problemas con filtros del dashboard:**

- Los filtros se aplican automáticamente al cambiar valores
- Si no se actualizan, verificar conexión con el backend
- Limpiar filtros reinicia todas las estadísticas

### Logs importantes

**Backend (Spring Boot):**

```bash
cd backend-api && mvn spring-boot:run
# Logs aparecen en la consola
```

**Frontend (Vite):**

```bash
cd churn-frontend && npm run dev
# Logs en la consola del navegador (F12)
```

**ML Service (FastAPI):**

```bash
cd ml-service && uvicorn main:app --reload
# Logs en la terminal
```

---

## 🤝 Cómo contribuir

1. **Fork** el repositorio
2. **Crear una rama** para tu feature: `git checkout -b feature/nueva-funcionalidad`
3. **Hacer commits** descriptivos: `git commit -m "Agrega nueva funcionalidad X"`
4. **Push** a tu rama: `git push origin feature/nueva-funcionalidad`
5. **Crear un Pull Request** con descripción detallada

### Estándares de código

- **Frontend:** ESLint configurado, usar React hooks correctamente
- **Backend:** Seguir convenciones de Spring Boot, usar Lombok
- **ML Service:** Seguir PEP 8, documentar funciones con docstrings
- **Commits:** Usar commits convencionales (feat, fix, docs, etc.)

### Testing

```bash
# Frontend
cd churn-frontend && npm run lint

# Backend
cd backend-api && mvn test

# ML Service
cd ml-service && python -m pytest  # Si se agregan tests
```

---

## �📜 Licencia

Este proyecto se distribuye bajo la licencia MIT.

---
