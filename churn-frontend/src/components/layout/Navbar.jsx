import React, { useState } from 'react';
import './Navbar.css';
import { Activity, LogOut } from 'lucide-react';

export default function Navbar({ currentScreen, setCurrentScreen, onLogout }) {
  const [menuOpen, setMenuOpen] = useState(false);
  const screens = [
    { key: 'analyzer', label: 'Analizador' },
    { key: 'dashboard', label: 'Estadísticas' },
    { key: 'history', label: 'Historial' },
    { key: 'prediction', label: 'Predicciones múltiples' }
  ];
  return (
    <nav className="navbar">
      <div className="navbar__container">
      <div 
          className="navbar__brand" 
          onClick={() => setCurrentScreen('analyzer')}
          style={{ cursor: 'pointer' }}
        >
       <img 
            src="/src/assets/Logo-InsightCore-2.png" 
            alt="InsightCore Logo" 
            className="login-logo-img"
          />
      </div>


      <div className="navbar__right-section">
        <div className="navbar__links">
        {screens.map(screen => (
          <button 
            key={screen.key}
            className={`navbar__link${currentScreen === screen.key ? ' active' : ''}`}
            onClick={() => setCurrentScreen(screen.key)}
          >
            {screen.label}
          </button>
        ))}
      </div>
          <div className="navbar__divider"></div>

      
      <button className="navbar__logout" onClick={onLogout}>
        <LogOut size={20} />
      </button>
      </div>
      {/* Menú hamburguesa para móviles */}
      <button className="navbar__hamburger" onClick={() => setMenuOpen(!menuOpen)}>
        <span>&#9776;</span>
      </button>
      {menuOpen && (
        <div className="navbar__mobile-menu">
          {screens.map(screen => (
            <button 
              key={screen.key}
              className={`navbar__link${currentScreen === screen.key ? ' active' : ''}`}
              style={{ display: 'block', width: '100%', marginBottom: '1rem' }}
              onClick={() => { setCurrentScreen(screen.key); setMenuOpen(false); }}
            >
              {screen.label}
            </button>
          ))}
          <button className="navbar__logout" style={{ display: 'block', width: '100%' }} onClick={onLogout}>
            <LogOut size={20} /> Cerrar sesión
          </button>
        </div>
      )}
      </div>
    </nav>
  );
}
