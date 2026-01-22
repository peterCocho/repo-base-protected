import React, { useState, useEffect } from 'react';
import './DashboardScreen.css';
import { Users, Activity, AlertTriangle, Filter, X } from 'lucide-react';
import { PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';
import axios from 'axios';

const tooltipContentStyle = { backgroundColor: '#1e293b', border: 'none', borderRadius: '8px' };
const tooltipItemStyle = { color: '#fff' };
const tooltipCursor = { fill: 'rgba(255,255,255,0.1)' };

export default function DashboardScreen() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Estadísticas avanzadas para premium
  const [advancedStats, setAdvancedStats] = useState(null);
  const [filters, setFilters] = useState({
    age: '',
    subscriptionType: '',
    region: '',
    device: '',
    gender: ''
  });

  // Estado para determinar si el usuario es premium
  const [isPremium, setIsPremium] = useState(false);

  useEffect(() => {
    const checkUserRole = async () => {
      try {
        const token = localStorage.getItem('token');
        const response = await axios.get(
          `${import.meta.env.VITE_API_URL || 'http://localhost:8080'}/api/users/me`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        
        // Verificar si el usuario tiene el rol PREMIUM
        const userProfiles = response.data.profiles || [];
        const hasPremiumRole = userProfiles.some(profile => 
          profile === 'ROLE_PREMIUM' || profile === 'PREMIUM'
        );
        setIsPremium(hasPremiumRole);
      } catch (err) {
        console.error('Error al obtener información del usuario', err);
        setIsPremium(false); // Por defecto, asumir no premium si hay error
      }
    };

    checkUserRole();
  }, []);

  const fetchStats = async () => {
    const token = localStorage.getItem('token');
    const params = new URLSearchParams();
    Object.entries(filters).forEach(([key, value]) => {
      if (value) params.append(key, value);
    });
    const url = `${import.meta.env.VITE_API_URL || 'http://localhost:8080'}/api/v1/predictions/stats?${params}`;
    const response = await axios.get(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      }
    });
    setStats(response.data);
  };

  const fetchAdvancedStats = async () => {
    const token = localStorage.getItem('token');
    const params = new URLSearchParams();
    Object.entries(filters).forEach(([key, value]) => {
      if (value) params.append(key, value);
    });
    const url = `${import.meta.env.VITE_API_URL || 'http://localhost:8080'}/api/predictions/history/statistics?${params}`;
    const response = await axios.get(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      }
    });
    setAdvancedStats(response.data);
  };

  useEffect(() => {
    const loadInitialData = async () => {
      setLoading(true);
      setError(null);
      try {
        await fetchStats();
        if (isPremium) {
          await fetchAdvancedStats();
        }
      } catch (err) {
        setError('Error al cargar las estadísticas');
        console.error('Error loading initial data:', err);
      } finally {
        setLoading(false);
      }
    };

    if (isPremium !== null) { // Solo cargar cuando sepamos si es premium
      loadInitialData();
    }
  }, [isPremium]); // Se ejecuta cuando se determina si el usuario es premium

  const handleFilterChange = (filterName, value) => {
    setFilters(prev => ({ ...prev, [filterName]: value }));
  };

  const applyFilters = async () => {
    setLoading(true);
    setError(null);
    try {
      await fetchStats(); // Actualizar estadísticas básicas
      if (isPremium) {
        await fetchAdvancedStats(); // Actualizar estadísticas avanzadas si es premium
      }
    } catch (err) {
      console.error('Error al aplicar filtros:', err);
    } finally {
      setLoading(false);
    }
  };

  const clearFilters = async () => {
    setFilters({
      age: '',
      subscriptionType: '',
      region: '',
      device: '',
      gender: ''
    });
    setLoading(true);
    setError(null);
    try {
      await fetchStats(); // Actualizar estadísticas básicas
      if (isPremium) {
        await fetchAdvancedStats(); // Actualizar estadísticas avanzadas si es premium
      }
    } catch (err) {
      console.error('Error al limpiar filtros:', err);
    } finally {
      setLoading(false);
    }
  };

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

      {isPremium && (
        <div className="premium-stats-section">
          <h2 className="premium-title">Estadísticas Avanzadas (Premium)</h2>
          
          <div className="filters-container">
            <div className="filter-group">
              <label>Edad:</label>
              <input
                type="number"
                value={filters.age}
                onChange={(e) => handleFilterChange('age', e.target.value)}
                placeholder="Ej: 25"
                min="1"
                max="120"
              />
            </div>
            
            <div className="filter-group">
              <label>Tipo de Suscripción:</label>
              <select 
                value={filters.subscriptionType} 
                onChange={(e) => handleFilterChange('subscriptionType', e.target.value)}
              >
                <option value="">Todos</option>
                <option value="Basic">Básica</option>
                <option value="Standard">Standard</option>
                <option value="Premium">Premium</option>
              </select>
            </div>
            
            <div className="filter-group">
              <label>Región:</label>
              <select 
                value={filters.region} 
                onChange={(e) => handleFilterChange('region', e.target.value)}
              >
                <option value="">Todas</option>
                <option value="South America">América del Sur</option>
                <option value="Europe">Europa</option>
                <option value="North America">América del Norte</option>
                <option value="Asia">Asia</option>
                <option value="Africa">África</option>
                <option value="Oceania">Oceanía</option>
              </select>
            </div>
            
            <div className="filter-group">
              <label>Dispositivo:</label>
              <select 
                value={filters.device} 
                onChange={(e) => handleFilterChange('device', e.target.value)}
              >
                <option value="">Todos</option>
                <option value="Tablet">Tableta</option>
                <option value="Laptop">Computadora personal</option>
                <option value="Mobile">Telefono Móvil</option>
                <option value="TV">Televisión</option>
                <option value="Desktop">Computadora</option>
              </select>
            </div>
            
            <div className="filter-group">
              <label>Género:</label>
              <select 
                value={filters.gender} 
                onChange={(e) => handleFilterChange('gender', e.target.value)}
              >
                <option value="">Todos</option>
                <option value="M">Masculino</option>
                <option value="F">Femenino</option>
              </select>
            </div>
            
            <div className="filter-buttons">
              <button className="apply-filters-btn" onClick={applyFilters}>
                <Filter size={16} />
                Aplicar Filtros
              </button>
              
              <button className="clear-filters-btn" onClick={clearFilters}>
                <X size={16} />
                Limpiar Filtros
              </button>
            </div>
          </div>
          
          {advancedStats && advancedStats.total !== undefined && (
            <div className="advanced-stats-grid">
              <div className="glass-panel advanced-stat-card">
                <div className="stat-icon">
                  <Users size={24} color="#3b82f6" />
                </div>
                <div>
                  <div className="stat-title">Total Predicciones</div>
                  <div className="stat-value">{advancedStats.total || 0}</div>
                </div>
              </div>
              
              <div className="glass-panel advanced-stat-card">
                <div className="stat-icon">
                  <AlertTriangle size={24} color="#ef4444" />
                </div>
                <div>
                  <div className="stat-title">En Riesgo</div>
                  <div className="stat-value">{advancedStats.churn || 0}</div>
                </div>
              </div>
              
              <div className="glass-panel advanced-stat-card">
                <div className="stat-icon">
                  <Activity size={24} color="#10b981" />
                </div>
                <div>
                  <div className="stat-title">Retenidos</div>
                  <div className="stat-value">{advancedStats.noChurn || 0}</div>
                </div>
              </div>
              
              <div className="glass-panel advanced-stat-card">
                <div className="stat-icon">
                  <Filter size={24} color="#f59e0b" />
                </div>
                <div>
                  <div className="stat-title">Tasa de Churn Filtrada</div>
                  <div className="stat-value">{advancedStats.rate ? advancedStats.rate.toFixed(2) : '0.00'}%</div>
                </div>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
