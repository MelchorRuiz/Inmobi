import './NavBar.css';

const linksPublicos = [
    { etiqueta: "Inicio", ruta: "./Inicio" },
    { etiqueta: "Principios", ruta: "./Principios" },
    { etiqueta: "Comparativa", ruta: "./Comparativa" },
    { etiqueta: "Descargar", ruta: "./Descargar" },
];

const linksPrivados = [
    { etiqueta: "Vender", ruta: "/vender" },
    { etiqueta: "Arrendar", ruta: "/arrendar" },
    { etiqueta: "Comprar", ruta: "/comprar" },
    { etiqueta: "Rentar", ruta: "/rentar" },
    { etiqueta: "Mensajes", ruta: "/mensajes" },
];

const NavBar = ({ isLoggedIn, onLogout, onLogin, onRegister }) => {
   const enlacesActuales = isLoggedIn ? linksPrivados : linksPublicos;

  return (
    <nav className="topBar">
      <div className="logo">Inmobi</div>

      <div className="nav-links">
        {enlacesActuales.map((item, index) => (
          <a key={index} href={item.ruta}>
            {item.etiqueta}
          </a>
        ))}
      </div>

      {/* Botones de autenticación dinámicos */}
      <div className="auth-buttons">
        {isLoggedIn ? (
          <button className="btn-logout" onClick={onLogout}>
            Cerrar Sesión
          </button>
        ) : (
          <>
            <button className="btn-register" onClick={onRegister}>
              Registrarse
            </button>
            <button className="btn-login" onClick={onLogin}>
              Iniciar Sesión
            </button>
          </>
        )}
      </div>
    </nav>
  );
}

export default NavBar;