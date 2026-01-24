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
import VerificationScreen from './pages/Verification/VerificationScreen';
import UpgradeToPremium from './pages/UpgradeToPremium';
import PaymentSuccess from './pages/PaymentSuccess';
import PaymentCancel from './pages/PaymentCancel';
import { BrowserRouter as Router, Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';

function AppRoutes({ isLoggedIn, setIsLoggedIn, currentScreen, setCurrentScreen, predictionData, setPredictionData }) {
  const location = useLocation();
  const navigate = useNavigate();
  // Si no está logueado y la ruta es /, forzar a /login
  React.useEffect(() => {
    // Siempre forzar a /login si no hay sesión y la ruta no es /register, /verify o /login
    if (!isLoggedIn && location.pathname !== '/login' && location.pathname !== '/register' && location.pathname !== '/verify') {
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
            : <LoginScreen onLogin={() => { setIsLoggedIn(true); setCurrentScreen('analyzer'); setPredictionData([]); }} />
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
      <Route
        path="/verify"
        element={
          !isLoggedIn
            ? <VerificationScreen />
            : <Navigate to="/dashboard" replace />
        }
      />
      <Route
        path="/upgrade"
        element={
          isLoggedIn ? <UpgradeToPremium /> : <Navigate to="/login" replace />
        }
      />
      <Route
        path="/payment/success"
        element={
          isLoggedIn ? <PaymentSuccess /> : <Navigate to="/login" replace />
        }
      />
      <Route
        path="/payment/cancel"
        element={
          isLoggedIn ? <PaymentCancel /> : <Navigate to="/login" replace />
        }
      />
      <Route
        path="/payment/cancel"
        element={<PaymentCancel />}
      />
      {/* Rutas protegidas solo si está logueado */}
      <Route
        path="/dashboard"
        element={
          isLoggedIn ? (
            <div>
              <Navbar currentScreen={currentScreen} setCurrentScreen={setCurrentScreen} onLogout={() => {
                localStorage.removeItem('token');
                localStorage.removeItem('userEmail');
                localStorage.removeItem('isLoggedIn');
                setIsLoggedIn(false);
                setPredictionData([]); // Limpiar datos de predicciones al cerrar sesión
              }} />
              <div className="main-content">
                {currentScreen === 'analyzer' && <AnalyzerScreen />}
                {currentScreen === 'dashboard' && <DashboardScreen />}
                {currentScreen === 'history' && <HistoryScreen />}
                {currentScreen === 'prediction' && (
                  <PredictionTableScreen
                    data={predictionData}
                    onCsvLoaded={(predictions) => {
                      setPredictionData(predictions);
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
  // Función para verificar si el token es válido
  const isTokenValid = () => {
    const token = localStorage.getItem('token');
    if (!token) return false;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Date.now() / 1000;
      return payload.exp > currentTime;
    } catch (error) {
      return false;
    }
  };

  const [isLoggedIn, setIsLoggedIn] = useState(() => {
    // Verificar si hay token válido y isLoggedIn es true
    return localStorage.getItem('isLoggedIn') === 'true' && isTokenValid();
  });
  const [currentScreen, setCurrentScreen] = useState(() => isLoggedIn ? 'analyzer' : 'analyzer');
  const [predictionData, setPredictionData] = useState([]);

  // Limpiar sesión si el token ha expirado
  useEffect(() => {
    if (!isTokenValid() && localStorage.getItem('isLoggedIn') === 'true') {
      localStorage.removeItem('token');
      localStorage.removeItem('userEmail');
      localStorage.removeItem('isLoggedIn');
      setIsLoggedIn(false);
    }
  }, []);

  // Actualiza localStorage cuando cambia el estado de login
  useEffect(() => {
    localStorage.setItem('isLoggedIn', isLoggedIn);
  }, [isLoggedIn]);

  // Resetear currentScreen a 'analyzer' y limpiar predictionData cuando el usuario inicia sesión
  useEffect(() => {
    if (isLoggedIn) {
      setCurrentScreen('analyzer');
      setPredictionData([]); // Limpiar datos de predicciones anteriores
    } else {
      setCurrentScreen('analyzer'); // También resetear cuando no está logueado
    }
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

