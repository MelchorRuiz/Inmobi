import './Footer.css';

export default function Footer() {
  return (
    <footer className="footer-container">
      <div className="footer-content">
        {/* Logotipo / Isotipo sutil en el footer */}
        <div className="footer-logo">
          <span className="footer-logo-badge">Inmobi</span>
        </div>

        {/* Enlaces secundarios o legales */}
        <div className="footer-links">
          <a href="#principios">Principios</a>
          <a href="#comparativa">Comparativa</a>
          <a href="#descargar">Descargar</a>
        </div>

        {/* Derechos de autor */}
        <p className="footer-rights">
          © {new Date().getFullYear()} Inmobi. Todos los derechos reservados.
        </p>
      </div>
    </footer>
  );
}