import React, { useState } from 'react';
import { Activity, PlayCircle } from 'lucide-react';
import './AnalyzerScreen.css';

export default function AnalyzerScreen() {
	const [result, setResult] = useState(null);
	const [loading, setLoading] = useState(false);

	const handleAnalyze = () => {
		setLoading(true);
		setTimeout(() => {
			setResult({
				probability: 82,
				status: 'danger',
				message: '¡Alerta! Ofrecer descuento del 15% inmediatamente.'
			});
			setLoading(false);
		}, 1500);
	};

	return (
		<div className="page-container grid-2 analyzer-page">
			<div className="glass-panel analyzer-panel-padding">
				<h2 className="analyzer-title-flex"><Activity size={20}/> Parámetros del Cliente</h2>
				<div className="analyzer-fields">
					<div>
						<label className="analyzer-label">Horas de visualización</label>
						<input type="number" className="input-glass" placeholder="Ej: 120" />
					</div>
					<div>
						<label className="analyzer-label">Días desde último login</label>
						<input type="number" className="input-glass" placeholder="Ej: 5" />
					</div>
					<div>
						<label className="analyzer-label">Número de perfiles</label>
						<input type="number" className="input-glass" placeholder="Ej: 3" />
					</div>
					<div>
						<label className="analyzer-label">Tarifa mensual</label>
						<input type="number" className="input-glass" placeholder="Ej: 29.99" step="0.01" />
					</div>
					<div>
						<label className="analyzer-label">Edad</label>
						<input type="number" className="input-glass" placeholder="Ej: 35" />
					</div>
					<div>
						<label className="analyzer-label">Tiempo promedio visualización por día</label>
						<input type="number" className="input-glass" placeholder="Ej: 2.5" step="0.01" />
					</div>
					<div>
						<label className="analyzer-label">Tipo de suscripción</label>
						<select className="input-glass">
							<option value="basica">Básica</option>
							<option value="premium">Premium</option>
							<option value="familiar">Familiar</option>
						</select>
					</div>
					<div>
						<label className="analyzer-label">Región</label>
						<select className="input-glass">
							<option value="norte">Norte</option>
							<option value="centro">Centro</option>
							<option value="sur">Sur</option>
							<option value="internacional">Internacional</option>
						</select>
					</div>
					<div>
						<label className="analyzer-label">Dispositivo</label>
						<select className="input-glass">
							<option value="movil">Móvil</option>
							<option value="pc">PC</option>
							<option value="smarttv">Smart TV</option>
							<option value="tablet">Tablet</option>
						</select>
					</div>
					<div>
						<label className="analyzer-label">Método de pago</label>
						<select className="input-glass">
							<option value="tarjeta">Tarjeta</option>
							<option value="paypal">PayPal</option>
							<option value="efectivo">Efectivo</option>
							<option value="otro">Otro</option>
						</select>
					</div>
					<div>
						<label className="analyzer-label">Género favorito</label>
						<select className="input-glass">
							<option value="drama">Drama</option>
							<option value="comedia">Comedia</option>
							<option value="accion">Acción</option>
							<option value="documental">Documental</option>
							<option value="terror">Terror</option>
							<option value="romance">Romance</option>
							<option value="animacion">Animación</option>
							<option value="cienciaficcion">Ciencia Ficción</option>
							<option value="otro">Otro</option>
						</select>
					</div>
					<div>
						<label className="analyzer-label">Sexo</label>
						<select className="input-glass">
							<option value="masculino">Masculino</option>
							<option value="femenino">Femenino</option>
							<option value="otro">Otro</option>
						</select>
					</div>

					<button className="btn-primary" onClick={handleAnalyze} disabled={loading}>
						{loading ? 'Procesando...' : 'Analizar Riesgo de Cancelacion'}
					</button>
				</div>
			</div>
			<div className="glass-panel analyzer-result-panel">
				{!result ? (
					<div className="analyzer-waiting">
						<PlayCircle size={64} className="analyzer-waiting-icon" />
						<p>Ingrese datos para predecir</p>
					</div>
				) : (
					<>
						<div className={`analyzer-probability-circle${result.status === 'danger' ? ' danger' : ''}`}>
							<h1 className="analyzer-probability-value">{result.probability}%</h1>
						</div>
						<h2 className="analyzer-prob-title">Probabilidad de Cancelación</h2>
						<div className="glass-panel analyzer-recommendation">
							<strong>ACCIÓN RECOMENDADA:</strong>
							<p className="analyzer-recommendation-message">{result.message}</p>
						</div>
					</>
				)}
			</div>
		</div>
	);
}