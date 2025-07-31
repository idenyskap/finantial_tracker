import React, {useState, useEffect} from 'react';
import {useAuth} from '../contexts/AuthContext';
import {useThemedStyles} from '../hooks/useThemedStyles';
import api from '../services/api';
import {toast} from 'sonner';
import PasswordChangeModal from '../components/profile/PasswordChangeModal';
import NotificationSettings from '../components/profile/NotificationSettings';
import ChangeEmailModal from '../components/profile/ChangeEmailModal';

function ProfilePage() {
  const {user} = useAuth();
  const styles = useThemedStyles(getStyles);
  const [activeTab, setActiveTab] = useState('general');
  const [userData, setUserData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [showEmailModal, setShowEmailModal] = useState(false);

  useEffect(() => {
    fetchUserData();
  }, []);

  const fetchUserData = async () => {
    try {
      const response = await api.get('/users/profile');
      setUserData(response.data);
    } catch (error) {
      toast.error('Failed to load profile');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    try {
      const response = await api.put('/users/profile', {
        name: userData.name
      });
      setUserData(response.data);
      toast.success('Profile updated successfully');
    } catch (error) {
      toast.error('Failed to update profile');
    }
  };

  const resendVerification = async () => {
    try {
      await api.post('/auth/resend-verification', { email: userData.email });
      toast.success('Verification email sent!');
    } catch (error) {
      toast.error('Failed to send verification email');
    }
  };

  if (loading) {
    return <div style={styles.loading}>Loading...</div>;
  }

  return (
    <div style={styles.container}>
      <h1 style={styles.title}>Profile Settings</h1>

      <div style={styles.tabContainer}>
        <button
          style={{
            ...styles.tab,
            ...(activeTab === 'general' ? styles.activeTab : {})
          }}
          onClick={() => setActiveTab('general')}
        >
          General
        </button>
        <button
          style={{
            ...styles.tab,
            ...(activeTab === 'notifications' ? styles.activeTab : {})
          }}
          onClick={() => setActiveTab('notifications')}
        >
          Notifications
        </button>
      </div>

      <div style={styles.content}>
        {activeTab === 'general' && (
          <div style={styles.card}>
            <h2 style={styles.cardTitle}>General Information</h2>

            <form onSubmit={handleUpdateProfile} style={styles.form}>
              <div style={styles.formGroup}>
                <label style={styles.label}>Email</label>
                <input
                  type="email"
                  value={userData?.email || ''}
                  disabled
                  style={{...styles.input, ...styles.disabledInput}}
                />
                {userData && !userData.emailVerified && (
                  <div style={styles.warningMessage}>
                    <span>⚠️ Your email is not verified. </span>
                    <button
                      type="button"
                      onClick={resendVerification}
                      style={styles.linkButton}
                    >
                      Resend verification email
                    </button>
                  </div>
                )}
                {userData?.newEmail && (
                  <div style={styles.infoMessage}>
                    <span>📧 Pending email change to: {userData.newEmail}</span>
                  </div>
                )}
              </div>

              <div style={styles.formGroup}>
                <label style={styles.label}>Name</label>
                <input
                  type="text"
                  value={userData?.name || ''}
                  onChange={(e) => setUserData({...userData, name: e.target.value})}
                  style={styles.input}
                  required
                />
              </div>

              <div style={styles.formActions}>
                <button type="submit" style={styles.primaryButton}>
                  Update Profile
                </button>
                <button
                  type="button"
                  onClick={() => setShowPasswordModal(true)}
                  style={styles.secondaryButton}
                >
                  Change Password
                </button>
                <button
                  type="button"
                  onClick={() => setShowEmailModal(true)}
                  style={styles.secondaryButton}
                >
                  Change Email
                </button>
              </div>
            </form>

            <div style={styles.section}>
              <h3 style={styles.sectionTitle}>Account Information</h3>
              <div style={styles.infoGrid}>
                <div style={styles.infoItem}>
                  <span style={styles.infoLabel}>Member Since</span>
                  <span style={styles.infoValue}>
                    {new Date(userData?.createdAt).toLocaleDateString()}
                  </span>
                </div>
                <div style={styles.infoItem}>
                  <span style={styles.infoLabel}>Account Status</span>
                  <span style={{...styles.infoValue, color: '#4CAF50'}}>Active</span>
                </div>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'notifications' && (
          <NotificationSettings/>
        )}
      </div>

      {showPasswordModal && (
        <PasswordChangeModal
          onClose={() => setShowPasswordModal(false)}
          onSuccess={() => {
            setShowPasswordModal(false);
            toast.success('Password changed successfully');
          }}
        />
      )}

      {showEmailModal && (
        <ChangeEmailModal
          currentEmail={userData?.email || ''}
          onClose={() => setShowEmailModal(false)}
          onSuccess={() => {
            setShowEmailModal(false);
            toast.info('Check your new email for confirmation link');
          }}
        />
      )}
    </div>
  );
}

const getStyles = (theme) => ({
  container: {
    maxWidth: '1000px',
    margin: '0 auto',
  },
  title: {
    fontSize: '2rem',
    fontWeight: 'bold',
    marginBottom: '2rem',
    color: theme.text,
  },
  tabContainer: {
    display: 'flex',
    gap: '1rem',
    marginBottom: '2rem',
    borderBottom: `2px solid ${theme.border}`,
  },
  tab: {
    padding: '0.75rem 1.5rem',
    backgroundColor: 'transparent',
    border: 'none',
    color: theme.textSecondary,
    fontSize: '1rem',
    cursor: 'pointer',
    transition: 'all 0.2s',
    borderBottom: '2px solid transparent',
    marginBottom: '-2px',
  },
  activeTab: {
    color: theme.primary,
    borderBottomColor: theme.primary,
  },
  content: {
    minHeight: '400px',
  },
  card: {
    backgroundColor: theme.backgroundSecondary,
    borderRadius: '8px',
    padding: '2rem',
    boxShadow: theme.shadow,
  },
  cardTitle: {
    fontSize: '1.5rem',
    marginBottom: '1.5rem',
    color: theme.text,
  },
  form: {
    marginBottom: '2rem',
  },
  formGroup: {
    marginBottom: '1.5rem',
  },
  label: {
    display: 'block',
    marginBottom: '0.5rem',
    fontWeight: '500',
    color: theme.text,
  },
  input: {
    width: '100%',
    padding: '0.75rem',
    borderRadius: '4px',
    border: `1px solid ${theme.border}`,
    backgroundColor: theme.background,
    color: theme.text,
    fontSize: '1rem',
  },
  disabledInput: {
    opacity: 0.6,
    cursor: 'not-allowed',
  },
  warningMessage: {
    marginTop: '0.5rem',
    padding: '0.75rem',
    backgroundColor: '#fff3cd',
    border: '1px solid #ffeaa7',
    borderRadius: '4px',
    fontSize: '0.875rem',
    color: '#856404',
  },
  infoMessage: {
    marginTop: '0.5rem',
    padding: '0.75rem',
    backgroundColor: '#d1ecf1',
    border: '1px solid #bee5eb',
    borderRadius: '4px',
    fontSize: '0.875rem',
    color: '#0c5460',
  },
  linkButton: {
    background: 'none',
    border: 'none',
    color: '#007bff',
    cursor: 'pointer',
    textDecoration: 'underline',
    padding: 0,
    font: 'inherit',
  },
  formActions: {
    display: 'flex',
    gap: '1rem',
    marginTop: '2rem',
  },
  primaryButton: {
    padding: '0.75rem 1.5rem',
    backgroundColor: theme.primary,
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    fontSize: '1rem',
    cursor: 'pointer',
    transition: 'opacity 0.2s',
  },
  secondaryButton: {
    padding: '0.75rem 1.5rem',
    backgroundColor: 'transparent',
    color: theme.primary,
    border: `1px solid ${theme.primary}`,
    borderRadius: '4px',
    fontSize: '1rem',
    cursor: 'pointer',
    transition: 'all 0.2s',
  },
  section: {
    marginTop: '2rem',
    paddingTop: '2rem',
    borderTop: `1px solid ${theme.border}`,
  },
  sectionTitle: {
    fontSize: '1.25rem',
    marginBottom: '1rem',
    color: theme.text,
  },
  infoGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
    gap: '1rem',
  },
  infoItem: {
    display: 'flex',
    flexDirection: 'column',
  },
  infoLabel: {
    fontSize: '0.875rem',
    color: theme.textSecondary,
    marginBottom: '0.25rem',
  },
  infoValue: {
    fontSize: '1rem',
    fontWeight: '500',
    color: theme.text,
  },
  loading: {
    textAlign: 'center',
    padding: '2rem',
    color: theme.textSecondary,
  },
});

export default ProfilePage;
