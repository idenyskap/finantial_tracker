import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { useThemedStyles } from '../hooks/useThemedStyles';
import ThemeToggle from '../components/ThemeToggle';
import {
  ChartBarIcon,
  CurrencyDollarIcon,
  ClockIcon,
  TrophyIcon,
  ShieldCheckIcon,
  SparklesIcon
} from '@heroicons/react/24/outline';

function HomePage() {
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const styles = useThemedStyles(getStyles);

  const handleGetStarted = () => {
    if (isAuthenticated) {
      navigate('/dashboard');
    } else {
      navigate('/register');
    }
  };

  const features = [
    {
      icon: ChartBarIcon,
      title: 'Smart Analytics',
      description: 'Visualize your spending patterns with beautiful charts and insights',
      color: '#3498db',
    },
    {
      icon: CurrencyDollarIcon,
      title: 'Budget Management',
      description: 'Set budgets and get real-time alerts when you\'re close to limits',
      color: '#27ae60',
    },
    {
      icon: ClockIcon,
      title: 'Recurring Transactions',
      description: 'Automate your regular payments and income tracking',
      color: '#f39c12',
    },
    {
      icon: TrophyIcon,
      title: 'Financial Goals',
      description: 'Set savings goals and track your progress towards achieving them',
      color: '#e74c3c',
    },
    {
      icon: ShieldCheckIcon,
      title: 'Secure & Private',
      description: 'Your financial data is encrypted and never shared',
      color: '#9b59b6',
    },
    {
      icon: SparklesIcon,
      title: 'Smart Categories',
      description: 'Organize transactions with customizable categories',
      color: '#1abc9c',
    },
  ];

  return (
    <div style={styles.container}>
      {/* Header */}
      <header style={styles.header}>
        <div style={styles.headerContent}>
          <div style={styles.logo}>
            <CurrencyDollarIcon style={styles.logoIcon} />
            <h1 style={styles.logoText}>FinanceTracker</h1>
          </div>

          <nav style={styles.nav}>
            <ThemeToggle />
            {isAuthenticated ? (
              <>
                <Link to="/dashboard" style={styles.navLink}>Dashboard</Link>
                <Link to="/profile" style={styles.navLinkPrimary}>My Account</Link>
              </>
            ) : (
              <>
                <Link to="/login" style={styles.navLink}>Login</Link>
                <Link to="/register" style={styles.navLinkPrimary}>Sign Up</Link>
              </>
            )}
          </nav>
        </div>
      </header>

      {/* Hero Section */}
      <section style={styles.hero}>
        <div style={styles.heroContent}>
          <h2 style={styles.heroTitle}>
            Take Control of Your Financial Future
          </h2>
          <p style={styles.heroSubtitle}>
            Track expenses, set budgets, and achieve your financial goals with our intuitive personal finance manager.
          </p>
          <div style={styles.heroButtons}>
            <button onClick={handleGetStarted} style={styles.ctaButton}>
              {isAuthenticated ? 'Go to Dashboard' : 'Get Started Free'}
            </button>
            <Link to="/login" style={styles.secondaryButton}>
              Learn More
            </Link>
          </div>

          {/* Demo Stats */}
          <div style={styles.demoStats}>
            <div style={styles.stat}>
              <span style={styles.statValue}>10K+</span>
              <span style={styles.statLabel}>Active Users</span>
            </div>
            <div style={styles.stat}>
              <span style={styles.statValue}>$2M+</span>
              <span style={styles.statLabel}>Money Tracked</span>
            </div>
            <div style={styles.stat}>
              <span style={styles.statValue}>50K+</span>
              <span style={styles.statLabel}>Transactions</span>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section style={styles.features}>
        <div style={styles.featuresContent}>
          <h3 style={styles.sectionTitle}>Everything You Need to Manage Your Money</h3>
          <p style={styles.sectionSubtitle}>
            Powerful features to help you track, analyze, and optimize your finances
          </p>

          <div style={styles.featuresGrid}>
            {features.map((feature, index) => {
              const Icon = feature.icon;
              return (
                <div key={index} style={styles.featureCard}>
                  <div style={{
                    ...styles.featureIcon,
                    backgroundColor: `${feature.color}20`,
                    color: feature.color,
                  }}>
                    <Icon style={styles.icon} />
                  </div>
                  <h4 style={styles.featureTitle}>{feature.title}</h4>
                  <p style={styles.featureDescription}>{feature.description}</p>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section style={styles.cta}>
        <div style={styles.ctaContent}>
          <h3 style={styles.ctaTitle}>Ready to Start Your Financial Journey?</h3>
          <p style={styles.ctaSubtitle}>
            Join thousands of users who are already taking control of their finances
          </p>
          <button onClick={handleGetStarted} style={styles.ctaButtonLarge}>
            {isAuthenticated ? 'Open Dashboard' : 'Create Free Account'}
          </button>
        </div>
      </section>

      {/* Footer */}
      <footer style={styles.footer}>
        <p style={styles.footerText}>
          © 2025 FinanceTracker. Built with ❤️ for better financial health.
        </p>
      </footer>
    </div>
  );
}

const getStyles = (theme) => ({
  container: {
    minHeight: '100vh',
    backgroundColor: theme.background,
    color: theme.text,
  },
  header: {
    borderBottom: `1px solid ${theme.border}`,
    backgroundColor: theme.backgroundSecondary,
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
  },
  logoIcon: {
    width: '32px',
    height: '32px',
    color: theme.primary,
  },
  logoText: {
    margin: 0,
    fontSize: '1.5rem',
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
  },
  navLinkPrimary: {
    padding: '0.5rem 1rem',
    backgroundColor: theme.primary,
    color: 'white',
    textDecoration: 'none',
    borderRadius: '4px',
    transition: 'opacity 0.2s',
  },
  hero: {
    padding: '4rem 2rem',
    textAlign: 'center',
  },
  heroContent: {
    maxWidth: '800px',
    margin: '0 auto',
  },
  heroTitle: {
    fontSize: '3rem',
    marginBottom: '1rem',
    color: theme.text,
  },
  heroSubtitle: {
    fontSize: '1.25rem',
    color: theme.textSecondary,
    marginBottom: '2rem',
  },
  heroButtons: {
    display: 'flex',
    gap: '1rem',
    justifyContent: 'center',
    marginBottom: '3rem',
  },
  ctaButton: {
    padding: '1rem 2rem',
    fontSize: '1.125rem',
    backgroundColor: theme.primary,
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    transition: 'transform 0.2s',
  },
  secondaryButton: {
    padding: '1rem 2rem',
    fontSize: '1.125rem',
    backgroundColor: 'transparent',
    color: theme.primary,
    border: `2px solid ${theme.primary}`,
    borderRadius: '4px',
    textDecoration: 'none',
    transition: 'background-color 0.2s',
  },
  demoStats: {
    display: 'flex',
    justifyContent: 'center',
    gap: '3rem',
  },
  stat: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
  },
  statValue: {
    fontSize: '2rem',
    fontWeight: 'bold',
    color: theme.primary,
  },
  statLabel: {
    fontSize: '0.875rem',
    color: theme.textSecondary,
  },
  features: {
    padding: '4rem 2rem',
    backgroundColor: theme.backgroundSecondary,
  },
  featuresContent: {
    maxWidth: '1200px',
    margin: '0 auto',
  },
  sectionTitle: {
    fontSize: '2.5rem',
    textAlign: 'center',
    marginBottom: '1rem',
  },
  sectionSubtitle: {
    fontSize: '1.125rem',
    textAlign: 'center',
    color: theme.textSecondary,
    marginBottom: '3rem',
  },
  featuresGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
    gap: '2rem',
  },
  featureCard: {
    backgroundColor: theme.cardBackground,
    padding: '2rem',
    borderRadius: '8px',
    boxShadow: theme.shadow,
    textAlign: 'center',
  },
  featureIcon: {
    width: '60px',
    height: '60px',
    borderRadius: '12px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    margin: '0 auto 1rem',
  },
  icon: {
    width: '30px',
    height: '30px',
  },
  featureTitle: {
    fontSize: '1.25rem',
    marginBottom: '0.5rem',
  },
  featureDescription: {
    color: theme.textSecondary,
    lineHeight: '1.6',
  },
  cta: {
    padding: '4rem 2rem',
    backgroundColor: theme.backgroundTertiary,
  },
  ctaContent: {
    maxWidth: '600px',
    margin: '0 auto',
    textAlign: 'center',
  },
  ctaTitle: {
    fontSize: '2rem',
    marginBottom: '1rem',
  },
  ctaSubtitle: {
    fontSize: '1.125rem',
    color: theme.textSecondary,
    marginBottom: '2rem',
  },
  ctaButtonLarge: {
    padding: '1.25rem 3rem',
    fontSize: '1.25rem',
    backgroundColor: theme.success,
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
  },
  footer: {
    padding: '2rem',
    textAlign: 'center',
    borderTop: `1px solid ${theme.border}`,
  },
  footerText: {
    color: theme.textSecondary,
  },
});

export default HomePage;
