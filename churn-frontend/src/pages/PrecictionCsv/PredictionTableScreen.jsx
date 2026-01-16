import React from 'react';
import './PredictionTableScreen.css';

export default function PredictionTableScreen({ data = [] }) {
  return (
    <div className="prediction-table-container">
      <h2>Resultados de Predicción</h2>
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
    </div>
  );
}
