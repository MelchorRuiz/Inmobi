import { useState } from 'react';
import './App.css';
import Inicio from './pages/Inicio/Inicio.jsx';
import Login from './pages/Login/Login.jsx';
import Register from './pages/Register/Register.jsx';

function App() {
  const [paginaActual, setPaginaActual] = useState('inicio');

  return (
    <div className="app-container">
      {paginaActual === 'inicio' && <Inicio onNavigate={setPaginaActual} />}
      {paginaActual === 'login' && <Login onNavigate={setPaginaActual} />}
      {paginaActual === 'register' && <Register onNavigate={setPaginaActual} />}
    </div>
  );
}

export default App;