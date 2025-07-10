import { useState } from 'react';
import { useQuery, useMutation } from '@tanstack/react-query';
import { userService } from '../services/userService';
import { authService } from '../services/authService';
import { transactionService } from '../services/transactionService';
import { toast } from 'sonner';
import { UserCircleIcon, KeyIcon, TrashIcon, ChartBarIcon } from '@heroicons/react/24/outline';

function ProfilePage() {
  const [activeTab, setActiveTab] = useState('profile');
  const [profileForm, setProfileForm] = useState({ name: '' });
  const [passwordForm, setPasswordForm] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });

  // Fetch user data
  const { data: userData, isLoading } = useQuery({
    queryKey: ['currentUser'],
    queryFn: () => userService.getCurrentUser(),
    onSuccess: (response) => {
      setProfileForm({ name: response.data.name });
    },
  });

  // Fetch statistics
  const { data: statsData } = useQuery({
    queryKey: ['userStats'],
    queryFn: () => transactionService.getStats(),
  });

  // Update profile mutation
  const updateProfileMutation = useMutation({
    mutationFn: userService.updateProfile,
    onSuccess: () => {
      toast.success('Profile updated successfully');
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error updating profile');
    },
  });

  // Change password mutation
  const changePasswordMutation = useMutation({
    mutationFn: userService.changePassword,
    onSuccess: () => {
      toast.success('Password changed successfully');
      setPasswordForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error changing password');
    },
  });

  // Delete account mutation
  const deleteAccountMutation = useMutation({
    mutationFn: userService.deleteAccount,
    onSuccess: () => {
      toast.success('Account deleted successfully');
      authService.logout();
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error deleting account');
    },
  });

  const user = userData?.data;
  const stats = statsData?.data;

  const handleProfileSubmit = (e) => {
    e.preventDefault();
    updateProfileMutation.mutate(profileForm);
  };

  const handlePasswordSubmit = (e) => {
    e.preventDefault();

    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      toast.error('Passwords do not match');
      return;
    }

    if (passwordForm.newPassword.length < 6) {
      toast.error('Password must be at least 6 characters');
      return;
    }

    changePasswordMutation.mutate({
      currentPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword,
    });
  };

  const handleDeleteAccount = () => {
    if (window.confirm('Are you sure you want to delete your account? This action cannot be undone.')) {
      if (window.confirm('This will permanently delete all your data. Are you absolutely sure?')) {
        deleteAccountMutation.mutate();
      }
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount || 0);
  };

  if (isLoading) return <div style={styles.loading}>Loading...</div>;

  return (
    <div style={styles.container}>
      <h1 style={styles.title}>Account Settings</h1>

      <div style={styles.content}>
        {/* Tabs */}
        <div style={styles.tabs}>
          <button
            onClick={() => setActiveTab('profile')}
            style={{
              ...styles.tab,
              ...(activeTab === 'profile' ? styles.activeTab : {}),
            }}
          >
            <UserCircleIcon style={styles.tabIcon} />
            Profile
          </button>
          <button
            onClick={() => setActiveTab('security')}
            style={{
              ...styles.tab,
              ...(activeTab === 'security' ? styles.activeTab : {}),
            }}
          >
            <KeyIcon style={styles.tabIcon} />
            Security
          </button>
          <button
            onClick={() => setActiveTab('statistics')}
            style={{
              ...styles.tab,
              ...(activeTab === 'statistics' ? styles.activeTab : {}),
            }}
          >
            <ChartBarIcon style={styles.tabIcon} />
            Statistics
          </button>
          <button
            onClick={() => setActiveTab('danger')}
            style={{
              ...styles.tab,
              ...(activeTab === 'danger' ? styles.activeTab : {}),
            }}
          >
            <TrashIcon style={styles.tabIcon} />
            Danger Zone
          </button>
        </div>

        {/* Tab Content */}
        <div style={styles.tabContent}>
          {/* Profile Tab */}
          {activeTab === 'profile' && (
            <div style={styles.section}>
              <h2 style={styles.sectionTitle}>Profile Information</h2>
              <form onSubmit={handleProfileSubmit} style={styles.form}>
                <div style={styles.formGroup}>
                  <label style={styles.label}>Email</label>
                  <input
                    type="email"
                    value={user?.email || ''}
                    disabled
                    style={styles.inputDisabled}
                  />
                  <p style={styles.helper}>Email cannot be changed</p>
                </div>

                <div style={styles.formGroup}>
                  <label style={styles.label}>Name</label>
                  <input
                    type="text"
                    value={profileForm.name}
                    onChange={(e) => setProfileForm({ name: e.target.value })}
                    required
                    style={styles.input}
                  />
                </div>

                <button
                  type="submit"
                  disabled={updateProfileMutation.isPending}
                  style={styles.submitButton}
                >
                  {updateProfileMutation.isPending ? 'Saving...' : 'Save Changes'}
                </button>
              </form>
            </div>
          )}

          {/* Security Tab */}
          {activeTab === 'security' && (
            <div style={styles.section}>
              <h2 style={styles.sectionTitle}>Change Password</h2>
              <form onSubmit={handlePasswordSubmit} style={styles.form}>
                <div style={styles.formGroup}>
                  <label style={styles.label}>Current Password</label>
                  <input
                    type="password"
                    value={passwordForm.currentPassword}
                    onChange={(e) => setPasswordForm({ ...passwordForm, currentPassword: e.target.value })}
                    required
                    style={styles.input}
                  />
                </div>

                <div style={styles.formGroup}>
                  <label style={styles.label}>New Password</label>
                  <input
                    type="password"
                    value={passwordForm.newPassword}
                    onChange={(e) => setPasswordForm({ ...passwordForm, newPassword: e.target.value })}
                    required
                    minLength={6}
                    style={styles.input}
                  />
                </div>

                <div style={styles.formGroup}>
                  <label style={styles.label}>Confirm New Password</label>
                  <input
                    type="password"
                    value={passwordForm.confirmPassword}
                    onChange={(e) => setPasswordForm({ ...passwordForm, confirmPassword: e.target.value })}
                    required
                    style={styles.input}
                  />
                </div>

                <button
                  type="submit"
                  disabled={changePasswordMutation.isPending}
                  style={styles.submitButton}
                >
                  {changePasswordMutation.isPending ? 'Changing...' : 'Change Password'}
                </button>
              </form>
            </div>
          )}

          {/* Statistics Tab */}
          {activeTab === 'statistics' && (
            <div style={styles.section}>
              <h2 style={styles.sectionTitle}>Account Statistics</h2>
              <div style={styles.statsGrid}>
                <div style={styles.statCard}>
                  <h3 style={styles.statTitle}>Total Income</h3>
                  <p style={styles.statValue}>{formatCurrency(stats?.income)}</p>
                </div>
                <div style={styles.statCard}>
                  <h3 style={styles.statTitle}>Total Expenses</h3>
                  <p style={styles.statValue}>{formatCurrency(stats?.expense)}</p>
                </div>
                <div style={styles.statCard}>
                  <h3 style={styles.statTitle}>Current Balance</h3>
                  <p style={styles.statValue}>{formatCurrency(stats?.balance)}</p>
                </div>
                <div style={styles.statCard}>
                  <h3 style={styles.statTitle}>Account Created</h3>
                  <p style={styles.statValue}>
                    {user?.createdAt ? new Date(user.createdAt).toLocaleDateString() : 'N/A'}
                  </p>
                </div>
              </div>
            </div>
          )}

          {/* Danger Zone Tab */}
          {activeTab === 'danger' && (
            <div style={styles.section}>
              <h2 style={styles.sectionTitleDanger}>Danger Zone</h2>
              <div style={styles.dangerCard}>
                <div>
                  <h3 style={styles.dangerTitle}>Delete Account</h3>
                  <p style={styles.dangerText}>
                    Once you delete your account, there is no going back. All your data will be permanently removed.
                  </p>
                </div>
                <button
                  onClick={handleDeleteAccount}
                  disabled={deleteAccountMutation.isPending}
                  style={styles.deleteButton}
                >
                  {deleteAccountMutation.isPending ? 'Deleting...' : 'Delete Account'}
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

const styles = {
  container: {
    maxWidth: '800px',
    margin: '0 auto',
  },
  title: {
    fontSize: '2rem',
    marginBottom: '2rem',
  },
  content: {
    backgroundColor: 'white',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    overflow: 'hidden',
  },
  tabs: {
    display: 'flex',
    borderBottom: '1px solid #e0e0e0',
  },
  tab: {
    flex: 1,
    padding: '1rem',
    backgroundColor: 'transparent',
    border: 'none',
    borderBottom: '3px solid transparent',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '0.5rem',
    color: '#666',
    transition: 'all 0.2s',
  },
  activeTab: {
    color: '#3498db',
    borderBottomColor: '#3498db',
    backgroundColor: '#f8f9fa',
  },
  tabIcon: {
    width: '20px',
    height: '20px',
  },
  tabContent: {
    padding: '2rem',
  },
  section: {
    maxWidth: '500px',
  },
  sectionTitle: {
    fontSize: '1.5rem',
    marginBottom: '1.5rem',
  },
  sectionTitleDanger: {
    fontSize: '1.5rem',
    marginBottom: '1.5rem',
    color: '#e74c3c',
  },
  form: {
    display: 'flex',
    flexDirection: 'column',
    gap: '1.5rem',
  },
  formGroup: {
    display: 'flex',
    flexDirection: 'column',
    gap: '0.5rem',
  },
  label: {
    fontWeight: '500',
    color: '#333',
  },
  input: {
    padding: '0.75rem',
    border: '1px solid #ddd',
    borderRadius: '4px',
    fontSize: '1rem',
  },
  inputDisabled: {
    padding: '0.75rem',
    border: '1px solid #ddd',
    borderRadius: '4px',
    fontSize: '1rem',
    backgroundColor: '#f5f5f5',
    color: '#666',
    cursor: 'not-allowed',
  },
  helper: {
    fontSize: '0.875rem',
    color: '#666',
    margin: 0,
  },
  submitButton: {
    padding: '0.75rem',
    backgroundColor: '#3498db',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    fontSize: '1rem',
    cursor: 'pointer',
    transition: 'background-color 0.2s',
  },
  statsGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
    gap: '1rem',
  },
  statCard: {
    padding: '1.5rem',
    backgroundColor: '#f8f9fa',
    borderRadius: '8px',
    textAlign: 'center',
  },
  statTitle: {
    fontSize: '0.875rem',
    color: '#666',
    marginBottom: '0.5rem',
  },
  statValue: {
    fontSize: '1.5rem',
    fontWeight: 'bold',
    color: '#333',
    margin: 0,
  },
  dangerCard: {
    padding: '1.5rem',
    border: '2px solid #e74c3c',
    borderRadius: '8px',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: '#fff5f5',
  },
  dangerTitle: {
    margin: '0 0 0.5rem 0',
    color: '#e74c3c',
  },
  dangerText: {
    margin: 0,
    color: '#666',
  },
  deleteButton: {
    padding: '0.75rem 1.5rem',
    backgroundColor: '#e74c3c',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontWeight: '500',
  },
  loading: {
    textAlign: 'center',
    padding: '2rem',
  },
};

export default ProfilePage;
