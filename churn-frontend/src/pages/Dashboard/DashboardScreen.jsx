import React, { useState, useEffect } from 'react';
import './DashboardScreen.css';
import { Users, Activity, AlertTriangle } from 'lucide-react';
import { PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';
import axios from 'axios';

const tooltipContentStyle = { backgroundColor: '#1e293b', border: 'none', borderRadius: '8px' };
const tooltipItemStyle = { color: '#fff' };
const tooltipCursor = { fill: 'rgba(255,255,255,0.1)' };

export default function DashboardScreen() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const token = localStorage.getItem('token');
        const response = await axios.get(
          `${import.meta.env.VITE_API_URL || 'http://localhost:8080'}/api/v1/predictions/stats`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setStats(response.data);
      } catch (err) {
        setError('Error al cargar las estadísticas');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  if (loading) {
    return (
      <div className="dashboard-page-container">
        <div className="loading">Cargando estadísticas...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="dashboard-page-container">
        <div className="error">{error}</div>
      </div>
    );
  }

  const pieData = [
    { name: 'Retenidos', value: stats.cantidad_retenidos, color: '#10b981' },
    { name: 'En Riesgo', value: stats.cantidad_churn, color: '#ef4444' },
  ];

  const variableTranslations = {
    watch_hours: 'HORAS DE VISUALIZACIÓN',
    last_login_days: 'DÍAS DESDE ÚLTIMO LOGIN',
    monthly_fee: 'TARIFA MENSUAL',
    number_of_profiles: 'NÚMERO DE PERFILES',
  };

  const barData = Object.entries(stats.cantidad_salida_variables).map(([key, value]) => ({
    name: variableTranslations[key] || key.replace('_', ' ').toUpperCase(),
    value: value,
  }));


  // Junta las 3 KPI y las 3 "tarjetas" de abajo en un solo array para el grid
  const cards = [
    // KPI cards
    { type: 'kpi', title: 'Clientes Analizados', val: stats.clientes_analizados, icon: Users, color: '#3b82f6' },
    { type: 'kpi', title: 'Tasa de Churn', val: `${stats.tasa_churn.toFixed(2)}%`, icon: Activity, color: '#ef4444' },
    { type: 'kpi', title: 'Ingresos en Riesgo', val: `$${stats.ingresos_en_riesgo.toFixed(2)}`, icon: AlertTriangle, color: '#f59e0b' },
    // Chart/Table cards
    { type: 'pie' },
    { type: 'bar' },
    { type: 'table' },
  ];

  return (
    <div className="dashboard-page-container">
      <div className="dashboard-kpi-grid">
        {cards.map((card, idx) => {
          if (card.type === 'kpi') {
            return (
              <div key={idx} className="glass-panel kpi-card">
                <div className="kpi-icon" style={{ background: card.color }}>
                  <card.icon size={24} color="white" />
                </div>
                <div>
                  <div className="kpi-title">{card.title}</div>
                  <div className="kpi-value">{card.val}</div>
                </div>
              </div>
            );
          }
          if (card.type === 'pie') {
            return (
              <div key={idx} className="glass-panel dashboard-chart-panel">
                <h3 className="dashboard-chart-title">Estado de la Cartera</h3>
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie data={pieData} innerRadius={60} outerRadius={80} paddingAngle={5} dataKey="value">
                      {pieData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.color} />
                      ))}
                    </Pie>
                    <Tooltip contentStyle={tooltipContentStyle} itemStyle={tooltipItemStyle}/>
                  </PieChart>
                </ResponsiveContainer>
              </div>
            );
          }
          if (card.type === 'bar') {
            return (
              <div key={idx} className="glass-panel dashboard-chart-panel">
                <h3 className="dashboard-chart-title">Motivos de Cancelación</h3>
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={barData}>
                    <XAxis dataKey="name" stroke="#94a3b8" angle={-45} textAnchor="end" />
                    <YAxis stroke="#94a3b8" />
                    <Tooltip cursor={tooltipCursor} contentStyle={tooltipContentStyle} itemStyle={tooltipItemStyle}/>
                    <Bar dataKey="value" fill="#3b82f6" radius={[4, 4, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            );
          }
          if (card.type === 'table') {
            return (
              <div key={idx} className="glass-panel dashboard-table-panel">
                <h3 className="dashboard-table-title">⚠️ Top Riesgo</h3>
                <table className="dashboard-table">
                  <thead>
                    <tr className="dashboard-table-header">
                      <th className="dashboard-table-th">Cliente</th>
                      <th className="dashboard-table-th">Prob.</th>
                      <th className="dashboard-table-th">Mensaje</th>
                    </tr>
                  </thead>
                  <tbody>
                    {stats.top5_predicciones.map((pred, i) => (
                      <tr key={i} className="dashboard-table-row">
                        <td className="dashboard-table-td">{pred.customer_id}</td>
                        <td className="dashboard-table-td dashboard-table-prob">{(pred.probabilidad * 100).toFixed(0)}%</td>
                        <td className="dashboard-table-td">{pred.mensaje || 'N/A'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            );
          }
          return null;
        })}
      </div>
    </div>
  );
}