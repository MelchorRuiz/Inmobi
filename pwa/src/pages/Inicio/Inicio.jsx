import NavBar from '../../components/NavBar';
import Footer from '../../components/Footer';
import './Inicio.css';
import bannerCasa from '../../assets/Inicio.png';

export default function Inicio({ onNavigate }) {
  return (
    <div className="inicio-container">
      {/* 1. Barra de navegación superior con la función de navegación */}
      <NavBar isLoggedIn={false} onNavigate={onNavigate} />

      {/* Sección Hero */}
      <section className="hero-section">
        <div className="hero-content">
          <span className="badge-tag">HOY · PRINCIPIOS · COMPARATIVA · DESCARGAR</span>
          <h1>
            El acceso a la vivienda digna empieza con <span className="highlight">precios justos</span> y transparentes
          </h1>
          <p className="hero-subtitle">
            La app donde el precio es real y el trato es justo. Sin intermediarios, sin comisiones ocultas y con contratos claros.
          </p>
        </div>

        <div className="hero-banner-wrapper">
          <img src={bannerCasa} alt="Casa Inmobi" className="hero-banner-img" />
        </div>

        {/* Buscador flotante */}
        <div className="search-bar-container">
          <div className="filter-item">
            <span className="filter-label">Dirección o Ciudad</span>
            <input type="text" placeholder="León, México" />
          </div>
          <div className="filter-item">
            <span className="filter-label">Tipo de Inmueble</span>
            <select>
              <option>Apartamento</option>
              <option>Casa</option>
              <option>Terreno</option>
            </select>
          </div>
          <div className="filter-item">
            <span className="filter-label">Acción</span>
            <select>
              <option>Alquilar</option>
              <option>Comprar</option>
              <option>Vender</option>
            </select>
          </div>
          <button className="btn-search">Analizar en 30s</button>
        </div>
      </section>

      {/* Sección de Principios */}
      <section className="principles-section" id="principios">
        <span className="section-tag">PRINCIPIOS DE INMOBI</span>
        <h2>Diseñado para resolver las cuatro fallas del mercado inmobiliario</h2>
        <p className="section-desc">
          En Inmobi, tu propiedad, tu dinero y tus datos están protegidos con tecnología de punta y contratos claros.
        </p>

        <div className="cards-grid">
          <div className="principle-card">
            <span className="card-tag">VALORACIÓN</span>
            <h3>Tasación con IA Nominal</h3>
            <p>Conoce el valor real del inmueble en base a datos del mercado y no a la especulación de comisiones altas.</p>
          </div>

          <div className="principle-card">
            <span className="card-tag">INQUILINOS</span>
            <h3>Garantía para Inquilinos</h3>
            <p>Encuentra depósitos justos y procesos transparentes sin cobros abusivos ni avales imposibles.</p>
          </div>

          <div className="principle-card">
            <span className="card-tag">CONTRATOS</span>
            <h3>Renovaciones y Gestiones Rápidas</h3>
            <p>Firma y revisa contratos de forma digital sin trámites burocráticos tardados ni intermediarios.</p>
          </div>

          <div className="principle-card">
            <span className="card-tag">LEGAL</span>
            <h3>Inclusión Legal y Garantías</h3>
            <p>Planes de pago digital y protección jurídica en cada paso para asegurar la tranquilidad de las partes.</p>
          </div>
        </div>
      </section>

      {/* Tabla Comparativa */}
      <section className="comparison-section" id="comparativa">
        <span className="section-tag">COMPARATIVA DIGITAL</span>
        <h2>Inmobi vs Portales Inmobiliarios Tradicionales</h2>
        <p className="section-desc">Descubre por qué Inmobi redefine la forma en que interactúas con el mercado inmobiliario.</p>

        <div className="comparison-table-wrapper">
          <table className="comparison-table">
            <thead>
              <tr>
                <th>Criterio Fundamental</th>
                <th className="highlight-col">Plataforma Inmobi</th>
                <th>Portales Tradicionales</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Enfoque de Precio</td>
                <td className="highlight-col">Tasación transparente basada en valor real del mercado sin artificios.</td>
                <td>Precios inflados por comisiones de agencias intermediarias.</td>
              </tr>
              <tr>
                <td>Contratos y Operaciones</td>
                <td className="highlight-col">Administración digital y directa de contratos con validez legal.</td>
                <td>Poco control, contratos confusos y cláusulas ocultas.</td>
              </tr>
              <tr>
                <td>Protección</td>
                <td className="highlight-col">Asistencia legal activa y pagos seguros garantizados en cada etapa.</td>
                <td>Sin respaldo legal directo ni protección para compradores/inquilinos.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      {/* Sección de Descarga */}
      <section className="download-section" id="descargar">
        <span className="section-tag">LLEVA INMOBI CONTIGO</span>
        <h2>Llévate Inmobi contigo: APK para Android o PWA en tu navegador</h2>
        <p className="section-desc">Instala la app en tu dispositivo Android o usa la versión PWA web instantánea sin instalaciones pesadas.</p>

        <div className="download-cards-container">
          <div className="download-card">
            <div className="download-info">
              <h4>App Android · APK</h4>
              <p>Descarga directa para tu equipo</p>
            </div>
            <button className="btn-download">Descargar APK</button>
          </div>

          <div className="download-card dark-card">
            <div className="download-info">
              <h4>Web App · PWA</h4>
              <p>Instala directo desde tu navegador</p>
            </div>
            <button className="btn-download-dark">Instalar PWA web</button>
          </div>
        </div>
      </section>

      {/* Pie de página */}
      <Footer />
    </div>
  );
}