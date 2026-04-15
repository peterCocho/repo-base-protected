# 📊 InsightCore

Objetivo:

Crear un análisis predictivo que muestre el comportamiento de los usuarios en la plataforma de streaming, con ello se busca detectar las posibles causas que provocan la cancelación de suscripciones.
El modelo entrenado entregará un Dashboard con las variables de mayor peso, para el caso de negocio estas insights ayudarán a tomar las mejores decisiones para retener suscriptores.

## 🚀 Descripción

InsightCore es un MVP desarrollado en un hackathon para predecir la probabilidad de cancelación de clientes en servicios de suscripción (telecomunicaciones, fintech, streaming, e-commerce).

El sistema combina **Data Science (Python, Scikit-learn)** y **Back-End (Java + Spring Boot)** para ofrecer predicciones vía API REST.

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
- **Swagger/OpenAPI** para documentación de APIs

### Machine Learning

- **Python 3.13+** con FastAPI
- **Scikit-learn 1.6.1** para modelos ML
- **Pandas, NumPy** para procesamiento de datos
- **Matplotlib, Seaborn** para visualización de datos
- **Joblib** para serialización de modelos
- **Uvicorn** como servidor ASGI
- **Python-multipart** para manejo de archivos

### DevOps

- **Docker**
- **Git** para control de versiones

---

## 📂 Estructura del proyecto

```
backend-api/             # Backend Java Spring Boot
├── HELP.md              # Documentación de ayuda de Spring Boot
├── mvnw                 # Wrapper de Maven para Unix
├── mvnw.cmd             # Wrapper de Maven para Windows
├── pom.xml              # Configuración de Maven
├── src/
│   ├── main/
│   │   ├── java/com/churninsight_dev/backend_api/
│   │   │   ├── BackendApiApplication.java      # Clase principal de Spring Boot
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java          # Autenticación y registro
│   │   │   │   ├── LoginController.java         # Login adicional
│   │   │   │   ├── UserController.java          # Gestión de usuarios
│   │   │   │   ├── PredictionController.java    # Predicciones individuales
│   │   │   │   ├── PredictionHistoryController.java # Historial y estadísticas
│   │   │   │   ├── PaymentController.java       # Integración PayPal
│   │   │   │   └── VerificationController.java  # Verificación de email
│   │   │   ├── model/                           # Entidades JPA
│   │   │   ├── repository/                      # Repositorios de datos
│   │   │   ├── service/                         # Lógica de negocio
│   │   │   ├── security/                        # Configuración de seguridad
│   │   │   ├── dto/                             # Objetos de transferencia de datos
│   │   │   ├── exception/                       # Manejo de excepciones
│   │   │   └── config/                          # Configuraciones adicionales
│   │   └── resources/
│   │       ├── application.properties           # Configuración principal
│   │       ├── db/migration/                    # Migraciones Flyway
│   │       ├── static/                          # Archivos estáticos
│   │       └── templates/                       # Plantillas (si se usan)
│   └── test/
│       └── java/com/
│           ├── churninsight/                    # Tests de la versión anterior
│           └── churninsight_dev/                # Tests actuales
└── target/                                      # Archivos compilados (generado por Maven)
    ├── classes/
    ├── generated-sources/
    ├── generated-test-sources/
    └── test-classes/
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

---

## 1. Crear la cuenta Gmail
- Accede a [https://accounts.google.com/signup](https://accounts.google.com/signup).
- Completa el formulario con nombre, usuario y contraseña.
- Verifica tu número de teléfono mediante SMS o llamada (Google lo exige para evitar abusos).
- Una vez validado, tu cuenta estará lista.

---

## 2. Activar verificación en dos pasos
- Ve a [https://myaccount.google.com/security](https://myaccount.google.com/security).
- En la sección **“Cómo inicias sesión en Google”**, activa **Verificación en dos pasos**.
- Configura tu teléfono o la aplicación Google Authenticator.

---

## 3. Generar contraseña de aplicación
- Regresa a la página de **Seguridad** de tu cuenta.
- Aparecerá la opción **Contraseñas de aplicaciones**.
- Selecciona:
  - Aplicación: **Correo**
  - Dispositivo: **Otro (Spring Boot)**
- Copia la contraseña de 16 caracteres que te da Google.

---

## 4. Configuración en `application.properties`
Agrega lo siguiente a tu proyecto:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=TU_CORREO_GMAIL
spring.mail.password=CONTRASEÑA_DE_APLICACIÓN
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true


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
# Opción 1: Instalar globalmente (recomendado para desarrollo)
pip install fastapi uvicorn pydantic scikit-learn==1.6.0 joblib numpy pandas python-multipart

# Opción 2: Usando entorno virtual (si prefieres aislamiento)
python -m venv venv
.\venv\Scripts\activate
pip install -r requirements.txt
```

#### Ejecutar el microservicio

