import { Link, Outlet } from 'react-router-dom';
import { useAuth } from '../features/auth/useAuth';

export default function Layout() {
  const { user, logout } = useAuth();

  const navStyle = {
    backgroundColor: '#2c3e50',
    color: 'white',
    padding: '1rem 2rem',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center'
  };

  const linkStyle = {
    color: 'white',
    textDecoration: 'none',
    padding: '0.5rem 1rem',
    marginRight: '1rem',
    borderRadius: '4px',
    transition: 'background-color 0.2s'
  };

  const mainStyle = {
    padding: '2rem',
    maxWidth: '1400px',
    margin: '0 auto'
  };

  return (
    <div>
      <nav style={navStyle}>
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <h2 style={{ margin: 0, marginRight: '2rem' }}>AS2 Server</h2>
          <Link to="/" style={linkStyle}>Dashboard</Link>
          <Link to="/partners" style={linkStyle}>Partners</Link>
          <Link to="/certificates" style={linkStyle}>Certificates</Link>
          <Link to="/messages" style={linkStyle}>Messages</Link>
          <Link to="/cem" style={linkStyle}>CEM</Link>
          <Link to="/statistics" style={linkStyle}>Statistics</Link>
          <Link to="/preferences" style={linkStyle}>Preferences</Link>
          <Link to="/system" style={linkStyle}>System</Link>
        </div>
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <span style={{ marginRight: '1rem' }}>User: {user?.username}</span>
          <button
            onClick={logout}
            style={{
              padding: '0.5rem 1rem',
              backgroundColor: '#e74c3c',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer'
            }}
          >
            Logout
          </button>
        </div>
      </nav>
      <main style={mainStyle}>
        <Outlet />
      </main>
    </div>
  );
}
