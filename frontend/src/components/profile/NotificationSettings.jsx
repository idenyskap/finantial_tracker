import React, { useState, useEffect } from 'react';
import { useThemedStyles } from '../../hooks/useThemedStyles';
import api from '../../services/api';
import { toast } from 'sonner';
import './NotificationSettings.css';

function NotificationSettings() {
  const styles = useThemedStyles(getStyles);
  const [settings, setSettings] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    fetchSettings();
  }, []);

  const fetchSettings = async () => {
    try {
      const response = await api.get('/notifications/settings');
      setSettings(response.data);
    } catch (error) {
      if (error.response?.status === 404) {
        try {
          const initResponse = await api.post('/notifications/settings/init');
          setSettings(initResponse.data);
        } catch (initError) {
          toast.error('Failed to initialize notification settings');
        }
      } else {
        toast.error('Failed to load notification settings');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleToggle = (field) => {
    setSettings(prev => ({
      ...prev,
      [field]: !prev[field]
    }));
  };

  const handleTimeChange = (e) => {
    setSettings(prev => ({
      ...prev,
      dailyReminderTime: e.target.value
    }));
  };

  const handleDaysChange = (e) => {
    const value = parseInt(e.target.value);
    if (value >= 0 && value <= 7) {
      setSettings(prev => ({
        ...prev,
        paymentReminderDays: value
      }));
    }
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      await api.put('/notifications/settings', settings);
      toast.success('Notification settings saved successfully');
    } catch (error) {
      toast.error('Failed to save notification settings');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <div style={styles.loading}>Loading notification settings...</div>;
  }

  return (
    <div style={styles.card}>
      <h2 style={styles.cardTitle}>Email Notifications</h2>

      <div style={styles.settingsContainer}>
        {/* Master toggle */}
        <div style={styles.settingGroup}>
          <label className="notification-toggle">
            <input
              type="checkbox"
              checked={settings?.emailEnabled || false}
              onChange={() => handleToggle('emailEnabled')}
            />
            <span className="toggle-slider"></span>
            <div style={styles.toggleContent}>
              <span style={styles.toggleLabel}>Email Notifications</span>
              <span style={styles.toggleDescription}>
                Master switch for all email notifications
              </span>
            </div>
          </label>
        </div>

        <div style={styles.divider}></div>

        {/* Individual notification settings */}
        <div style={{ opacity: settings?.emailEnabled ? 1 : 0.5 }}>
          {/* Analytics Reports */}
          <div style={styles.section}>
            <h3 style={styles.sectionTitle}>📊 Analytics & Reports</h3>

            <div style={styles.settingGroup}>
              <label className="notification-toggle">
                <input
                  type="checkbox"
                  checked={settings?.weeklyReport || false}
                  onChange={() => handleToggle('weeklyReport')}
                  disabled={!settings?.emailEnabled}
                />
                <span className="toggle-slider"></span>
                <div style={styles.toggleContent}>
                  <span style={styles.toggleLabel}>Weekly Reports</span>
                  <span style={styles.toggleDescription}>
                    Receive a summary of your finances every Monday
                  </span>
                </div>
              </label>
            </div>

            <div style={styles.settingGroup}>
              <label className="notification-toggle">
                <input
                  type="checkbox"
                  checked={settings?.monthlyReport || false}
                  onChange={() => handleToggle('monthlyReport')}
                  disabled={!settings?.emailEnabled}
                />
                <span className="toggle-slider"></span>
                <div style={styles.toggleContent}>
                  <span style={styles.toggleLabel}>Monthly Reports</span>
                  <span style={styles.toggleDescription}>
                    Comprehensive monthly financial analysis on the 1st of each month
                  </span>
                </div>
              </label>
            </div>
          </div>

          {/* Reminders */}
          <div style={styles.section}>
            <h3 style={styles.sectionTitle}>🔔 Reminders</h3>

            <div style={styles.settingGroup}>
              <label className="notification-toggle">
                <input
                  type="checkbox"
                  checked={settings?.dailyReminder || false}
                  onChange={() => handleToggle('dailyReminder')}
                  disabled={!settings?.emailEnabled}
                />
                <span className="toggle-slider"></span>
                <div style={styles.toggleContent}>
                  <span style={styles.toggleLabel}>Daily Expense Reminder</span>
                  <span style={styles.toggleDescription}>
                    Remind me to log my daily expenses
                  </span>
                </div>
              </label>

              {settings?.dailyReminder && settings?.emailEnabled && (
                <div style={styles.subSetting}>
                  <label style={styles.subLabel}>Reminder Time</label>
                  <input
                    type="time"
                    value={settings?.dailyReminderTime || '21:00'}
                    onChange={handleTimeChange}
                    style={styles.timeInput}
                  />
                </div>
              )}
            </div>

            <div style={styles.settingGroup}>
              <label className="notification-toggle">
                <input
                  type="checkbox"
                  checked={settings?.paymentReminders || false}
                  onChange={() => handleToggle('paymentReminders')}
                  disabled={!settings?.emailEnabled}
                />
                <span className="toggle-slider"></span>
                <div style={styles.toggleContent}>
                  <span style={styles.toggleLabel}>Payment Reminders</span>
                  <span style={styles.toggleDescription}>
                    Notify me about upcoming recurring payments
                  </span>
                </div>
              </label>

              {settings?.paymentReminders && settings?.emailEnabled && (
                <div style={styles.subSetting}>
                  <label style={styles.subLabel}>Days in advance</label>
                  <input
                    type="number"
                    min="0"
                    max="7"
                    value={settings?.paymentReminderDays || 1}
                    onChange={handleDaysChange}
                    style={styles.numberInput}
                  />
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      <div style={styles.actions}>
        <button
          onClick={handleSave}
          disabled={saving}
          style={styles.saveButton}
        >
          {saving ? 'Saving...' : 'Save Settings'}
        </button>
      </div>
    </div>
  );
}

const getStyles = (theme) => ({
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
  settingsContainer: {
    maxWidth: '600px',
  },
  section: {
    marginBottom: '2rem',
  },
  sectionTitle: {
    fontSize: '1.125rem',
    fontWeight: '600',
    marginBottom: '1rem',
    color: theme.text,
  },
  settingGroup: {
    marginBottom: '1.5rem',
  },
  toggleContent: {
    display: 'flex',
    flexDirection: 'column',
    marginLeft: '1rem',
  },
  toggleLabel: {
    fontSize: '1rem',
    fontWeight: '500',
    color: theme.text,
    marginBottom: '0.25rem',
  },
  toggleDescription: {
    fontSize: '0.875rem',
    color: theme.textSecondary,
    lineHeight: '1.4',
  },
  subSetting: {
    marginTop: '1rem',
    marginLeft: '66px',
    display: 'flex',
    alignItems: 'center',
    gap: '1rem',
  },
  subLabel: {
    fontSize: '0.875rem',
    color: theme.textSecondary,
  },
  timeInput: {
    padding: '0.5rem',
    borderRadius: '4px',
    border: `1px solid ${theme.border}`,
    backgroundColor: theme.background,
    color: theme.text,
  },
  numberInput: {
    width: '60px',
    padding: '0.5rem',
    borderRadius: '4px',
    border: `1px solid ${theme.border}`,
    backgroundColor: theme.background,
    color: theme.text,
  },
  divider: {
    height: '1px',
    backgroundColor: theme.border,
    margin: '2rem 0',
  },
  actions: {
    marginTop: '2rem',
    paddingTop: '2rem',
    borderTop: `1px solid ${theme.border}`,
  },
  saveButton: {
    padding: '0.75rem 2rem',
    backgroundColor: theme.primary,
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    fontSize: '1rem',
    cursor: 'pointer',
    transition: 'opacity 0.2s',
  },
  loading: {
    textAlign: 'center',
    padding: '2rem',
    color: theme.textSecondary,
  },
});

export default NotificationSettings;
