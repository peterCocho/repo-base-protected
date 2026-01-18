import React, { useState } from 'react';
import axios from 'axios';
import './LoginScreen.css';
import { User, Lock, Eye, EyeOff, Activity } from 'lucide-react';

export default function LoginScreen({ onLogin }) {
  const [showPass, setShowPass] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");
    try {
      // Limpia el token anterior antes de login
      localStorage.removeItem('token');
      const response = await axios.post(
        'http://localhost:8080/login',
        { email, password }
      );
      // Si el backend responde con éxito y trae token, guárdalo
      if (response.status === 200 && response.data && response.data.token) {
        localStorage.setItem('token', response.data.token);
        onLogin();
      } else {
        setError('Credenciales no válidas');
      }
    } catch (err) {
      setError('Credenciales no válidas');
      setTimeout(() => setError(''), 3000);
    }
  };

  return (
    <div className="login-container">
      <div className="login-visual glass-panel">
        <div className="login-visual__overlay"></div>
        <div className="login-visual__content">
          {/* <Activity size={80} color="#3b82f6" className="login-logo-icon" /> */}
          <h1 className="login-title">InsightCore</h1>
          <img 
            src="/src/assets/logo-cubo.png" 
            alt="InsightCore Logo" 
            className="login-logo-img"
          />
          <p className="login-slogan">Anticipando el futuro de tus clientes</p>
        </div>
      </div>
      <div className="login-form__wrapper">
        <div className="login-form glass-panel">
          <div className="login-form__header">
            <h3>Acceso Corporativo</h3>
            <p>Ingrese sus credenciales seguras</p>
          </div>
          <form onSubmit={handleLogin} className="login-form__form">
            <div className="login-form__input-group">
              <User size={18} className="login-form__icon" />
              <input 
                type="email" 
                placeholder="usuario@empresa.com" 
                className="input-glass" 
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
            <div className="login-form__input-group">
              <Lock size={18} className="login-form__icon" />
              <input 
                type={showPass ? "text" : "password"} 
                placeholder="Contraseña" 
                className="input-glass" 
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
              <button 
                type="button"
                className="login-form__showpass"
                onClick={() => setShowPass(!showPass)}
              >
                {showPass ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
            {error && <div className="login-form__error">{error}</div>}
            <button 
              type="submit" 
              className="btn-primary" 
              disabled={!email || !password}
            >
              Iniciar Sesión
            </button>
            <button
              type="button"
              className="btn-secondary login-form__register-btn"
              onClick={() => window.location.href = '/register'}
            >
              ¿No tienes cuenta? Regístrate
            </button>
          </form>
          <div className="login-form__footer">
            ChurnInsight - Hackathon No Country - v1.0 ONE
          </div>
        </div>
      </div>
    </div>
  );
}
