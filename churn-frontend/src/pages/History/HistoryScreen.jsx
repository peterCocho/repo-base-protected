import React from 'react';
import { Download } from 'lucide-react';
import './HistoryScreen.css';

export default function HistoryScreen() {
  const data = [
    { id: '001', date: '2023-10-25', result: 'Churn', prob: '85%' },
    { id: '002', date: '2023-10-24', result: 'No Churn', prob: '12%' },
    { id: '003', date: '2023-10-24', result: 'Churn', prob: '78%' },
    { id: '004', date: '2023-10-23', result: 'No Churn', prob: '5%' },
    { id: '005', date: '2023-10-23', result: 'Churn', prob: '92%' },
  ];
  return (
    <div className="page-container">
      <div className="glass-panel history-panel-padding">
        <div className="history-header">
          <h2 className="history-title">Historial de Predicciones</h2>
          <div className="history-actions">
            <button className="input-glass history-filter-btn">Filtro: Alto Riesgo</button>
            <button className="btn-primary history-export-btn">
              <Download size={16} /> Exportar CSV
            </button>
          </div>
        </div>
        <table className="history-table">
          <thead>
            <tr className="history-table-header">
              <th className="history-table-th">ID Cliente</th>
              <th className="history-table-th">Fecha</th>
              <th className="history-table-th">Resultado</th>
              <th className="history-table-th">Probabilidad</th>
              <th className="history-table-th">Fecha</th>
              <th className="history-table-th">Resultado</th>
              <th className="history-table-th">Probabilidad</th>
            </tr>
          </thead>
          <tbody>
            {data.map((row) => (
              <tr key={row.id} className="history-table-row">
                <td className="history-table-td">#{row.id}</td>
                <td className="history-table-td">{row.date}</td>
                <td className="history-table-td">
                  <span className={`history-result${row.result === 'Churn' ? ' churn' : ' nochurn'}`}>{row.result}</span>
                </td>
                <td className="history-table-td">{row.prob}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
