import React, { useState } from 'react';
import { Activity, PlayCircle } from 'lucide-react';
import './AnalyzerScreen.css';
import api from '../../services/api';

export default function AnalyzerScreen() {
	const [result, setResult] = useState(null);
	const [loading, setLoading] = useState(false);
	const [formError, setFormError] = useState("");

	const [form, setForm] = useState({
		customer_id: '',
		age: '',
		gender: '',
		subscription_type: '',
		watch_hours: '',
		last_login_days: '',
		region: '',
		device: '',
		monthly_fee: '',
		payment_method: '',
		number_of_profiles: '',
		avg_watch_time_per_day: '',
		favorite_genre: ''
	});

	const handleChange = (e) => {
		setForm({ ...form, [e.target.name]: e.target.value });
	};

	const handleAnalyze = async (e) => {
		e.preventDefault();
		setFormError("");
		setResult(null);
		// Validación: ningún campo vacío o nulo
		const requiredFields = [
			"customer_id", "age", "gender", "subscription_type", "watch_hours", "last_login_days", "region", "device", "monthly_fee", "payment_method", "number_of_profiles", "avg_watch_time_per_day", "favorite_genre"
		];
		for (let field of requiredFields) {
			if (form[field] === undefined || form[field] === null || form[field] === "") {
				setFormError("Por favor, completa todos los campos antes de analizar.");
				return;
			}
		}
		setLoading(true);
		try {
			const response = await api.post(
  `/api/v1/predictions/predict`,
  {
    ...form,
    age: Number(form.age),
    watch_hours: Number(form.watch_hours),
    last_login_days: Number(form.last_login_days),
    monthly_fee: Number(form.monthly_fee),
    number_of_profiles: Number(form.number_of_profiles),
    avg_watch_time_per_day: Number(form.avg_watch_time_per_day)
  }
);

			
			const data = response.data;
			let status = 'success';
			let message = data.custom_message;
			if (data.prediction === 'Churn') {
				status = 'danger';
			} else if (data.prediction === 'No Churn') {
				status = 'success';
				message = 'Buena Noticia, tu cliente seguirá activo en la plataforma.';
			}
			setResult({
				probability: Math.round((data.probability || 0) * 100),
				status,
				message
			});
		} catch (err) {
			setResult({
				probability: 0,
				status: 'danger',
				message: 'Error al analizar. Intenta de nuevo.'
			});
		}
		setLoading(false);
	};

	return (
		<div className="analyzer-fullscreen">
			{!result ? (
				<form className="glass-panel analyzer-form-full analyzer-form-full-inline" onSubmit={handleAnalyze}>
					<h2 className="analyzer-title-flex"><Activity size={20}/> Parámetros del Cliente</h2>
					{formError && (
						<div className="analyzer-error">{formError}</div>
					)}
					<div className="analyzer-fields-full">
            <div>
							<label className="analyzer-label">Identificador del Cliente</label>
							<input type="text" name="customer_id" className="input-glass" placeholder="Ej: O211" value={form.customer_id} onChange={handleChange} />
						</div>
						<div>
							<label className="analyzer-label">Edad</label>
							<input type="number" name="age" className="input-glass" placeholder="Ej: 60" value={form.age} onChange={handleChange} />
						</div>
                    <div>
                        <label className="analyzer-label">Cargo mensual</label>
						<input type="number" name="monthly_fee" className="input-glass input-number-no-spinner" placeholder="Ej: 15.99 (usar punto)" value={form.monthly_fee} onChange={handleChange} step="0.01" min="0" />
                    </div>
						<div>
							<label className="analyzer-label">Sexo</label>
							<select name="gender" className="input-glass" value={form.gender} onChange={handleChange}>
								<option value="">Selecciona</option>
								<option value="M">Masculino</option>
								<option value="F">Femenino</option>
							</select>
						</div>
						<div>
							<label className="analyzer-label">Tipo de suscripción</label>
							<select name="subscription_type" className="input-glass" value={form.subscription_type} onChange={handleChange}>
								<option value="">Selecciona</option>
								<option value="Basic">Básica</option>
								<option value="Premium">Premium</option>
								<option value="Standard">Standar</option>
							</select>
						</div>
						<div>
							<label className="analyzer-label">Horas de visualización</label>
							<input type="number" name="watch_hours" className="input-glass" placeholder="Ej: 12" value={form.watch_hours} onChange={handleChange} />
						</div>
								<div>
									<label className="analyzer-label">Número de perfiles</label>
									<input type="number" name="number_of_profiles" className="input-glass" placeholder="Ej: 2" value={form.number_of_profiles} onChange={handleChange} min="1" />
								</div>
								<div>
									<label className="analyzer-label">Promedio de horas diarias</label>
									<input type="number" name="avg_watch_time_per_day" className="input-glass input-number-no-spinner" placeholder="Ej: 2.5 (usar punto)" value={form.avg_watch_time_per_day} onChange={handleChange} step="0.01" min="0" />
								</div>
						<div>
							<label className="analyzer-label">Días desde último login</label>
							<input type="number" name="last_login_days" className="input-glass" placeholder="Ej: 30" value={form.last_login_days} onChange={handleChange} />
						</div>
						<div>
							<label className="analyzer-label">Región</label>
						<select className="input-glass" id="region-select" name="region" value={form.region} onChange={handleChange}>
							<option value="">Selecciona</option>
							<option value="South America">América del Sur</option>
							<option value="Europe">Europa</option>
							<option value="North America">América del Norte</option>
							<option value="Asia">Asia</option>
							<option value="Africa">África</option>
							<option value="Oceania">Oceanía</option>
						</select>
						</div>
						<div>
							<label className="analyzer-label">Dispositivo</label>
						<select className="input-glass" id="device-select" name="device" value={form.device} onChange={handleChange}>
							<option value="">Selecciona</option>
							<option value="Tablet">Tableta</option>
							<option value="Laptop">Computadora personal</option>
							<option value="Mobile">Telefono Móvil</option>
							<option value="TV">Televisión</option>
							<option value="Desktop">Computadora</option>
						</select>
						</div>
						<div>
							<label className="analyzer-label">Método de pago</label>
											<select className="input-glass" id="payment_method-select" name="payment_method" value={form.payment_method} onChange={handleChange}>
												<option value="">Selecciona</option>
												<option value="Debit Card">Tarjeta de débito</option>
												<option value="PayPal">PayPal</option>
												<option value="Crypto">Cryptomoneda</option>
												<option value="Gift Card">Tarjetas de Regalo</option>
												<option value="Credit Card">Tarjeta de Crédito</option>
											</select>
						</div>
						<div>
							<label className="analyzer-label">Género favorito</label>
								<select className="input-glass" id="favorite_genre-select" name="favorite_genre" value={form.favorite_genre} onChange={handleChange}>
									<option value="">Selecciona</option>
									<option value="Drama">Drama</option>
									<option value="Documentary">Documental</option>
									<option value="Romance">Romance</option>
									<option value="Sci-Fi">Ciencia Ficción</option>
									<option value="Horror">Terror</option>
									<option value="Action">Acción</option>
									<option value="Comedy">Comedia</option>
								</select>
						</div>
					</div>
					<button className="btn-p mt-4 analyzer-submit-btn" type="submit" disabled={loading}>
						{loading ? 'Procesando...' : 'Analizar Riesgo de Cancelacion'}
					</button>
				</form>
			) : (
				<div className="glass-panel analyzer-result-panel analyzer-result-panel-inline">
					<div className={`analyzer-probability-circle${result.status === 'danger' ? ' danger' : result.status === 'success' ? ' success' : ''}`}>
						<h1 className="analyzer-probability-value">{result.probability}%</h1>
					</div>
					<h2 className="analyzer-prob-title">Probabilidad de Cancelación</h2>
					<div className={`glass-panel analyzer-recommendation analyzer-recommendation-inline${result.status === 'success' ? ' success' : ''}`}>
						{result.status === 'danger' && <strong>ACCIÓN RECOMENDADA:</strong>}
						<p className={`analyzer-recommendation-message${result.status === 'success' ? ' success' : ''}`}>{result.message}</p>
					</div>
					<button className="btn-primary mt-4 analyzer-back-btn" onClick={() => setResult(null)}>
						Volver a analizar otro cliente
					</button>
				</div>
			)}
		</div>
	);
}