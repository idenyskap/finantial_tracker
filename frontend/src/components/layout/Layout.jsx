import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { useThemedStyles } from '../../hooks/useThemedStyles';
import { useLanguage } from '../../hooks/useLanguage';
import { useIsMobile } from '../../hooks/useMediaQuery';
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
  ArrowRightOnRectangleIcon,
  Bars3Icon,
  XMarkIcon
} from '@heroicons/react/24/outline';

function Layout({ children }) {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();
  const { t } = useLanguage();
  const isMobile = useIsMobile();
  const styles = useThemedStyles(getStyles);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const navLinks = [
    { to: '/dashboard', icon: HomeIcon, label: t('navigation.dashboard') },
    { to: '/transactions', icon: BanknotesIcon, label: t('navigation.transactions') },
    { to: '/categories', icon: TagIcon, label: t('navigation.categories') },
    { to: '/budgets', icon: WalletIcon, label: t('navigation.budgets') },
    { to: '/goals', icon: TrophyIcon, label: t('navigation.goals') },
    { to: '/recurring', icon: ArrowPathIcon, label: t('navigation.recurring') },
    { to: '/currency-converter', icon: CurrencyDollarIcon, label: t('navigation.converter') },
    { to: '/profile', icon: UserCircleIcon, label: t('navigation.profile') },
  ];

  const handleNavClick = () => {
    if (isMobile) {
      setMobileMenuOpen(false);
    }
  };

  return (
    <div style={styles.container}>
      <nav style={styles.nav}>
        <div style={styles.navContent}>
          <Link to="/dashboard" style={styles.logo}>
            {isMobile ? 'FT' : 'Financial Tracker'}
          </Link>

          {/* Desktop Navigation */}
          {!isMobile && (
            <div style={styles.navCenter}>
              {navLinks.map((link) => (
                <Link
                  key={link.to}
                  to={link.to}
                  style={{
                    ...styles.link,
                    ...(location.pathname === link.to ? styles.activeLink : {})
                  }}
                >
                  <link.icon style={styles.navIcon} />
                  {link.label}
                </Link>
              ))}
            </div>
          )}

          <div style={styles.navRight}>
            <LanguageSelector compact={true} />
            <ThemeToggle />
            {!isMobile && (
              <button onClick={handleLogout} style={styles.logoutBtn} title={user?.email}>
                <ArrowRightOnRectangleIcon style={styles.logoutIcon} />
              </button>
            )}
            {/* Hamburger Menu Button */}
            {isMobile && (
              <button
                onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
                style={styles.hamburgerBtn}
              >
                {mobileMenuOpen ? (
                  <XMarkIcon style={styles.hamburgerIcon} />
                ) : (
                  <Bars3Icon style={styles.hamburgerIcon} />
                )}
              </button>
            )}
          </div>
        </div>

        {/* Mobile Menu */}
        {isMobile && mobileMenuOpen && (
          <div style={styles.mobileMenu}>
            {navLinks.map((link) => (
              <Link
                key={link.to}
                to={link.to}
                onClick={handleNavClick}
                style={{
                  ...styles.mobileLink,
                  ...(location.pathname === link.to ? styles.activeMobileLink : {})
                }}
              >
                <link.icon style={styles.mobileNavIcon} />
                {link.label}
              </Link>
            ))}
            <button onClick={handleLogout} style={styles.mobileLogoutBtn}>
              <ArrowRightOnRectangleIcon style={styles.mobileNavIcon} />
              {t('navigation.logout')}
            </button>
          </div>
        )}
      </nav>

      <main style={styles.main}>
        {children}
      </main>
    </div>
  );
}

const getStyles = (theme, { isMobile } = {}) => ({
  container: {
    minHeight: '100vh',
    backgroundColor: theme.background,
    color: theme.text,
  },
  nav: {
    backgroundColor: theme.backgroundSecondary,
    padding: isMobile ? '0.75rem 0' : '1rem 0',
    boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06)',
    borderBottom: `1px solid ${theme.border}`,
    position: isMobile ? 'sticky' : 'relative',
    top: 0,
    zIndex: 100,
  },
  navContent: {
    maxWidth: '100%',
    margin: '0 auto',
    padding: isMobile ? '0 1rem' : '0 2rem',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    gap: '1rem',
  },
  logo: {
    color: theme.primary,
    fontSize: isMobile ? '1.25rem' : '1.5rem',
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
    gap: isMobile ? '0.5rem' : '0.75rem',
    flexShrink: 0,
    paddingLeft: isMobile ? '0' : '1rem',
    borderLeft: isMobile ? 'none' : `1px solid ${theme.border}`,
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
  hamburgerBtn: {
    backgroundColor: theme.primary,
    color: 'white',
    border: 'none',
    padding: '0.5rem',
    borderRadius: '6px',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  hamburgerIcon: {
    width: '24px',
    height: '24px',
  },
  mobileMenu: {
    display: 'flex',
    flexDirection: 'column',
    padding: '1rem',
    gap: '0.5rem',
    borderTop: `1px solid ${theme.border}`,
    backgroundColor: theme.backgroundSecondary,
  },
  mobileLink: {
    color: theme.text,
    textDecoration: 'none',
    padding: '0.75rem 1rem',
    borderRadius: '8px',
    fontSize: '1rem',
    fontWeight: '500',
    display: 'flex',
    alignItems: 'center',
    gap: '0.75rem',
    transition: 'all 0.2s ease',
    backgroundColor: theme.cardBackground,
  },
  activeMobileLink: {
    backgroundColor: theme.primary,
    color: 'white',
  },
  mobileNavIcon: {
    width: '20px',
    height: '20px',
  },
  mobileLogoutBtn: {
    backgroundColor: '#ef4444',
    color: 'white',
    border: 'none',
    padding: '0.75rem 1rem',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '1rem',
    fontWeight: '500',
    display: 'flex',
    alignItems: 'center',
    gap: '0.75rem',
    marginTop: '0.5rem',
  },
  main: {
    width: '100%',
    maxWidth: 'none',
    margin: isMobile ? '1rem 0' : '2rem 0',
    padding: isMobile ? '0 0.5rem' : '0 2rem',
  },
});

export default Layout;
