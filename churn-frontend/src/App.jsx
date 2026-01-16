import React, { useState } from 'react';
import './App.css';
import Navbar from './components/layout/Navbar';
import LoginScreen from './pages/Login/LoginScreen';
import AnalyzerScreen from './pages/Analyzer/AnalyzerScreen';
import DashboardScreen from './pages/Dashboard/DashboardScreen';
import HistoryScreen from './pages/History/HistoryScreen';
import PredictionTableScreen from './pages/PrecictionCsv/PredictionTableScreen';

export default function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [currentScreen, setCurrentScreen] = useState('analyzer');

  if (!isLoggedIn) {
    return <LoginScreen onLogin={() => setIsLoggedIn(true)} />;
  }

  return (
    <div>
      <Navbar currentScreen={currentScreen} setCurrentScreen={setCurrentScreen} onLogout={() => setIsLoggedIn(false)} />
      <div className="main-content">
        {currentScreen === 'analyzer' && <AnalyzerScreen />}
        {currentScreen === 'dashboard' && <DashboardScreen />}
        {currentScreen === 'history' && <HistoryScreen />}
        {currentScreen === 'prediction' && <PredictionTableScreen />}
      </div>
    </div>
  );
}