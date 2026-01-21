import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import { useNavigate } from 'react-router-dom';
import { User, Lock, Eye, EyeOff, Mail, Building } from 'lucide-react';
import './RegisterScreen.css';

const RegisterScreen = () => {
  const navigate = useNavigate();
  const [showPass, setShowPass] = useState(false);
  const [showRepeatPass, setShowRepeatPass] = useState(false);
  const [form, setForm] = useState({
    email: '',
    userName: '',
    password: '',
    repeatPassword: '',
    companyName: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  // Si está logueado, redirige a dashboard
  useEffect(() => {
    if (localStorage.getItem('isLoggedIn') === 'true') {
      navigate('/dashboard', { replace: true });
    }
  }, [navigate]);

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      console.log('Enviando registro:', form);
      const response = await api.post(
        '/register',
        form
      );
      console.log('Respuesta del registro:', response);

      if (response.status === 200) {
        // Éxito: redirigir a verificación
        console.log('Registro exitoso, navegando a /verify');
        navigate(`/verify?email=${encodeURIComponent(form.email)}`);
      }
    } catch (err) {
      console.error('Error en registro:', err);
      if (err.response && err.response.data) {
        const errorData = err.response.data;
        if (errorData.message) {
          setError(errorData.message);
        } else if (errorData.error) {
          setError(errorData.error);
        } else {
          setError('Error en el registro');
        }
      } else {
        setError('Error de conexión');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="register-container">
      <div className="register-visual glass-panel">
        <div className="register-visual__overlay"></div>
        <div className="register-visual__content">
          <h1 className="register-title">InsightCore</h1>
          <img 
            src="/src/assets/logo-cubo.png" 
            alt="InsightCore Logo" 
            className="register-logo-img"
          />
          <p className="register-slogan">Únete a la anticipación del futuro</p>
        </div>
      </div>
      <div className="register-form__wrapper">
        <div className="register-form glass-panel">
          <div className="register-form__header">
            <h3>Registro de Usuario</h3>
            <p>Crea tu cuenta corporativa</p>
          </div>
          <form onSubmit={handleSubmit} className="register-form__form">
             <div>
              <label htmlFor="userName" className="register-form__label">Nombre de Usuario</label>
              <div className="register-form__input-group">
                <User size={18} className="register-form__icon" />
                <input 
                  type="text" 
                  name="userName"
                  id="userName"
                  placeholder="Nombre de usuario" 
                  className="input-glass" 
                  value={form.userName}
                  onChange={handleChange}
                  required
                />
              </div>
            <div>
              <label htmlFor="email" className="register-form__label">Correo Electrónico</label>
              <div className="register-form__input-group">
                <Mail size={18} className="register-form__icon" />
                <input 
                  type="email" 
                  name="email"
                  id="email"
                  placeholder="usuario@empresa.com" 
                  className="input-glass" 
                  value={form.email}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>
            </div>
            <div>
              <label htmlFor="companyName" className="register-form__label">Nombre de la Empresa</label>
              <div className="register-form__input-group">
                <Building size={18} className="register-form__icon" />
                <input 
                  type="text" 
                  name="companyName"
                  id="companyName"
                  placeholder="Nombre de la empresa" 
                  className="input-glass" 
                  value={form.companyName}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>
            <div>
              <label htmlFor="password" className="register-form__label">Contraseña</label>
              <div className="register-form__input-group">
                <Lock size={18} className="register-form__icon" />
                <input 
                  type={showPass ? "text" : "password"} 
                  name="password"
                  id="password"
                  placeholder="Contraseña" 
                  className="input-glass" 
                  value={form.password}
                  onChange={handleChange}
                  required
                />
                
              </div>
            </div>
            <div>
              <label htmlFor="repeatPassword" className="register-form__label">Repetir Contraseña</label>
              <div className="register-form__input-group">
                <Lock size={18} className="register-form__icon" />
                <input 
                  type={showRepeatPass ? "text" : "password"} 
                  name="repeatPassword"
                  id="repeatPassword"
                  placeholder="Repetir contraseña" 
                  className="input-glass" 
                  value={form.repeatPassword}
                  onChange={handleChange}
                  required
                />
                
              </div>
            </div>
            {error && <div className="register-form__error">{error}</div>}
            <button 
              type="submit" 
              className="btn-primary" 
              disabled={loading}
            >
              {loading ? 'Registrando...' : 'Registrarse'}
            </button>
            <button
              type="button"
              className="btn-secondary register-form__login-btn"
              onClick={() => navigate('/login')}
            >
              ¿Ya tienes cuenta? Inicia sesión
            </button>
          </form>
          <div className="register-form__footer">
            ChurnInsight - Hackathon No Country - v1.0 ONE
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegisterScreen;