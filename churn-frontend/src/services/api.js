import axios from 'axios';

// Configurar la URL base de la API
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

// Crear instancia de axios con configuración base
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Función para verificar si el token JWT ha expirado
const isTokenExpired = (token) => {
  if (!token) return true;
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const currentTime = Date.now() / 1000;
    return payload.exp < currentTime;
  } catch (error) {
    return true;
  }
};

// Función para obtener el token del localStorage
const getToken = () => localStorage.getItem('token');

// Función para limpiar la sesión
const clearSession = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('userEmail');
  localStorage.removeItem('isLoggedIn');
  window.location.href = '/login'; // Redirigir a login
};

// Interceptor de solicitud: agregar token si existe y no ha expirado
api.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token && !isTokenExpired(token)) {
      config.headers.Authorization = `Bearer ${token}`;
    } else if (token && isTokenExpired(token)) {
      // Token expirado, limpiar sesión
      clearSession();
      return Promise.reject(new Error('Token expirado'));
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor de respuesta: manejar errores 401
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Token inválido o expirado, limpiar sesión
      clearSession();
    }
    return Promise.reject(error);
  }
);

export default api;