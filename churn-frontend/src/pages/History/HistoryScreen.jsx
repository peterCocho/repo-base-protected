import React, { useState, useEffect } from 'react';
import { Download } from 'lucide-react';
import './HistoryScreen.css';
import axios from 'axios';

export default function HistoryScreen() {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filterHighRisk, setFilterHighRisk] = useState(false);

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const token = localStorage.getItem('token');
        const response = await axios.get(
          `${import.meta.env.VITE_API_URL || 'http://localhost:8080'}/api/predictions/history`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setHistory(response.data);
        console.log('Datos del historial:', response.data);
      } catch (err) {
        setError('Error al cargar el historial de predicciones');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchHistory();
  }, []);
  if (loading) {
    return (
      <div className="page-container">
        <div className="loading">Cargando historial de predicciones...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="page-container">
        <div className="error">{error}</div>
      </div>
    );
  }

  // Filtrar datos según el filtro activo
  const filteredHistory = filterHighRisk 
    ? history.filter(row => (row.resultado || '').toLowerCase().trim() === 'churn')
    : history;

  // Función para exportar a CSV
  const exportToCSV = () => {
    if (filteredHistory.length === 0) {
      alert('No hay datos para exportar');
      return;
    }

    // Headers del CSV
    const headers = ['ID Cliente', 'Fecha', 'Resultado', 'Probabilidad', 'Mensaje'];
    
    // Convertir datos a filas CSV
    const csvRows = filteredHistory.map(row => {
      const fechaFormateada = row.fecha_prediccion 
        ? new Date(row.fecha_prediccion).toLocaleDateString('es-ES')
        : 'Fecha inválida';
      
      const resultado = (row.resultado || '').toLowerCase().trim() === 'churn' ? 'Churn' : 'No Churn';
      const probabilidad = row.probabilidad !== undefined ? (row.probabilidad * 100).toFixed(1) + '%' : 'N/A';
      const mensaje = row.custom_message || 'N/A';

      // Escapar comas y comillas en los valores
      const escapeCSV = (value) => {
        if (typeof value === 'string' && (value.includes(',') || value.includes('"') || value.includes('\n'))) {
          return `"${value.replace(/"/g, '""')}"`;
        }
        return value;
      };

      return [
        escapeCSV(row.customerId),
        escapeCSV(fechaFormateada),
        escapeCSV(resultado),
        escapeCSV(probabilidad),
        escapeCSV(mensaje)
      ].join(',');
    });

    // Combinar headers y filas
    const csvContent = [headers.join(','), ...csvRows].join('\n');
    
    // Crear blob y descargar
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    
    if (link.download !== undefined) {
      const url = URL.createObjectURL(blob);
      link.setAttribute('href', url);
      link.setAttribute('download', `historial_predicciones_${new Date().toISOString().split('T')[0]}.csv`);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  };

  return (
    <div className="page-container">
      <div className="glass-panel history-panel-padding">
        <div className="history-header">
          <h2 className="history-title">Historial de Predicciones</h2>
          <div className="history-actions">
            <button 
              className="input-glass history-filter-btn"
              onClick={() => setFilterHighRisk(!filterHighRisk)}
            >
              {filterHighRisk ? 'Mostrar Todos' : 'Filtro: Alto Riesgo'}
            </button>
            <button className="btn-primary history-export-btn" onClick={exportToCSV}>
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
              <th className="history-table-th">Mensaje</th>
            </tr>
          </thead>
          <tbody>
            {filteredHistory.map((row, index) => {
              console.log('Procesando fila:', row);

              // Mejor manejo de fechas
              let fechaFormateada = 'Fecha inválida';
              if (row.fecha_prediccion) {
                try {
                  const fecha = new Date(row.fecha_prediccion);
                  if (!isNaN(fecha.getTime())) {
                    fechaFormateada = fecha.toLocaleDateString('es-ES');
                  }
                } catch (e) {
                  console.error('Error parseando fecha:', row.fecha_prediccion, e);
                }
              }

              // Determinar si es churn
              const resultado = (row.resultado || '').toLowerCase().trim();
              const esChurn = resultado === 'churn' || resultado === '1';

              return (
                <tr key={index} className="history-table-row">
                  <td className="history-table-td">#{row.customerId}</td>
                  <td className="history-table-td">{fechaFormateada}</td>
                  <td className="history-table-td">
                    <span className={`history-result${esChurn ? ' churn' : ' nochurn'}`}>
                      {esChurn ? 'Churn' : 'No Churn'}
                    </span>
                  </td>
                  <td className="history-table-td">
                    {row.probabilidad !== undefined ? (row.probabilidad * 100).toFixed(1) + '%' : 'N/A'}
                  </td>
                  <td className="history-table-td history-message">
                    {row.custom_message || 'N/A'}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}
