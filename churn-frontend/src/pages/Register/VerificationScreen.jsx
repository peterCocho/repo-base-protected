import React, { useState } from 'react';

export default function VerificationScreen({ onVerified }) {
  const [code, setCode] = useState('');
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    // Simulación de verificación. Reemplazar por llamada real al backend.
    setTimeout(() => {
      if (code.length === 6) {
        setSuccess(true);
        setLoading(false);
        setTimeout(() => {
          if (onVerified) onVerified();
        }, 1800);
      } else {
        setError('El código debe tener 6 dígitos.');
        setLoading(false);
      }
    }, 1200);
  };

  return (
    <div className="glass-panel" style={{ maxWidth: 400, margin: '4rem auto', padding: '2.5rem 2rem', borderRadius: '1.5rem', textAlign: 'center' }}>
      <h2>Verificación de correo</h2>
      <p>Ingresa el código de verificación que recibiste en tu correo electrónico.</p>
      <form onSubmit={handleSubmit} style={{ marginTop: '2rem' }}>
        <input
          type="text"
          maxLength={6}
          value={code}
          onChange={e => setCode(e.target.value.replace(/\D/g, ''))}
          placeholder="Código de 6 dígitos"
          style={{
            width: '100%',
            padding: '0.8rem',
            fontSize: '1.2rem',
            borderRadius: '0.8rem',
            border: '1px solid #cbd5e1',
            marginBottom: '1.2rem',
            textAlign: 'center',
            letterSpacing: '0.2em',
            background: 'rgba(255,255,255,0.7)'
          }}
          disabled={success || loading}
        />
        <button
          type="submit"
          className="btn-primary"
          style={{ width: '100%', padding: '0.8rem', fontSize: '1.1rem', borderRadius: '0.8rem', fontWeight: 600 }}
          disabled={loading || success}
        >
          {loading ? 'Verificando...' : 'Verificar código'}
        </button>
      </form>
      {error && <div style={{ color: '#ef4444', marginTop: '1rem' }}>{error}</div>}
      {success && <div style={{ color: '#22c55e', marginTop: '1.5rem', fontWeight: 600 }}>¡Cuenta verificada exitosamente! Redirigiendo a login...</div>}
    </div>
  );
}
