import React, { useState, useEffect } from 'react';
import './App.css';
import axios from 'axios';
import Navbar from './components/layout/Navbar';
import LoginScreen from './pages/Login/LoginScreen';
import AnalyzerScreen from './pages/Analyzer/AnalyzerScreen';
import DashboardScreen from './pages/Dashboard/DashboardScreen';
import HistoryScreen from './pages/History/HistoryScreen';
import PredictionTableScreen from './pages/PrecictionCsv/PredictionTableScreen';
import RegisterScreen from './pages/Register/RegisterScreen';
import { BrowserRouter as Router, Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';

function AppRoutes({ isLoggedIn, setIsLoggedIn, currentScreen, setCurrentScreen, predictionData, setPredictionData }) {
  const location = useLocation();
  const navigate = useNavigate();
  // Si no está logueado y la ruta es /, forzar a /login
  React.useEffect(() => {
    // Siempre forzar a /login si no hay sesión y la ruta no es /register
    if (!isLoggedIn && location.pathname !== '/login' && location.pathname !== '/register') {
      navigate('/login', { replace: true });
    }
  }, [isLoggedIn, location.pathname, navigate]);

  return (
    <Routes>
      {/* Redirigir la raíz a /login si no está logueado */}
      <Route path="/" element={<Navigate to={isLoggedIn ? "/dashboard" : "/login"} replace />} />
      <Route
        path="/login"
        element={
          isLoggedIn
            ? <Navigate to="/dashboard" replace />
            : <LoginScreen onLogin={() => setIsLoggedIn(true)} />
        }
      />
      <Route
        path="/register"
        element={
          !isLoggedIn
            ? <RegisterScreen />
            : <Navigate to="/dashboard" replace />
        }
      />
      {/* Rutas protegidas solo si está logueado */}
      <Route
        path="/dashboard"
        element={
          isLoggedIn ? (
            <div>
              <Navbar currentScreen={currentScreen} setCurrentScreen={setCurrentScreen} onLogout={() => {
                localStorage.removeItem('token');
                setIsLoggedIn(false);
              }} />
              <div className="main-content">
                {currentScreen === 'analyzer' && <AnalyzerScreen />}
                {currentScreen === 'dashboard' && <DashboardScreen />}
                {currentScreen === 'history' && <HistoryScreen />}
                {currentScreen === 'prediction' && (
                  <PredictionTableScreen
                    data={predictionData}
                    onCsvLoaded={(csvText) => {
                      // Aquí deberías procesar el CSV y convertirlo a datos para la tabla
                      // Por ahora, solo simula una predicción de ejemplo
                      // TODO: Reemplazar por lógica real de parseo y petición a modelo
                      setPredictionData([
                        { prediccion: 'Sí', probabilidad: '85%', mensaje: 'Alerta: riesgo alto' },
                        { prediccion: 'No', probabilidad: '20%', mensaje: 'Sin riesgo' }
                      ]);
                    }}
                  />
                )}
              </div>
            </div>
          ) : (
            <Navigate to="/login" replace />
          )
        }
      />
      {/* Cualquier otra ruta redirige a login */}
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}

export default function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(() => {
    // Lee el estado de login desde localStorage al iniciar
    return localStorage.getItem('isLoggedIn') === 'true';
  });
  const [currentScreen, setCurrentScreen] = useState('analyzer');
  const [predictionData, setPredictionData] = useState([]);

  // Actualiza localStorage cuando cambia el estado de login
  useEffect(() => {
    localStorage.setItem('isLoggedIn', isLoggedIn);
  }, [isLoggedIn]);

  return (
    <Router>
      <AppRoutes
        isLoggedIn={isLoggedIn}
        setIsLoggedIn={setIsLoggedIn}
        currentScreen={currentScreen}
        setCurrentScreen={setCurrentScreen}
        predictionData={predictionData}
        setPredictionData={setPredictionData}
      />
    </Router>
  );
}

