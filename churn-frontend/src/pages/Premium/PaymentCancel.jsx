import React from 'react';
import { useNavigate } from 'react-router-dom';
import { XCircle } from 'lucide-react';
import './PaymentResult.css';

const PaymentCancel = () => {
  const navigate = useNavigate();

  return (
    <div className="payment-result-container">
      <div className="payment-result-form glass-panel">
        <XCircle size={64} color="#ef4444" />
        <h3>Pago Cancelado</h3>
        <p>El pago ha sido cancelado. No se ha realizado ningún cargo.</p>
        <button className="btn-primary" onClick={() => navigate('/upgrade')}>
          Intentar de Nuevo
        </button>
        <button className="btn-secondary" onClick={() => navigate('/dashboard')}>
          Volver al Dashboard
        </button>
      </div>
    </div>
  );
};

export default PaymentCancel;