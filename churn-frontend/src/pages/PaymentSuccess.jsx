import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import api from '../services/api';
import { CheckCircle, XCircle } from 'lucide-react';
import './PaymentResult.css';

const PaymentSuccess = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [status, setStatus] = useState('processing'); // processing, success, error
  const [message, setMessage] = useState('');

  useEffect(() => {
    const confirmPayment = async () => {
      const email = searchParams.get('email');
      const paymentId = searchParams.get('paymentId');
      const payerId = searchParams.get('PayerID');

      if (!email) {
        setStatus('error');
        setMessage('Email no proporcionado');
        return;
      }

      try {
        // Confirmar el pago en el backend
        const response = await api.post(
          '/api/payment/confirm',
          { email }
        );

        if (response.status === 200) {
          setStatus('success');
          setMessage('¡Pago confirmado! Tu cuenta ha sido actualizada a Premium.');
          // Actualizar el rol en localStorage o contexto si es necesario
          localStorage.setItem('userRole', 'premium');
          setTimeout(() => navigate('/dashboard'), 3000);
        } else {
          setStatus('error');
          setMessage('Error al confirmar el pago');
        }
      } catch (err) {
        setStatus('error');
        setMessage('Error al procesar el pago: ' + (err.response?.data?.message || err.message));
      }
    };

    confirmPayment();
  }, [searchParams, navigate]);

  return (
    <div className="payment-result-container">
      <div className="payment-result-form glass-panel">
        {status === 'processing' && (
          <>
            <div className="processing-icon">⏳</div>
            <h3>Procesando Pago...</h3>
            <p>Por favor espera mientras confirmamos tu pago.</p>
          </>
        )}

        {status === 'success' && (
          <>
            <CheckCircle size={64} color="#10b981" />
            <h3>Pago Exitoso</h3>
            <p>{message}</p>
            <p>Redirigiendo al dashboard...</p>
          </>
        )}

        {status === 'error' && (
          <>
            <XCircle size={64} color="#ef4444" />
            <h3>Error en el Pago</h3>
            <p>{message}</p>
            <button className="btn-primary" onClick={() => navigate('/upgrade')}>
              Intentar de Nuevo
            </button>
          </>
        )}
      </div>
    </div>
  );
};

export default PaymentSuccess;