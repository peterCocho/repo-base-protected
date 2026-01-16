import React from 'react';
import './DashboardScreen.css';
import { Users, Activity, AlertTriangle } from 'lucide-react';
import { PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';

export default function DashboardScreen() {
  const pieData = [
    { name: 'Retenidos', value: 750, color: '#10b981' },
    { name: 'En Riesgo', value: 250, color: '#ef4444' },
  ];
  const barData = [
    { name: 'Precio', value: 400 },
    { name: 'Soporte', value: 300 },
    { name: 'Cobertura', value: 200 },
    { name: 'Competencia', value: 100 },
  ];
  // Junta las 3 KPI y las 3 "tarjetas" de abajo en un solo array para el grid
  const cards = [
    // KPI cards
    { type: 'kpi', title: 'Clientes Analizados', val: '1,240', icon: Users, color: '#3b82f6' },
    { type: 'kpi', title: 'Tasa de Churn', val: '23%', icon: Activity, color: '#ef4444' },
    { type: 'kpi', title: 'Ingresos en Riesgo', val: '$45M', icon: AlertTriangle, color: '#f59e0b' },
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
                    <Tooltip contentStyle={{ backgroundColor: '#1e293b', border: 'none', borderRadius: '8px' }} itemStyle={{ color: '#fff' }}/>
                  </PieChart>
                </ResponsiveContainer>
              </div>
            );
          }
          if (card.type === 'bar') {
            return (
              <div key={idx} className="glass-panel dashboard-chart-panel">
                <h3 className="dashboard-chart-title">Motivos de Fuga</h3>
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={barData}>
                    <XAxis dataKey="name" stroke="#94a3b8" />
                    <YAxis stroke="#94a3b8" />
                    <Tooltip cursor={{fill: 'rgba(255,255,255,0.1)'}} contentStyle={{ backgroundColor: '#1e293b', border: 'none', borderRadius: '8px' }} itemStyle={{ color: '#fff' }}/>
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
                      <th className="dashboard-table-th">Acción</th>
                    </tr>
                  </thead>
                  <tbody>
                    {[1,2,3].map(i => (
                      <tr key={i} className="dashboard-table-row">
                        <td className="dashboard-table-td">Empresa {i}</td>
                        <td className="dashboard-table-td dashboard-table-prob">{90 + i}%</td>
                        <td className="dashboard-table-td">
                          <button className="dashboard-table-contact-btn">Contactar</button>
                        </td>
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