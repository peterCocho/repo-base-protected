import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate, useLocation, useSearchParams } from 'react-router-dom';
import { Mail, CheckCircle } from 'lucide-react';
import './VerificationScreen.css';

const VerificationScreen = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [code, setCode] = useState('');
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');

  useEffect(() => {
    const emailParam = searchParams.get('email');
    if (emailParam) {
      setEmail(decodeURIComponent(emailParam));
    } else {
      navigate('/register');
    }
  }, [searchParams, navigate]);

  const handleVerify = async (e) => {

    try {
      const response = await axios.post(
        `${import.meta.env.VITE_API_URL || 'http://localhost:8080'}/verification`,
        { email, verification: code }
      );

      if (response.status === 200) {
        setSuccessMessage(response.data.message);
        setSuccess(true);
        setTimeout(() => navigate('/login'), 3000);
      }
    } catch (err) {
      if (err.response && err.response.data) {
        const errorData = err.response.data;
        if (errorData.message) {
          setError(errorData.message);
        } else {
          setError('Código incorrecto');
        }
      } else {
        setError('Error de conexión');
      }
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className="verification-container">
          <div className="verification-form glass-panel">
            <div className="verification-success">
              <CheckCircle size={64} color="#10b981" />
              <h3>Verificación Exitosa</h3>
              <p>{successMessage}</p>
              <p>Redirigiendo al login...</p>
            </div>
          </div>
      </div>
    );
  }

  return (
    <div className="verification-container">
      <div className="verification-form glass-panel">
        <div className="verification-form__header">
          <h3>Verificar Correo</h3>
          <p>Ingresa el código de verificación enviado a {email}</p>
          <p className="verification-form__note">Nota: El código fue enviado automáticamente al registrarte. Si no lo recibes, registra de nuevo.</p>
        </div>
        <form onSubmit={handleVerify} className="verification-form__form">
          <div className="verification-form__input-group">
            <Mail size={18} className="verification-form__icon" />
            <input 
              type="text" 
              placeholder="Código de verificación" 
              className="input-glass" 
              value={code}
              onChange={(e) => setCode(e.target.value)}
              required
              maxLength={6}
            />
          </div>
          {error && <div className="verification-form__error">{error}</div>}
          <button 
            type="submit" 
            className="btn-primary" 
            disabled={loading || !code}
          >
            {loading ? 'Verificando...' : 'Verificar'}
          </button>
          <button
            type="button"
            className="btn-secondary verification-form__back-btn"
            onClick={() => navigate('/register')}
          >
            Volver al Registro
          </button>
        </form>
        <div className="verification-form__footer">
          ChurnInsight - Hackathon No Country - v1.0 ONE
        </div>
      </div>
    </div>
  );
};

export default VerificationScreen;