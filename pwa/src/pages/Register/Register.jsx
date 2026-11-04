import { useState } from 'react';
import './Register.css';
import Logo from '../../assets/LogoTipo.png';
import { GoogleLogin } from '@react-oauth/google';

export default function Register({ onNavigate }) {
  const [formData, setFormData] = useState({
    nombre: '',
    email: '',
    telefono: '',
    fechaNacimiento: '',
    preferencia: 'comprar', // Valor inicial
    password: '',
    confirmPassword: '',
    terms: false
  });

  const handleChange = (e) => {
    const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
    setFormData({ ...formData, [e.target.name]: value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (formData.password !== formData.confirmPassword) {
      alert('Las contraseñas no coinciden');
      return;
    }
    console.log('Registrando usuario en Inmobi:', formData);
  };

  return (
    <div className="auth-page">
      {/* Header Superior Limpio */}
      <header className="auth-header-bar">
        <div className="brand-logo" onClick={() => onNavigate('inicio')}>
          <img src={Logo} alt="Inmobi Logo" className="logo-img" />
          <span className="logo-title">Inmobi</span>
        </div>

        <button className="btn-volver-aesthetic" onClick={() => onNavigate('inicio')}>
          <span className="icon-back">🏠</span>
          <span>Volver al inicio</span>
        </button>
      </header>

      {/* Tarjeta Central Equilibrada (Sin rellenos raras a los lados) */}
      <main className="auth-single-container">
        <div className="auth-card-clean">
          <div className="auth-card-header">
            <span className="badge-tag-mini">REGISTRO DE USUARIO</span>
            <h2>Crea tu cuenta en Inmobi</h2>
            <p>Accede a precios reales de mercado, contratos claros y cero comisiones sorpresa.</p>
          </div>

          {/* Botón Google OAuth Oficial */}
          <div className="google-oauth-box">
            <GoogleLogin
              onSuccess={(res) => console.log('Registro Google OK:', res)}
              onError={() => console.error('Error Google Registro')}
              theme="outline"
              size="large"
              width="100%"
              text="signup_with"
              shape="rectangular"
              locale="es"
            />
          </div>

          <div className="auth-divider">
            <span>o completa tu perfil</span>
          </div>

          <form onSubmit={handleSubmit} className="modern-form">
            {/* Fila 1: Nombre completo */}
            <div className="input-field">
              <label>Nombre completo</label>
              <input 
                type="text" 
                name="nombre" 
                placeholder="Ej. Ana Martínez Nuñez" 
                value={formData.nombre}
                onChange={handleChange}
                required 
              />
            </div>

            {/* Fila 2: Correo y Teléfono */}
            <div className="form-grid-row">
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
                <label>Teléfono de contacto</label>
                <input 
                  type="tel" 
                  name="telefono" 
                  placeholder="Ej. +52 477 123 4567" 
                  value={formData.telefono}
                  onChange={handleChange}
                  required 
                />
              </div>
            </div>

            {/* Fila 3: Fecha de Nacimiento y Preferencia */}
            <div className="form-grid-row">
              <div className="input-field">
                <label>Fecha de nacimiento</label>
                <input 
                  type="date" 
                  name="fechaNacimiento" 
                  value={formData.fechaNacimiento}
                  onChange={handleChange}
                  required 
                />
              </div>

              <div className="input-field">
                <label>¿Qué buscas hacer en Inmobi?</label>
                <select 
                  name="preferencia" 
                  value={formData.preferencia} 
                  onChange={handleChange}
                  className="select-custom"
                >
                  <option value="comprar">Comprar un inmueble</option>
                  <option value="rentar">Rentar una propiedad</option>
                  <option value="vender">Vender o publicar propiedad</option>
                  <option value="arrendar">Poner en renta (Propietario)</option>
                </select>
              </div>
            </div>

            {/* Fila 4: Contraseñas */}
            <div className="form-grid-row">
              <div className="input-field">
                <label>Contraseña</label>
                <input 
                  type="password" 
                  name="password" 
                  placeholder="Mínimo 8 caracteres" 
                  value={formData.password}
                  onChange={handleChange}
                  required 
                />
              </div>

              <div className="input-field">
                <label>Confirmar contraseña</label>
                <input 
                  type="password" 
                  name="confirmPassword" 
                  placeholder="Repite tu contraseña" 
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  required 
                />
              </div>
            </div>

            {/* Términos */}
            <div className="checkbox-row">
              <label className="custom-checkbox">
                <input 
                  type="checkbox" 
                  name="terms" 
                  checked={formData.terms}
                  onChange={handleChange}
                  required
                />
                <span>Acepto los Términos de Servicio y la Política de Privacidad de Inmobi</span>
              </label>
            </div>

            <button type="submit" className="btn-inmobi-primary">
              Completar Registro
            </button>
          </form>

          <div className="card-footer-action">
            <p>
              ¿Ya tienes cuenta?{' '}
              <button type="button" onClick={() => onNavigate('login')} className="link-highlight">
                Inicia sesión aquí
              </button>
            </p>
          </div>
        </div>
      </main>

      <footer className="auth-footer-bar">
        <span>© 2026 Inmobi. Todos los derechos reservados.</span>
      </footer>
    </div>
  );
}