```bash
# Opción 1: Usando python -m (recomendado)
python -m uvicorn main:app --reload --host 0.0.0.0 --port 8000

# Opción 2: Usando uvicorn directamente (si está en PATH)
uvicorn main:app --reload --host 0.0.0.0 --port 8000

# Producción (sin reload)
uvicorn main:app --host 0.0.0.0 --port 8000
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
- **Modelo ML entrenado** con scikit-learn (Logistic Regression)
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
### 🏗️ Arquitectura y Flujo de Datos

El sistema sigue una arquitectura de microservicios desacoplada para garantizar escalabilidad y mantenimiento:

* **⚛️ Frontend (React):** Interfaz de usuario moderna con tablero interactivo y formularios de análisis.
* **☕ Backend (Spring Boot):** API REST que gestiona usuarios, autenticación (**Security + JWT**) y coordina con el servicio de ML.
* **🐍 Machine Learning (FastAPI):** Microservicio especializado en predicciones de *churn* usando modelos entrenados (**Scikit-learn**).
* **🐘 Base de Datos (PostgreSQL):** Almacenamiento de datos de clientes, historial de predicciones y usuarios.

#### 🔄 Flujo de Datos (End-to-End)

1.  **Interacción:** El usuario interactúa con el tablero en React para solicitar un análisis.
2.  **Solicitud:** El frontend envía las solicitudes al backend en Java.
3.  **Orquestación:** El Backend consulta estadísticas o envía los datos limpios al microservicio de ML.
4.  **Inferencia:** FastAPI procesa la predicción usando el modelo y devuelve el resultado.
5.  **Resultado:** La información fluye de vuelta al usuario a través del backend, permitiendo visualizar la estrategia recomendada. 

---

## 🚀 Despliegue

### Opción 1: Despliegue Gratuito con Railway (Recomendado)

Railway es ideal para aplicaciones full-stack con múltiples servicios. Soporta Docker y PostgreSQL integrado.

#### Pasos para desplegar:

1. **Crear cuenta en Railway:**
   - Ve a [railway.app](https://railway.app) y regístrate
   - Conecta tu cuenta de GitHub

2. **Crear proyecto:**
   - Haz clic en "New Project" > "Deploy from GitHub repo"
   - Selecciona tu repositorio `ChurnInsight_dev`

3. **Configurar servicios:**

   **Base de datos PostgreSQL:**
   - Railway crea automáticamente una instancia PostgreSQL
   - Copia las variables de entorno generadas

   **Backend (Spring Boot):**
   - Railway detectará automáticamente el Dockerfile
   - Variables de entorno necesarias:
     ```
     SPRING_PROFILES_ACTIVE=production
     SPRING_DATASOURCE_URL=${DATABASE_URL}
     DS_SERVICE_URL=https://ml-service-production.up.railway.app
     JWT_SECRET=tu-jwt-secret-seguro
     ```

   **ML Service (FastAPI):**
   - Railway detectará el Dockerfile
   - Variables de entorno:
     ```
     PORT=8000
     ```

   **Frontend (React):**
   - Railway detectará el Dockerfile
   - Variables de entorno:
     ```
     VITE_API_BASE_URL=https://backend-production.up.railway.app
     ```

4. **Configurar dominios:**
   - Railway asigna automáticamente subdominios gratuitos
   - Para dominio personalizado: Settings > Domains

5. **Desplegar:**
   - Railway desplegará automáticamente al hacer push a main
   - Monitorea logs en el dashboard

### Opción 2: Desarrollo Local con Docker

```bash
# Clonar el repositorio
git clone https://github.com/tu-usuario/ChurnInsight_dev.git
cd ChurnInsight_dev

# Copiar variables de entorno
cp .env.example .env

# Levantar todos los servicios
docker-compose up --build

# Acceder a la aplicación:
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080
# ML Service: http://localhost:8000
# PostgreSQL: localhost:5432
```

### Variables de Entorno Requeridas

Copia `.env.example` a `.env` y configura:

```bash
# Base de datos
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/churninsight
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password

# ML Service
DS_SERVICE_URL=http://localhost:8000

# JWT
JWT_SECRET=genera-un-secret-seguro-aqui

# Email (opcional)
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=tu-email@gmail.com
SPRING_MAIL_PASSWORD=tu-app-password

# Frontend
VITE_API_BASE_URL=http://localhost:8080
```

### Otras Plataformas Gratuitas

- **Render:** Similar a Railway, soporta Docker
- **Fly.io:** Bueno para apps contenerizadas
- **Heroku:** Limitado, pero fácil para principiantes

### Notas Importantes

- Asegúrate de que los puertos (8080, 8000, 3000) estén disponibles
- Para producción, usa variables de entorno seguras
- El modelo ML debe estar en `ml-service/` con los archivos necesarios
- La base de datos PostgreSQL debe tener las migraciones Flyway aplicadas

---

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
