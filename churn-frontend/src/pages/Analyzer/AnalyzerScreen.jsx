import React, { useState, useEffect, useRef } from "react";
import { Activity, User, CreditCard, MonitorPlay } from "lucide-react";
import "./AnalyzerScreen.css";
import successSound from "../../assets/success.mp3";
import errorSound from "../../assets/error.mp3";
import errorSound2 from "../../assets/advertence.mp3";
import api from "../../services/api";

export default function AnalyzerScreen() {
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [formError, setFormError] = useState("");

  const successAudioRef = useRef(null);
  const errorAudioRef = useRef(null);
  const advertenceAudioRef = useRef(null);

  useEffect(() => {
    try {
      console.log("[Analyzer] audio URLs:", { successSound, errorSound, errorSound2 });
      successAudioRef.current = new Audio(successSound);
      successAudioRef.current.preload = "auto";
      successAudioRef.current.load();
      successAudioRef.current.addEventListener("canplaythrough", () => {
        console.log("[Analyzer] success audio canplaythrough");
      });
      successAudioRef.current.addEventListener("error", (ev) => {
        console.error("[Analyzer] success audio error:", ev);
      });

      errorAudioRef.current = new Audio(errorSound);
      errorAudioRef.current.preload = "auto";
      errorAudioRef.current.load();
      errorAudioRef.current.addEventListener("canplaythrough", () => {
        console.log("[Analyzer] error audio canplaythrough");
      });
      errorAudioRef.current.addEventListener("error", (ev) => {
        console.error("[Analyzer] error audio error:", ev);
      });

      advertenceAudioRef.current = new Audio(errorSound2);
      advertenceAudioRef.current.preload = "auto";
      advertenceAudioRef.current.load();
      advertenceAudioRef.current.addEventListener("canplaythrough", () => {
        console.log("[Analyzer] advertence audio canplaythrough");
      });
      advertenceAudioRef.current.addEventListener("error", (ev) => {
        console.error("[Analyzer] advertence audio error:", ev);
      });
    } catch (e) {
      // ignore audio init errors
    }
  }, []);

  const [form, setForm] = useState({
    customer_id: "",
    age: "",
    gender: "",
    subscription_type: "",
    watch_hours: "",
    last_login_days: "",
    region: "",
    device: "",
    monthly_fee: "",
    payment_method: "",
    number_of_profiles: "",
    avg_watch_time_per_day: "",
    favorite_genre: "",
  });

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleAnalyze = async (e) => {
    e.preventDefault();
    setFormError("");
    setResult(null);

    // Intento de "unlock" de audio: algunos navegadores bloquean play() cuando
    // se invoca desde callbacks asíncronos (p. ej. respuesta de la API).
    // Reproducimos brevemente y pausamos para obtener permiso del usuario.
    try {
      [successAudioRef, errorAudioRef, advertenceAudioRef].forEach((r) => {
        try {
          if (r.current) {
            const p = r.current.play();
            if (p && typeof p.then === "function") {
              p.then(() => {
                r.current.pause();
                r.current.currentTime = 0;
              }).catch(() => {});
            }
          }
        } catch (e) {}
      });
    } catch (e) {}

    const requiredFields = [
      "customer_id",
      "age",
      "gender",
      "subscription_type",
      "watch_hours",
      "last_login_days",
      "region",
      "device",
      "monthly_fee",
      "payment_method",
      "number_of_profiles",
      "avg_watch_time_per_day",
      "favorite_genre",
    ];

    for (let field of requiredFields) {
      if (!form[field] || form[field] === "") {
        setFormError(
          "Datos insuficientes. El modelo de análisis requiere todos los datos.",
        );
        return;
      }
    }

    setLoading(true);
    try {
      const response = await api.post(`/api/v1/predictions/predict`, {
        ...form,
        age: Number(form.age),
        watch_hours: Number(form.watch_hours),
        last_login_days: Number(form.last_login_days),
        monthly_fee: Number(form.monthly_fee),
        number_of_profiles: Number(form.number_of_profiles),
        avg_watch_time_per_day: Number(form.avg_watch_time_per_day),
      });

      const data = response.data;
      let status = "success";
      let message = data.custom_message;

      if (data.prediction === "Churn") {
        status = "danger";
      } else if (data.prediction === "No Churn") {
        status = "success";
        message = "Buena Noticia, tu cliente seguirá activo en la plataforma.";
      }
      // Reproducir sonido según resultado (preloadado)
      try {
        const playSafe = (r) => {
          if (!r || !r.current) return;
          const p = r.current.play();
          if (p && typeof p.then === "function") p.catch(() => {});
        };
        if (status === "success") {
          playSafe(successAudioRef);
        } else {
          playSafe(errorAudioRef);
        }
      } catch (e) {
        // silencioso en fallos de reproducción
      }
      setResult({
        probability: Math.round((data.probability || 0) * 100),
        status,
        message,
      });
    } catch (err) {
      try {
        advertenceAudioRef.current && advertenceAudioRef.current.play();
      } catch (e) {}
      setResult({
        probability: 0,
        status: "danger",
        message: "Error al analizar. Intenta de nuevo.",
      });
    }
    setLoading(false);
  };

  return (
    <div className="analyzer-fullscreen">
      {!result ? (
        <form className="analyzer-container" onSubmit={handleAnalyze}>
          <div className="analyzer-header">
            <h2 className="analyzer-title-flex">Parámetros del Cliente</h2>
            <p className="analyzer-subtitle">
              Configure los datos del perfil para ejecutar el modelo de análisis
              de predicción
            </p>
          </div>

          {formError && (
            <div className="error-container-fade">
              <div className="error-banner-modern">
                <span>{formError}</span>
              </div>
            </div>
          )}

          <div className="analyzer-cards-grid">
            {/* CARD 1: PERFIL - 4 Líneas */}
            <div className="glass-panel analyzer-card">
              <h3>
                <User size={18} /> Perfil
              </h3>
              <div className="card-fields">
                <label className="analyzer-label">ID Cliente</label>
                <input
                  type="text"
                  name="customer_id"
                  className="input-glass"
                  placeholder="Ej: O211"
                  value={form.customer_id}
                  onChange={handleChange}
                />

                <label className="analyzer-label">Edad</label>
                <input
                  type="number"
                  name="age"
                  className="input-glass"
                  placeholder="Ej: 60"
                  value={form.age}
                  onChange={handleChange}
                />

                <label className="analyzer-label">Sexo</label>
                <select
                  name="gender"
                  className="input-glass"
                  value={form.gender}
                  onChange={handleChange}
                >
                  <option value="">Selecciona</option>
                  <option value="Male">Masculino</option>
                  <option value="Female">Femenino</option>
                </select>

                <label className="analyzer-label">Región</label>
                <select
                  className="input-glass"
                  name="region"
                  value={form.region}
                  onChange={handleChange}
                >
                  <option value="">Selecciona</option>
                  <option value="South America">América del Sur</option>
                  <option value="Europe">Europa</option>
                  <option value="North America">América del Norte</option>
                  <option value="Asia">Asia</option>
                  <option value="Africa">África</option>
                  <option value="Oceania">Oceanía</option>
                </select>
              </div>
            </div>

            {/* CARD 2: SUSCRIPCIÓN - 4 Líneas */}
            <div className="glass-panel analyzer-card">
              <h3>
                <CreditCard size={18} /> Suscripción
              </h3>
              <div className="card-fields">
                <label className="analyzer-label">Tipo de suscripción</label>
                <select
                  name="subscription_type"
                  className="input-glass"
                  value={form.subscription_type}
                  onChange={handleChange}
                >
                  <option value="">Selecciona</option>
                  <option value="Basic">Básica</option>
                  <option value="Premium">Premium</option>
                  <option value="Standard">Standard</option>
                </select>

                <label className="analyzer-label">Cargo mensual</label>
                <input
                  type="number"
                  name="monthly_fee"
                  className="input-glass"
                  placeholder="Ej: 15.99"
                  value={form.monthly_fee}
                  onChange={handleChange}
                  step="0.01"
                />

                <label className="analyzer-label">Método de pago</label>
                <select
                  className="input-glass"
                  name="payment_method"
                  value={form.payment_method}
                  onChange={handleChange}
                >
                  <option value="">Selecciona</option>
                  <option value="Debit Card">Tarjeta de débito</option>
                  <option value="PayPal">PayPal</option>
                  <option value="Credit Card">Tarjeta de Crédito</option>
                  <option value="Crypto">Criptomoneda</option>
                </select>

                <label className="analyzer-label">Género favorito</label>
                <select
                  className="input-glass"
                  name="favorite_genre"
                  value={form.favorite_genre}
                  onChange={handleChange}
                >
                  <option value="">Selecciona</option>
                  <option value="Drama">Drama</option>
                  <option value="Sci-Fi">Ciencia Ficción</option>
                  <option value="Action">Acción</option>
                  <option value="Comedy">Comedia</option>
                  <option value="Documentary">Documental</option>
                  <option value="Horror">Terror</option>
                </select>
              </div>
            </div>

            {/* CARD 3: ACTIVIDAD - 4 Líneas (Campos agrupados) */}
            <div className="glass-panel analyzer-card">
              <h3>
                <MonitorPlay size={18} /> Actividad
              </h3>
              <div className="card-fields">
                {/* Línea 1: Dos campos en uno */}
                <div style={{ display: "flex", gap: "10px" }}>
                  <div style={{ flex: 1 }}>
                    <label className="analyzer-label">Horas totales</label>
                    <input
                      type="number"
                      name="watch_hours"
                      className="input-glass"
                      placeholder="120"
                      value={form.watch_hours}
                      onChange={handleChange}
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label className="analyzer-label">Promedio diario</label>
                    <input
                      type="number"
                      name="avg_watch_time_per_day"
                      className="input-glass"
                      placeholder="2.5"
                      value={form.avg_watch_time_per_day}
                      onChange={handleChange}
                      step="0.01"
                    />
                  </div>
                </div>

                {/* Línea 2 */}
                <label className="analyzer-label">Número de perfiles</label>
                <input
                  type="number"
                  name="number_of_profiles"
                  className="input-glass"
                  placeholder="Ej: 2"
                  value={form.number_of_profiles}
                  onChange={handleChange}
                  min="1"
                />

                {/* Línea 3 */}
                <label className="analyzer-label">Días último login</label>
                <input
                  type="number"
                  name="last_login_days"
                  className="input-glass"
                  placeholder="Ej: 5"
                  value={form.last_login_days}
                  onChange={handleChange}
                />

                {/* Línea 4 */}
                <label className="analyzer-label">Dispositivo</label>
                <select
                  className="input-glass"
                  name="device"
                  value={form.device}
                  onChange={handleChange}
                >
              <option value="">Selecciona</option>
              <option value="Tablet">Tableta</option>

							<option value="Laptop">Computadora personal</option>

							<option value="Mobile">Telefono Móvil</option>

							<option value="TV">Televisión</option>
							
							<option value="Desktop">Computadora</option>
                </select>
              </div>
            </div>
          </div>

          <div className="analyzer-actions">
            <button
              className="btn-p btn-analyze-large"
              type="submit"
              disabled={loading}
            >
              {loading ? "Procesando..." : "Analizar Riesgo de Cancelación"}
            </button>
          </div>
        </form>
      ) : (
        /* Panel de resultados */
        <div
          className="glass-panel analyzer-result-panel"
          style={{
            height: "80vh",
            width: "70vw",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
          }}
        >
          <div className={`analyzer-probability-circle ${result.status}`}>
            <h1 className="analyzer-probability-value">
              {result.probability}%
            </h1>
          </div>
          <h2 className="analyzer-prob-title">Probabilidad de Cancelación</h2>
          <div
            className={`glass-panel analyzer-recommendation ${result.status}`}
            
          >
            {result.status === "danger" && <strong>ACCIÓN RECOMENDADA:</strong>}
            <p className="analyzer-recommendation-message">{result.message}</p>
          </div>
          <button
            className="btn-primary mt-4"
            style={{ width: "300px", height: "3.5rem" }}
            onClick={() => setResult(null)}
          >
            Volver a analizar
          </button>
        </div>
      )}
    </div>
  );
}
