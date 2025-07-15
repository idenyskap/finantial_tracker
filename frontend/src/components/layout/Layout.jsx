import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { useThemedStyles } from '../../hooks/useThemedStyles';
import ThemeToggle from '../ThemeToggle';

function Layout({ children }) {
  const navigate = useNavigate();
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
            <Link to="/dashboard" style={styles.link}>Dashboard</Link>
            <Link to="/transactions" style={styles.link}>Transactions</Link>
            <Link to="/categories" style={styles.link}>Categories</Link>
            <Link to="/budgets" style={styles.link}>Budgets</Link>
            <Link to="/goals" style={styles.link}>Goals</Link>
            <Link to="/recurring" style={styles.link}>Recurring</Link>
            <Link to="/profile" style={styles.link}>Profile</Link>

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
    boxShadow: theme.shadow,
    borderBottom: `1px solid ${theme.border}`,
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
    color: theme.primary,
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
    color: theme.text,
    textDecoration: 'none',
    padding: '0.5rem 1rem',
    borderRadius: '4px',
    transition: 'all 0.2s',
    '&:hover': {
      backgroundColor: theme.backgroundTertiary,
    }
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
  },
  logoutBtn: {
    backgroundColor: theme.danger,
    color: 'white',
    border: 'none',
    padding: '0.5rem 1rem',
    borderRadius: '4px',
    cursor: 'pointer',
    transition: 'opacity 0.2s',
    '&:hover': {
      opacity: 0.9,
    }
  },
  main: {
    maxWidth: '1200px',
    margin: '2rem auto',
    padding: '0 1rem',
  },
});

export default Layout;
