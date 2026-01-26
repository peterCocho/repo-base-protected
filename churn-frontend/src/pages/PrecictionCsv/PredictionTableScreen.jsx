import React, { useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';
import './PredictionTableScreen.css';

const PredictionTableScreen = ({ data = [], onCsvLoaded }) => {
  const fileInputRef = useRef();
  const navigate = useNavigate();
  const [csvSelected, setCsvSelected] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showUpgradeModal, setShowUpgradeModal] = useState(false);

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (!file) {
      setCsvSelected(false);
      return;
    }
    setCsvSelected(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const file = fileInputRef.current.files[0];
    if (!file) return;

    setLoading(true);
    setError('');

    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await api.post(
        '/api/v1/predictions/csv',
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }
      );

      // Procesar la respuesta
      const predictions = response.data.map(pred => ({
        customerId: pred.customer_id,
        prediccion: pred.prediction,
        probabilidad: `${Math.round(pred.probability * 100)}%`,
        mensaje: pred.custom_message
      }));

      if (onCsvLoaded) onCsvLoaded(predictions);
    } catch (err) {
      console.log('Error response:', err.response); // Debug
      if (err.response && (err.response.status === 403 || err.response.status === 402)) {
        // Mostrar modal de upgrade
        setShowUpgradeModal(true);
      } else {
        setError('Error al procesar el CSV: ' + (err.response?.data?.message || err.message));
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="prediction-table-container">
      <h2>Predicciones Múltiples por CSV</h2>
      <form onSubmit={handleSubmit} className='form-control-csv'>
        <input
          type="file"
          accept=".csv"
          ref={fileInputRef}
          className="file-input"
          onChange={handleFileChange}
        />
        <input
          className='csv submit-btn'
          style={{ display: csvSelected ? 'inline-block' : 'none' }}
          type="submit"
          value={loading ? "Procesando..." : "Enviar CSV"}
          disabled={loading}
        />
      </form>
      {error && <div className="error-msg">{error}</div>}
      
      {/* Vista de tabla para desktop */}
      <div className="prediction-table-container desktop-only">
        <table className="prediction-table">
          <thead>        
            <tr>
              <th>ID Cliente</th>
              <th>Predicción</th>
              <th>Probabilidad</th>
              <th>Mensaje</th>
            </tr>
          </thead>
          <tbody>
            {data.length === 0 ? (
              <tr>
                <td colSpan="4" className="no-data">
                  No hay datos para mostrar
                </td>
              </tr>
            ) : (
              data.map((row, idx) => (
                <tr key={idx}>
                  <td>{row.customerId}</td>
                  <td>{row.prediccion}</td>
                  <td>{row.probabilidad}</td>
                  <td>{row.mensaje}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Vista de tarjetas para móvil */}
      <div className="prediction-cards mobile-only">
        {data.length === 0 ? (
          <div className="no-data-card">
            No hay datos para mostrar
          </div>
        ) : (
          data.map((row, idx) => (
            <div key={idx} className="prediction-card">
              <div className="prediction-card-header">
                <span className="prediction-card-id">#{row.customerId}</span>
              </div>
              <div className="prediction-card-body">
                <div className="prediction-card-item">
                  <strong>Predicción:</strong>
                  <span className={`prediction-result ${row.prediccion === 'Churn' ? 'churn' : 'nochurn'}`}>
                    {row.prediccion}
                  </span>
                </div>
                <div className="prediction-card-item">
                  <strong>Probabilidad:</strong>
                  {row.probabilidad}
                </div>
                <div className="prediction-card-item">
                  <strong>Mensaje:</strong>
                  <span className="prediction-message">{row.mensaje}</span>
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Modal de Upgrade */}
      {showUpgradeModal && (
        <div className="upgrade-modal-overlay" onClick={() => setShowUpgradeModal(false)}>
          <div className="upgrade-modal" onClick={(e) => e.stopPropagation()}>
            <div className="upgrade-modal-header">
              <h3>Actualiza a Premium</h3>
              <button className="upgrade-modal-close" onClick={() => setShowUpgradeModal(false)}>×</button>
            </div>
            <div className="upgrade-modal-body">
              <p>Esta función requiere una suscripción premium.</p>
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
            </div>
            <div className="upgrade-modal-footer">
              <button className="btn-secondary" onClick={() => setShowUpgradeModal(false)}>Cancelar</button>
              <button className="btn-primary" onClick={() => navigate('/upgrade')}>Actualizar a Premium</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PredictionTableScreen;
