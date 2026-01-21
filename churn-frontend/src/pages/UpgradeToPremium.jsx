import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { Crown, CreditCard } from 'lucide-react';
import './UpgradeToPremium.css';

const UpgradeToPremium = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [showPaymentForm, setShowPaymentForm] = useState(false);
  const [paymentData, setPaymentData] = useState({
    cardNumber: '',
    expiryDate: '',
    cvv: '',
    name: ''
  });

  // Obtener email del usuario logueado
  const email = localStorage.getItem('userEmail');

  const handlePaymentDataChange = (e) => {
    setPaymentData({ ...paymentData, [e.target.name]: e.target.value });
  };

  const handleUpgrade = () => {
    setShowPaymentForm(true);
  };

  const handlePaymentSubmit = async (e) => {
    e.preventDefault();
    if (!email) {
      setError('Usuario no autenticado');
      return;
    }

    // Validación básica
    if (!paymentData.cardNumber || !paymentData.expiryDate || !paymentData.cvv || !paymentData.name) {
      setError('Por favor, completa todos los campos');
      return;
    }

    setLoading(true);
    setError('');

    try {
      // Simular procesamiento de pago
      await new Promise(resolve => setTimeout(resolve, 2000)); // Simular delay

      // Confirmar en backend (simulado)
      await api.post('/api/payment/confirm', { email });
      
      setSuccessMessage('Pago exitoso! Tu cuenta ha sido actualizada a Premium.');
      setTimeout(() => navigate('/dashboard'), 2000); // Redirigir después de 2 segundos
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

        {successMessage && <div className="upgrade-success">{successMessage}</div>}

        {!showPaymentForm ? (
          <button
            className="btn-premium"
            onClick={handleUpgrade}
            disabled={loading}
          >
            <CreditCard size={18} />
            Proceder al Pago
          </button>
        ) : (
          <form onSubmit={handlePaymentSubmit} className="payment-form">
            <h4>Información de Pago</h4>
            <div className="form-group">
              <label>Nombre en la Tarjeta</label>
              <input
                type="text"
                name="name"
                value={paymentData.name}
                onChange={handlePaymentDataChange}
                placeholder="Juan Pérez"
                required
              />
            </div>
            <div className="form-group">
              <label>Número de Tarjeta</label>
              <input
                type="text"
                name="cardNumber"
                value={paymentData.cardNumber}
                onChange={handlePaymentDataChange}
                placeholder="1234 5678 9012 3456"
                required
              />
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>Fecha de Expiración</label>
                <input
                  type="text"
                  name="expiryDate"
                  value={paymentData.expiryDate}
                  onChange={handlePaymentDataChange}
                  placeholder="MM/YY"
                  required
                />
              </div>
              <div className="form-group">
                <label>CVV</label>
                <input
                  type="text"
                  name="cvv"
                  value={paymentData.cvv}
                  onChange={handlePaymentDataChange}
                  placeholder="123"
                  required
                />
              </div>
            </div>
            <button
              type="submit"
              className="btn-premium"
              disabled={loading}
            >
              {loading ? 'Procesando...' : 'Pagar $9.99'}
            </button>
          </form>
        )}

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