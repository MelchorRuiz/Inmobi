import './NavBar.css';
import Logo from '../assets/LogoTipo.png';

const linksPublicos = [
    { etiqueta: "Inicio", ruta: "inicio" },
    { etiqueta: "Principios", ruta: "#principios" },
    { etiqueta: "Comparativa", ruta: "#comparativa" },
    { etiqueta: "Descargar", ruta: "#descargar" },
];

const linksPrivados = [
    { etiqueta: "Vender", ruta: "vender" },
    { etiqueta: "Arrendar", ruta: "arrendar" },
    { etiqueta: "Comprar", ruta: "comprar" },
    { etiqueta: "Rentar", ruta: "rentar" },
    { etiqueta: "Mensajes", ruta: "mensajes" },
];

const NavBar = ({ isLoggedIn, onLogout, onNavigate }) => {
   const enlacesActuales = isLoggedIn ? linksPrivados : linksPublicos;

  return (
    <nav className="topBar">
      {/* El logotipo te regresa a la vista de inicio */}
      <div 
        className="navbar-logo-container" 
        onClick={() => onNavigate('inicio')}
        style={{ cursor: 'pointer' }}
      >
        <img src={Logo} alt="Inmobi Logo" className="navbar-logo-img" />
        <span className="navbar-logo-text">Inmobi</span>
      </div>

      <div className="nav-links">
        {enlacesActuales.map((item, index) => (
          <a key={index} href={`#${item.ruta}`}>
            {item.etiqueta}
          </a>
        ))}
      </div>

      {/* Botones de autenticación conectados a las vistas de Login y Register */}
      <div className="auth-buttons">
        {isLoggedIn ? (
          <button className="btn-logout" onClick={onLogout}>
            Cerrar Sesión
          </button>
        ) : (
          <>
            <button className="btn-register" onClick={() => onNavigate('register')}>
              Registrarse
            </button>
            <button className="btn-login" onClick={() => onNavigate('login')}>
              Iniciar Sesión
            </button>
          </>
        )}
      </div>
    </nav>
  );
}

export default NavBar;