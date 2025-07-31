import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useThemedStyles } from '../hooks/useThemedStyles';
import { useAuth } from '../contexts/AuthContext';
import api from '../services/api';

function EmailChangeConfirmationPage() {
  const styles = useThemedStyles(getStyles);
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { logout } = useAuth();
  const [status, setStatus] = useState('confirming');
  const [message, setMessage] = useState('');

  useEffect(() => {
    const token = searchParams.get('token');
    if (token) {
      confirmEmailChange(token);
    } else {
      setStatus('error');
      setMessage('Invalid confirmation link');
    }
  }, [searchParams]);

  const confirmEmailChange = async (token) => {
    try {
      await api.post('/auth/confirm-email-change', null, { params: { token } });
      setStatus('success');
      setMessage('Your email has been changed successfully! Please login with your new email.');

      // Logout after 3 seconds
      setTimeout(() => {
        logout();
        navigate('/login');
      }, 3000);
    } catch (error) {
      setStatus('error');
      setMessage(error.response?.data?.error || 'Failed to confirm email change.');
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        {status === 'confirming' && (
          <>
            <div style={styles.spinner}></div>
            <h2 style={styles.title}>Confirming Email Change...</h2>
            <p style={styles.text}>Please wait while we update your email address.</p>
          </>
        )}

        {status === 'success' && (
          <>
            <div style={styles.successIcon}>✓</div>
            <h2 style={styles.title}>Email Changed!</h2>
            <p style={styles.text}>{message}</p>
            <p style={styles.text}>Redirecting to login...</p>
          </>
        )}

        {status === 'error' && (
          <>
            <div style={styles.errorIcon}>✗</div>
            <h2 style={styles.title}>Confirmation Failed</h2>
            <p style={styles.text}>{message}</p>
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
});

export default EmailChangeConfirmationPage;
