import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { useThemedStyles } from '../../hooks/useThemedStyles';
import { useLanguage } from '../../hooks/useLanguage';
import ThemeToggle from '../ThemeToggle';
import LanguageSelector from '../language/LanguageSelector';
import {
  HomeIcon,
  BanknotesIcon,
  TagIcon,
  WalletIcon,
  TrophyIcon,
  ArrowPathIcon,
  CurrencyDollarIcon,
  UserCircleIcon,
  ArrowRightOnRectangleIcon
} from '@heroicons/react/24/outline';

function Layout({ children }) {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();
  const { t } = useLanguage();
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

          <div style={styles.navCenter}>
            <Link
              to="/dashboard"
              style={{
                ...styles.link,
                ...(location.pathname === '/dashboard' ? styles.activeLink : {})
              }}
            >
              <HomeIcon style={styles.navIcon} />
              {t('navigation.dashboard')}
            </Link>
            <Link
              to="/transactions"
              style={{
                ...styles.link,
                ...(location.pathname === '/transactions' ? styles.activeLink : {})
              }}
            >
              <BanknotesIcon style={styles.navIcon} />
              {t('navigation.transactions')}
            </Link>
            <Link
              to="/categories"
              style={{
                ...styles.link,
                ...(location.pathname === '/categories' ? styles.activeLink : {})
              }}
            >
              <TagIcon style={styles.navIcon} />
              {t('navigation.categories')}
            </Link>
            <Link
              to="/budgets"
              style={{
                ...styles.link,
                ...(location.pathname === '/budgets' ? styles.activeLink : {})
              }}
            >
              <WalletIcon style={styles.navIcon} />
              {t('navigation.budgets')}
            </Link>
            <Link
              to="/goals"
              style={{
                ...styles.link,
                ...(location.pathname === '/goals' ? styles.activeLink : {})
              }}
            >
              <TrophyIcon style={styles.navIcon} />
              {t('navigation.goals')}
            </Link>
            <Link
              to="/recurring"
              style={{
                ...styles.link,
                ...(location.pathname === '/recurring' ? styles.activeLink : {})
              }}
            >
              <ArrowPathIcon style={styles.navIcon} />
              {t('navigation.recurring')}
            </Link>
            <Link
              to="/currency-converter"
              style={{
                ...styles.link,
                ...(location.pathname === '/currency-converter' ? styles.activeLink : {})
              }}
            >
              <CurrencyDollarIcon style={styles.navIcon} />
              {t('navigation.converter')}
            </Link>
            <Link
              to="/profile"
              style={{
                ...styles.link,
                ...(location.pathname === '/profile' ? styles.activeLink : {})
              }}
            >
              <UserCircleIcon style={styles.navIcon} />
              {t('navigation.profile')}
            </Link>
          </div>

          <div style={styles.navRight}>
            <LanguageSelector compact={true} />
            <ThemeToggle />
            <button onClick={handleLogout} style={styles.logoutBtn} title={user?.email}>
              <ArrowRightOnRectangleIcon style={styles.logoutIcon} />
            </button>
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
    gap: '1rem',
  },
  logo: {
    color: theme.primary,
    fontSize: '1.5rem',
    fontWeight: 'bold',
    textDecoration: 'none',
    flexShrink: 0,
  },
  navCenter: {
    display: 'flex',
    gap: '0.25rem',
    alignItems: 'center',
    flex: 1,
    justifyContent: 'center',
  },
  link: {
    color: theme.text,
    textDecoration: 'none',
    padding: '0.5rem 0.6rem',
    borderRadius: '6px',
    transition: 'all 0.2s ease',
    fontSize: '0.85rem',
    fontWeight: '500',
    whiteSpace: 'nowrap',
    display: 'flex',
    alignItems: 'center',
    gap: '0.35rem',
  },
  navIcon: {
    width: '16px',
    height: '16px',
  },
  activeLink: {
    backgroundColor: theme.primary,
    color: 'white',
    boxShadow: '0 2px 4px rgba(59, 130, 246, 0.3)',
  },
  navRight: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.75rem',
    flexShrink: 0,
    paddingLeft: '1rem',
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
    padding: '0.5rem',
    borderRadius: '6px',
    cursor: 'pointer',
    transition: 'all 0.2s ease',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  logoutIcon: {
    width: '18px',
    height: '18px',
  },
  main: {
    width: '100%',
    maxWidth: 'none',
    margin: '2rem 0',
    padding: '0 2rem',
  },
});

export default Layout;
