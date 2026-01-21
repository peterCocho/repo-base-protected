import React, { useState } from 'react';
import './Navbar.css';
import { Activity, LogOut } from 'lucide-react';

export default function Navbar({ currentScreen, setCurrentScreen, onLogout }) {
  const [menuOpen, setMenuOpen] = useState(false);
  const screens = [
    { key: 'analyzer', label: 'Analizador' },
    { key: 'dashboard', label: 'Estadisticas' },
    { key: 'history', label: 'Historial' },
    { key: 'prediction', label: 'Predicciones múltiples' }
  ];
  return (
    <nav className="navbar">
      <div className="navbar__brand">
        <Activity color="#3b82f6" /> InsightCore
      </div>
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
      <button className="navbar__logout" onClick={onLogout}>
        <LogOut size={20} />
      </button>
      {/* Menú hamburguesa para móviles */}
      <button className="navbar__hamburger" onClick={() => setMenuOpen(!menuOpen)}>
        <span>&#9776;</span>
      </button>
      {menuOpen && (
        <div className="navbar__mobile-menu">
          {screens.map(screen => (
            <button 
              key={screen.key}
              className={`navbar__link navbar__mobile-link${currentScreen === screen.key ? ' active' : ''}`}
              onClick={() => { setCurrentScreen(screen.key); setMenuOpen(false); }}
            >
              {screen.label}
            </button>
          ))}
          <button className="navbar__logout navbar__mobile-logout" onClick={onLogout}>
            <LogOut size={20} /> Cerrar sesión
          </button>
        </div>
      )}
    </nav>
  );
}
