import { Link } from 'react-router-dom';
import { useThemedStyles } from '../../hooks/useThemedStyles';
import ThemeToggle from '../ThemeToggle';
import LanguageSelector from '../language/LanguageSelector';
import { CurrencyDollarIcon } from '@heroicons/react/24/outline';

const AuthHeader = ({ currentPage }) => {
  const styles = useThemedStyles(getStyles);

  return (
    <header style={styles.header}>
      <div style={styles.headerContent}>
        <Link to="/" style={styles.logo}>
          <CurrencyDollarIcon style={styles.logoIcon} />
          <span style={styles.logoText}>FinanceTracker</span>
        </Link>

        <nav style={styles.nav}>
          <LanguageSelector compact />
          <ThemeToggle />
          {currentPage !== 'login' && (
            <Link to="/login" style={styles.navLink}>Login</Link>
          )}
          {currentPage !== 'register' && (
            <Link to="/register" style={styles.navLinkPrimary}>Sign Up</Link>
          )}
        </nav>
      </div>
    </header>
  );
};

const getStyles = (theme) => ({
  header: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    borderBottom: `1px solid ${theme.border}`,
    backgroundColor: theme.backgroundSecondary,
    zIndex: 100,
  },
  headerContent: {
    maxWidth: '1200px',
    margin: '0 auto',
    padding: '1rem 2rem',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  logo: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
    textDecoration: 'none',
    cursor: 'pointer',
  },
  logoIcon: {
    width: '28px',
    height: '28px',
    color: theme.primary,
  },
  logoText: {
    margin: 0,
    fontSize: '1.25rem',
    fontWeight: 'bold',
    color: theme.primary,
  },
  nav: {
    display: 'flex',
    alignItems: 'center',
    gap: '1rem',
  },
  navLink: {
    padding: '0.5rem 1rem',
    color: theme.text,
    textDecoration: 'none',
    borderRadius: '4px',
    transition: 'background-color 0.2s',
    fontSize: '0.9rem',
  },
  navLinkPrimary: {
    padding: '0.5rem 1rem',
    backgroundColor: theme.primary,
    color: 'white',
    textDecoration: 'none',
    borderRadius: '4px',
    transition: 'opacity 0.2s',
    fontSize: '0.9rem',
  },
});

export default AuthHeader;