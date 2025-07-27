import { Link } from 'react-router-dom';
import { useThemedStyles } from '../hooks/useThemedStyles';
import LoginForm from '../components/LoginForm.jsx';

function LoginPage() {
  const styles = useThemedStyles(getStyles);

  return (
    <div style={styles.container}>
      <h1>Login</h1>
      <LoginForm/>
      <div style={styles.forgotPasswordContainer}>
        <Link to="/forgot-password" style={styles.forgotPasswordLink}>
          Forgot your password?
        </Link>
      </div>
    </div>
  );
}

const getStyles = (theme) => ({
  container: {
    padding: 20
  },
  forgotPasswordContainer: {
    textAlign: 'center',
    marginTop: '1rem'
  },
  forgotPasswordLink: {
    color: theme.primary,
    textDecoration: 'none',
    fontSize: '0.875rem'
  }
});

export default LoginPage;
