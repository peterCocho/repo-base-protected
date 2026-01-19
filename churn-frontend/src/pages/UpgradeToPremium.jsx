import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { Crown, CreditCard } from 'lucide-react';
import './UpgradeToPremium.css';

const UpgradeToPremium = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Obtener email del usuario logueado (asumiendo que está en localStorage o contexto)
  const email = localStorage.getItem('userEmail'); // Ajustar según tu implementación

  const handleUpgrade = async () => {
    if (!email) {
      setError('Usuario no autenticado');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const token = localStorage.getItem('token');
      const response = await axios.post(
        `${import.meta.env.VITE_API_URL || 'http://localhost:8080'}/api/payment/create`,
        { email },
        {
          headers: token ? { Authorization: `Bearer ${token}` } : {}
        }
      );

      if (response.data.approvalUrl) {
        // Redirigir a PayPal
        window.location.href = response.data.approvalUrl;
      } else {
        setError('Error al iniciar el pago');
      }
    } catch (err) {
      setError('Error al procesar el pago: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  if (!email) {
    return (
      <div className="upgrade-container">
        <div className="upgrade-form glass-panel">
          <h3>Acceso Denegado</h3>
          <p>Debes iniciar sesión para acceder a esta página.</p>
          <button className="btn-primary" onClick={() => navigate('/login')}>
            Ir al Login
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="upgrade-container">
      <div className="upgrade-form glass-panel">
        <div className="upgrade-header">
          <Crown size={64} color="#ffd700" />
          <h3>Actualiza a Premium</h3>
          <p>Desbloquea predicciones múltiples por CSV y funciones avanzadas.</p>
        </div>

        <div className="upgrade-features">
          <h4>Beneficios del Plan Premium:</h4>
          <ul>
            <li>✅ Carga masiva de clientes via CSV</li>
            <li>✅ Estadísticas avanzadas</li>
            <li>✅ Soporte prioritario</li>
            <li>✅ Sin límites de predicciones</li>
          </ul>
          <p className="upgrade-price"><strong>$9.99 USD</strong> (pago único)</p>
        </div>

        {error && <div className="upgrade-error">{error}</div>}

        <button
          className="btn-premium"
          onClick={handleUpgrade}
          disabled={loading}
        >
          {loading ? (
            <>
              <CreditCard size={18} />
              Procesando...
            </>
          ) : (
            <>
              <CreditCard size={18} />
              Pagar con PayPal
            </>
          )}
        </button>

        <button
          className="btn-secondary"
          onClick={() => navigate('/dashboard')}
        >
          Volver al Dashboard
        </button>
      </div>
    </div>
  );
};

export default UpgradeToPremium;