import React, { useState, useEffect } from 'react';
import VerificationScreen from './VerificationScreen';
import { useNavigate } from 'react-router-dom';
import "../../App.css";


const RegisterScreen = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    email: '',
    userName: '',
    password: '',
    repeatPassword: '',
    companyName: '',
  });
  const [showVerification, setShowVerification] = useState(false);

  // Si está logueado, redirige a dashboard (no debe ver registro)
  useEffect(() => {
    if (localStorage.getItem('isLoggedIn') === 'true') {
      navigate('/dashboard', { replace: true });
    }
    // Si NO está logueado, NO redirigir automáticamente, solo mostrar el registro
  }, [navigate]);

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // Aquí normalmente enviarías los datos al backend y esperarías confirmación
    // Simulamos que el registro fue exitoso y se debe verificar el correo
    setShowVerification(true);
  };

  if (showVerification) {
    return <VerificationScreen onVerified={() => navigate('/login')} />;
  }

  return (
    <div className="main-content d-flex justify-content-center align-items-center min-vh-100">
      <div className="glass-panel p-4 m-3" style={{ width: '100%', maxWidth: '700px' }}>
        <h2 className="text-center mb-2" style={{ color: 'var(--text-primary)', fontWeight: 'bold' }}>
          Únete al Equipo
        </h2>
        <p className="text-center mb-4" style={{ color: 'var(--text-secondary)' }}>
          Crea tu cuenta para acceder al panel de control
        </p>
        <form onSubmit={handleSubmit}>
          <div className="d-grid gap-2 mb-3">
            <button type="button" className="btn-secondary" onClick={() => navigate('/login')}>
              Volver a Login
            </button>
          </div>
          <div className="row">
            <div className="col-md-6 mb-3">
              <label htmlFor="userName" className="form-label" style={{ color: 'var(--text-secondary)' }}>Nombre Completo</label>
              <input
                type="text"
                className="input-glass" 
                id="userName"
                name="userName"
                placeholder="Pedro Contreras"
                value={form.userName}
                onChange={handleChange}
                required
              />
            </div>
            <div className="col-md-6 mb-3">
              <label htmlFor="companyName" className="form-label" style={{ color: 'var(--text-secondary)' }}>Empresa / Organización</label>
              <input
                type="text"
                className="input-glass"
                id="companyName"
                name="companyName"
                placeholder="SENA / Freelance"
                value={form.companyName}
                onChange={handleChange}
                required
              />
            </div>
          </div>
          <div className="mb-3">
            <label htmlFor="email" className="form-label" style={{ color: 'var(--text-secondary)' }}>Correo Electrónico</label>
            <input
              type="email"
              className="input-glass"
              id="email"
              name="email"
              placeholder="peterxmen3@gmail.com"
              value={form.email}
              onChange={handleChange}
              required
            />
          </div>
          <div className="row">
            <div className="col-md-6 mb-3">
              <label htmlFor="password" className="form-label" style={{ color: 'var(--text-secondary)' }}>Contraseña</label>
              <input
                type="password"
                className="input-glass"
                id="password"
                name="password"
                placeholder="••••••••"
                value={form.password}
                onChange={handleChange}
                required
              />
            </div>
            <div className="col-md-6 mb-4">
              <label htmlFor="repeatPassword" className="form-label" style={{ color: 'var(--text-secondary)' }}>Confirmar Contraseña</label>
              <input
                type="password"
                className="input-glass"
                id="repeatPassword"
                name="repeatPassword"
                placeholder="••••••••"
                value={form.repeatPassword}
                onChange={handleChange}
                required
              />
            </div>
          </div>
          <div className="d-grid gap-2 mt-2">
            <button type="submit" className="btn-primary">
              Registrarse
            </button>
          </div>
          <div className="text-center mt-4">
            <small style={{ color: 'var(--text-secondary)' }}>
              ¿Ya tienes cuenta? <a href="/login" style={{ color: 'var(--accent-blue)' }}>Inicia sesión aquí</a>
            </small>
          </div>
        </form>
      </div>
    </div>
  );
};

export default RegisterScreen;