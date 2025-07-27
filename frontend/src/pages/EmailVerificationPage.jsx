import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import { useThemedStyles } from '../hooks/useThemedStyles';
import api from '../services/api';

function EmailVerificationPage() {
  const styles = useThemedStyles(getStyles);
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [status, setStatus] = useState('verifying'); // verifying, success, error
  const [message, setMessage] = useState('');

  useEffect(() => {
    const token = searchParams.get('token');
    if (token) {
      verifyEmail(token);
    } else {
      setStatus('error');
      setMessage('Invalid verification link');
    }
  }, [searchParams]);

  const verifyEmail = async (token) => {
    try {
      await api.post('/auth/verify-email', null, { params: { token } });
      setStatus('success');
      setMessage('Your email has been verified successfully!');

      setTimeout(() => {
        navigate('/login');
      }, 3000);
    } catch (error) {
      setStatus('error');
      setMessage(error.response?.data?.error || 'Verification failed. The link may be invalid or expired.');
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        {status === 'verifying' && (
          <>
            <div style={styles.spinner}></div>
            <h2 style={styles.title}>Verifying your email...</h2>
            <p style={styles.text}>Please wait while we verify your email address.</p>
          </>
        )}

        {status === 'success' && (
          <>
            <div style={styles.successIcon}>✓</div>
            <h2 style={styles.title}>Email Verified!</h2>
            <p style={styles.text}>{message}</p>
            <p style={styles.text}>Redirecting to login...</p>
          </>
        )}

        {status === 'error' && (
          <>
            <div style={styles.errorIcon}>✗</div>
            <h2 style={styles.title}>Verification Failed</h2>
            <p style={styles.text}>{message}</p>
            <Link to="/login" style={styles.link}>
              Go to Login
            </Link>
          </>
        )}
      </div>
    </div>
  );
}

const getStyles = (theme) => ({
  container: {
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: theme.background,
  },
  card: {
    backgroundColor: theme.backgroundSecondary,
    padding: '3rem',
    borderRadius: '8px',
    boxShadow: theme.shadow,
    textAlign: 'center',
    maxWidth: '400px',
    width: '100%',
  },
  spinner: {
    width: '50px',
    height: '50px',
    border: `3px solid ${theme.border}`,
    borderTopColor: theme.primary,
    borderRadius: '50%',
    animation: 'spin 1s linear infinite',
    margin: '0 auto 2rem',
  },
  successIcon: {
    fontSize: '4rem',
    color: '#4CAF50',
    marginBottom: '1rem',
  },
  errorIcon: {
    fontSize: '4rem',
    color: theme.danger,
    marginBottom: '1rem',
  },
  title: {
    fontSize: '1.5rem',
    marginBottom: '1rem',
    color: theme.text,
  },
  text: {
    color: theme.textSecondary,
    marginBottom: '1rem',
  },
  link: {
    color: theme.primary,
    textDecoration: 'none',
    fontWeight: '500',
  },
});

const style = document.createElement('style');
style.textContent = `
  @keyframes spin {
    to { transform: rotate(360deg); }
  }
`;
document.head.appendChild(style);

export default EmailVerificationPage;
