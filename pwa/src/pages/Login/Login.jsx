import { useState } from 'react';
import './Login.css';
import Logo from '../../assets/LogoTipo.png';
import { GoogleLogin } from '@react-oauth/google';

export default function Login({ onNavigate }) {
  const [formData, setFormData] = useState({ email: '', password: '', remember: false });

  const handleChange = (e) => {
    const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
    setFormData({ ...formData, [e.target.name]: value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log('Iniciando sesión:', formData);
  };

  return (
    <div className="auth-page">
      {/* Header Minimalista */}
      <header className="auth-header-bar">
        <div className="brand-logo" onClick={() => onNavigate('inicio')}>
            <img src={Logo} alt="Inmobi Logo" className="logo-img" />
            <span className="logo-title">Inmobi</span>
        </div>

        {/* Botón de volver al inicio rediseñado y aesthetic */}
        <button className="btn-volver-aesthetic" onClick={() => onNavigate('inicio')}>
            <span className="icon-back">🏠</span>
            <span>Volver al inicio</span>
        </button>
      </header>

      {/* Contenedor Split con Estética Inmobi */}
      <main className="auth-split-container">
        {/* Panel Izquierdo: Esencia y Motivación */}
        <div className="auth-brand-side">
          <div className="brand-badge-pill">✨ MERCADO TRANSPARENTE</div>
          <h2>El camino inteligente hacia tu próximo hogar.</h2>
          <p>
            Accede a tasaciones con IA, contratos digitales seguros y un trato directo sin intermediarios ni comisiones sorpresa.
          </p>

          <div className="auth-quote-card">
            <p className="quote-text">"La app donde el precio es real y el trato es justo."</p>
            <span className="quote-author">Com comunidad Inmobi</span>
          </div>
        </div>

        {/* Panel Derecho: Formulario */}
        <div className="auth-form-side">
          <div className="auth-card-modern">
            <div className="card-top">
              <h3>Iniciar Sesión</h3>
              <p>Ingresa tus datos para acceder a tu cuenta</p>
            </div>

            {/* Google OAuth Oficial */}
            <div className="google-oauth-box">
              <GoogleLogin
                onSuccess={(res) => console.log('Login Google OK:', res)}
                onError={() => console.error('Error Google Login')}
                theme="outline"
                size="large"
                width="100%"
                text="continue_with"
                shape="rectangular"
                locale="es"
              />
            </div>

            <div className="auth-divider">
              <span>o con tu correo</span>
            </div>

            <form onSubmit={handleSubmit} className="modern-form">
              <div className="input-field">
                <label>Correo electrónico</label>
                <input 
                  type="email" 
                  name="email" 
                  placeholder="tucorreo@ejemplo.com" 
                  value={formData.email} 
                  onChange={handleChange} 
                  required 
                />
              </div>

              <div className="input-field">
                <div className="label-flex">
                  <label>Contraseña</label>
                  <span className="link-forgot" onClick={() => alert('Función de recuperación')}>
                    ¿Olvidaste tu contraseña?
                  </span>
                </div>
                <input 
                  type="password" 
                  name="password" 
                  placeholder="••••••••" 
                  value={formData.password} 
                  onChange={handleChange} 
                  required 
                />
              </div>

              <div className="checkbox-row">
                <label className="custom-checkbox">
                  <input 
                    type="checkbox" 
                    name="remember" 
                    checked={formData.remember} 
                    onChange={handleChange} 
                  />
                  <span>Recordar mi sesión</span>
                </label>
              </div>

              <button type="submit" className="btn-inmobi-primary">
                Entrar a Inmobi
              </button>
            </form>

            <div className="card-footer-action">
              <p>
                ¿Aún no tienes cuenta?{' '}
                <button type="button" onClick={() => onNavigate('register')} className="link-highlight">
                  Regístrate aquí
                </button>
              </p>
            </div>
          </div>
        </div>
      </main>

      <footer className="auth-footer-bar">
        <span>© 2026 Inmobi. Todos los derechos reservados.</span>
      </footer>
    </div>
  );
}