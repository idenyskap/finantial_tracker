import { Link, useNavigate } from 'react-router-dom';
import { authService } from '../../services/authService';

function Layout({ children }) {
  const navigate = useNavigate();
  const isAuthenticated = authService.isAuthenticated();

  const handleLogout = () => {
    authService.logout();
  };

  return (
    <div>
      <nav style={styles.nav}>
        <div style={styles.navContent}>
          <Link to="/" style={styles.logo}>Financial Tracker</Link>

          <div style={styles.navLinks}>
            {isAuthenticated ? (
              <>
                <Link to="/transactions" style={styles.link}>Transactions</Link>
                <Link to="/categories" style={styles.link}>Categories</Link>
                <Link to="/budgets" style={styles.link}>Budgets</Link>
                <Link to="/dashboard" style={styles.link}>Dashboard</Link>
                <button onClick={handleLogout} style={styles.logoutBtn}>
                  Logout
                </button>
              </>
            ) : (
              <>
                <Link to="/login" style={styles.link}>Login</Link>
                <Link to="/register" style={styles.link}>Register</Link>
              </>
            )}
          </div>
        </div>
      </nav>

      <main style={styles.main}>
        {children}
      </main>
    </div>
  );
}

const styles = {
  nav: {
    backgroundColor: '#2c3e50',
    padding: '1rem 0',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  navContent: {
    maxWidth: '1200px',
    margin: '0 auto',
    padding: '0 1rem',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  logo: {
    color: 'white',
    fontSize: '1.5rem',
    fontWeight: 'bold',
    textDecoration: 'none',
  },
  navLinks: {
    display: 'flex',
    gap: '1rem',
    alignItems: 'center',
  },
  link: {
    color: 'white',
    textDecoration: 'none',
    padding: '0.5rem 1rem',
    borderRadius: '4px',
    transition: 'background-color 0.2s',
  },
  logoutBtn: {
    backgroundColor: '#e74c3c',
    color: 'white',
    border: 'none',
    padding: '0.5rem 1rem',
    borderRadius: '4px',
    cursor: 'pointer',
  },
  main: {
    maxWidth: '1200px',
    margin: '2rem auto',
    padding: '0 1rem',
  },
};

export default Layout;
