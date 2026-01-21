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
      <form onSubmit={handleSubmit}>
        <input
          type="file"
          accept=".csv"
          ref={fileInputRef}
          style={{ marginBottom: '1.5rem' }}
          onChange={handleFileChange}
        />
        <input
          className='csv'
          style={{
            display: csvSelected ? 'inline-block' : 'none',
            marginBottom: '1.5rem',
            padding: '0.7rem 2.2rem',
            borderRadius: '1.2rem',
            fontWeight: 600,
            fontSize: '1.08rem',
            background: '#0c3d8d',
            color: 'white',
            border: 'none',
            boxShadow: '0 2px 12px 0 rgba(31, 38, 135, 0.10)',
            cursor: loading ? 'not-allowed' : 'pointer'
          }}
          type="submit"
          value={loading ? "Procesando..." : "Enviar CSV"}
          disabled={loading}
        />
      </form>
      {error && <div style={{ color: 'red', marginBottom: '1rem' }}>{error}</div>}
      <table className="prediction-table">
        <thead>        
          <tr>
            <th>Predicción</th>
            <th>Probabilidad</th>
            <th>Mensaje</th>
          </tr>
        </thead>
        <tbody>
          {data.length === 0 ? (
            <tr>
              <td colSpan="3" style={{ textAlign: 'center', color: '#888' }}>
                No hay datos para mostrar
              </td>
            </tr>
          ) : (
            data.map((row, idx) => (
              <tr key={idx}>
                <td>{row.prediccion}</td>
                <td>{row.probabilidad}</td>
                <td>{row.mensaje}</td>
              </tr>
            ))
          )}
        </tbody>
      </table>

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
