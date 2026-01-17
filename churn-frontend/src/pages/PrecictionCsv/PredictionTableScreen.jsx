import React, { useRef, useState } from 'react';
import './PredictionTableScreen.css';

const PredictionTableScreen = ({ data = [], onCsvLoaded }) => {
  const fileInputRef = useRef();
  const [csvSelected, setCsvSelected] = useState(false);

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (!file) {
      setCsvSelected(false);
      return;
    }
    setCsvSelected(true);
    const reader = new FileReader();
    reader.onload = (evt) => {
      const csvText = evt.target.result;
      if (onCsvLoaded) onCsvLoaded(csvText);
    };
    reader.readAsText(file);
  };

  return (
    <div className="prediction-table-container">
      <h2>Resultados de Predicción</h2>
      <form>
        <input
          type="file"
          accept=".csv"
          ref={fileInputRef}
          style={{ marginBottom: '1.5rem' }}
          onChange={handleFileChange}
        />
        <input
          className='csv'
          style={{ display: csvSelected ? 'inline-block' : 'none', marginBottom: '1.5rem', padding: '0.7rem 2.2rem', borderRadius: '1.2rem', fontWeight: 600, fontSize: '1.08rem', background: '#0c3d8d', color: 'white', border: 'none', boxShadow: '0 2px 12px 0 rgba(31, 38, 135, 0.10)', cursor: 'pointer' }}
          type="submit"
          value="Enviar csv"
        />
      </form>
      <table className="prediction-table">
        <thead>        
          <tr>
            <th>Cancela</th>
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
    </div>
  );
};

export default PredictionTableScreen;
