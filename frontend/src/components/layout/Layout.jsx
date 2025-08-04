import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { useThemedStyles } from '../../hooks/useThemedStyles';
import ThemeToggle from '../ThemeToggle';

function Layout({ children }) {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();
  const styles = useThemedStyles(getStyles);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div style={styles.container}>
      <nav style={styles.nav}>
        <div style={styles.navContent}>
          <Link to="/dashboard" style={styles.logo}>Financial Tracker</Link>

          <div style={styles.navLinks}>
            <Link
              to="/dashboard"
              style={{
                ...styles.link,
                ...(location.pathname === '/dashboard' ? styles.activeLink : {})
              }}
            >
              Dashboard
            </Link>
            <Link
              to="/transactions"
              style={{
                ...styles.link,
                ...(location.pathname === '/transactions' ? styles.activeLink : {})
              }}
            >
              Transactions
            </Link>
            <Link
              to="/categories"
              style={{
                ...styles.link,
                ...(location.pathname === '/categories' ? styles.activeLink : {})
              }}
            >
              Categories
            </Link>
            <Link
              to="/budgets"
              style={{
                ...styles.link,
                ...(location.pathname === '/budgets' ? styles.activeLink : {})
              }}
            >
              Budgets
            </Link>
            <Link
              to="/goals"
              style={{
                ...styles.link,
                ...(location.pathname === '/goals' ? styles.activeLink : {})
              }}
            >
              Goals
            </Link>
            <Link
              to="/recurring"
              style={{
                ...styles.link,
                ...(location.pathname === '/recurring' ? styles.activeLink : {})
              }}
            >
              Recurring
            </Link>
            <Link
              to="/currency-converter"
              style={{
                ...styles.link,
                ...(location.pathname === '/currency-converter' ? styles.activeLink : {})
              }}
            >
              Converter
            </Link>
            <Link
              to="/profile"
              style={{
                ...styles.link,
                ...(location.pathname === '/profile' ? styles.activeLink : {})
              }}
            >
              Profile
            </Link>

            <div style={styles.navRight}>
              <span style={styles.userEmail}>{user?.email}</span>
              <ThemeToggle />
              <button onClick={handleLogout} style={styles.logoutBtn}>
                Logout
              </button>
            </div>
          </div>
        </div>
      </nav>

      <main style={styles.main}>
        {children}
      </main>
    </div>
  );
}

const getStyles = (theme) => ({
  container: {
    minHeight: '100vh',
    backgroundColor: theme.background,
    color: theme.text,
  },
  nav: {
    backgroundColor: theme.backgroundSecondary,
    padding: '1rem 0',
    boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06)',
    borderBottom: `1px solid ${theme.border}`,
  },
  navContent: {
    maxWidth: '100%',
    margin: '0 auto',
    padding: '0 2rem',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    flexWrap: 'wrap',
  },
  logo: {
    color: theme.primary,
    fontSize: '1.5rem',
    fontWeight: 'bold',
    textDecoration: 'none',
  },
  navLinks: {
    display: 'flex',
    gap: '0.5rem',
    alignItems: 'center',
    flexWrap: 'wrap',
  },
  link: {
    color: theme.text,
    textDecoration: 'none',
    padding: '0.75rem 1rem',
    borderRadius: '8px',
    transition: 'all 0.2s ease',
    fontSize: '0.95rem',
    fontWeight: '500',
    whiteSpace: 'nowrap',
  },
  activeLink: {
    backgroundColor: theme.primary,
    color: 'white',
    boxShadow: '0 2px 4px rgba(59, 130, 246, 0.3)',
  },
  navRight: {
    display: 'flex',
    alignItems: 'center',
    gap: '1rem',
    marginLeft: '2rem',
    paddingLeft: '2rem',
    borderLeft: `1px solid ${theme.border}`,
  },
  userEmail: {
    color: theme.textSecondary,
    fontSize: '0.875rem',
    fontWeight: '500',
  },
  logoutBtn: {
    backgroundColor: '#ef4444',
    color: 'white',
    border: 'none',
    padding: '0.5rem 1rem',
    borderRadius: '6px',
    cursor: 'pointer',
    transition: 'all 0.2s ease',
    fontSize: '0.875rem',
    fontWeight: '500',
  },
  main: {
    width: '100%',
    maxWidth: 'none',
    margin: '2rem 0',
    padding: '0 2rem',
  },
});

export default Layout;
