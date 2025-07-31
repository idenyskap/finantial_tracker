import { useState } from 'react';

function GoalCard({ goal, onEdit, onDelete, onContribute, onStatusChange }) {
  const [showContribution, setShowContribution] = useState(false);
  const [contributionAmount, setContributionAmount] = useState('');
  const [contributionType, setContributionType] = useState('ADD');

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount);
  };

  const getProgressColor = () => {
    if (goal.isCompleted) return '#27ae60';
    if (goal.isOverdue) return '#e74c3c';
    if (goal.progressPercentage >= 75) return '#f39c12';
    return '#3498db';
  };

  const getStatusIcon = () => {
    switch (goal.status) {
      case 'COMPLETED':
        return '✓';
      case 'PAUSED':
        return '❚❚';
      case 'CANCELLED':
        return '✗';
      default:
        return goal.isOverdue ? '!' : null;
    }
  };

  const handleContribute = () => {
    if (!contributionAmount) return;

    onContribute({
      goalId: goal.id,
      amount: parseFloat(contributionAmount),
      type: contributionType,
      note: '',
    });

    setContributionAmount('');
    setShowContribution(false);
  };

  const progressPercentage = Math.min(goal.progressPercentage || 0, 100);

  return (
    <div style={styles.card}>
      <div style={styles.header}>
        <div style={styles.titleSection}>
          <div style={styles.titleRow}>
            <h3 style={styles.name}>{goal.name}</h3>
            {getStatusIcon() && (
              <span style={{
                ...styles.statusIcon,
                color: goal.status === 'COMPLETED' ? '#27ae60' :
                  goal.status === 'CANCELLED' || goal.isOverdue ? '#e74c3c' :
                    '#f39c12'
              }}>
                {getStatusIcon()}
              </span>
            )}
          </div>
          <div style={styles.badges}>
            <span style={{
              ...styles.priorityBadge,
              backgroundColor: goal.priority === 'CRITICAL' ? '#e74c3c' :
                goal.priority === 'HIGH' ? '#f39c12' :
                  goal.priority === 'MEDIUM' ? '#3498db' : '#95a5a6'
            }}>
              {goal.priority}
            </span>
            {goal.categoryName && (
              <span style={styles.categoryBadge}>
                <div style={{
                  ...styles.categoryDot,
                  backgroundColor: goal.categoryColor || '#666',
                }} />
                <span style={styles.categoryName}>{goal.categoryName}</span>
              </span>
            )}
          </div>
        </div>

        {goal.status === 'ACTIVE' && (
          <div style={styles.actions}>
            <button
              onClick={() => setShowContribution(!showContribution)}
              style={styles.actionButton}
              title="Add/Withdraw funds"
            >
              Add Funds
            </button>
            <button
              onClick={() => onStatusChange(goal.id, 'pause')}
              style={styles.pauseButton}
              title="Pause goal"
            >
              Pause
            </button>
            <button
              onClick={() => onEdit(goal)}
              style={styles.editButton}
              title="Edit"
            >
              Edit
            </button>
            <button
              onClick={() => onDelete(goal.id)}
              style={styles.deleteButton}
              title="Delete"
            >
              Delete
            </button>
          </div>
        )}

        {goal.status === 'PAUSED' && (
          <div style={styles.actions}>
            <button
              onClick={() => onStatusChange(goal.id, 'resume')}
              style={styles.resumeButton}
              title="Resume goal"
            >
              Resume
            </button>
          </div>
        )}
      </div>

      {goal.description && (
        <p style={styles.description}>{goal.description}</p>
      )}

      <div style={styles.progressSection}>
        <div style={styles.amounts}>
          <span style={styles.currentAmount}>{formatCurrency(goal.currentAmount)}</span>
          <span style={styles.targetAmount}>of {formatCurrency(goal.targetAmount)}</span>
        </div>

        <div style={styles.progressBar}>
          <div
            style={{
              ...styles.progressFill,
              width: `${progressPercentage}%`,
              backgroundColor: getProgressColor(),
            }}
          />
        </div>

        <div style={styles.progressStats}>
          <span>{progressPercentage.toFixed(1)}% complete</span>
          <span>{formatCurrency(goal.remainingAmount)} to go</span>
        </div>
      </div>

      {showContribution && (
        <div style={styles.contributionForm}>
          <div style={styles.contributionRow}>
            <input
              type="number"
              step="0.01"
              placeholder="Amount"
              value={contributionAmount}
              onChange={(e) => setContributionAmount(e.target.value)}
              style={styles.contributionInput}
            />
            <select
              value={contributionType}
              onChange={(e) => setContributionType(e.target.value)}
              style={styles.contributionSelect}
            >
              <option value="ADD">Add funds</option>
              <option value="WITHDRAW">Withdraw</option>
            </select>
            <button onClick={handleContribute} style={styles.contributionButton}>
              Confirm
            </button>
          </div>
        </div>
      )}

      <div style={styles.details}>
        <div style={styles.detailItem}>
          <span style={styles.detailLabel}>Target Date:</span>
          <span style={styles.detailValue}>
            {new Date(goal.targetDate).toLocaleDateString()}
          </span>
        </div>

        {goal.daysRemaining !== undefined && goal.status === 'ACTIVE' && (
          <div style={styles.detailItem}>
            <span style={styles.detailLabel}>Days Remaining:</span>
            <span style={{
              ...styles.detailValue,
              color: goal.daysRemaining < 30 ? '#e74c3c' : '#2c3e50',
            }}>
              {goal.daysRemaining > 0 ? goal.daysRemaining : 'Overdue'}
            </span>
          </div>
        )}
      </div>

      {goal.status === 'ACTIVE' && goal.remainingAmount > 0 && goal.daysRemaining > 0 && (
        <div style={styles.savingsRequired}>
          <h4 style={styles.savingsTitle}>To reach your goal, save:</h4>
          <div style={styles.savingsGrid}>
            <div style={styles.savingsItem}>
              <span style={styles.savingsAmount}>
                {formatCurrency(goal.requiredDailySaving)}
              </span>
              <span style={styles.savingsPeriod}>per day</span>
            </div>
            <div style={styles.savingsItem}>
              <span style={styles.savingsAmount}>
                {formatCurrency(goal.requiredWeeklySaving)}
              </span>
              <span style={styles.savingsPeriod}>per week</span>
            </div>
            <div style={styles.savingsItem}>
              <span style={styles.savingsAmount}>
                {formatCurrency(goal.requiredMonthlySaving)}
              </span>
              <span style={styles.savingsPeriod}>per month</span>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

const styles = {
  card: {
    backgroundColor: 'white',
    borderRadius: '8px',
    padding: '1.5rem',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: '1rem',
  },
  titleSection: {
    flex: 1,
  },
  titleRow: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
    marginBottom: '0.5rem',
  },
  name: {
    margin: 0,
    fontSize: '1.25rem',
    fontWeight: '600',
    color: '#2c3e50',
  },
  statusIcon: {
    fontSize: '1.25rem',
    fontWeight: 'bold',
  },
  badges: {
    display: 'flex',
    gap: '0.5rem',
  },
  priorityBadge: {
    padding: '0.25rem 0.75rem',
    borderRadius: '4px',
    color: 'white',
    fontSize: '0.75rem',
    fontWeight: 'bold',
    textTransform: 'uppercase',
  },
  categoryBadge: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.25rem',
    padding: '0.25rem 0.75rem',
    backgroundColor: '#f8f9fa',
    borderRadius: '4px',
    fontSize: '0.75rem',
  },
  categoryDot: {
    width: '8px',
    height: '8px',
    borderRadius: '50%',
  },
  categoryName: {
    fontWeight: '600',
    color: '#2c3e50',
  },
  actions: {
    display: 'flex',
    gap: '0.5rem',
  },
  actionButton: {
    padding: '0.5rem 0.75rem',
    backgroundColor: '#27ae60',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.75rem',
    fontWeight: '500',
  },
  pauseButton: {
    padding: '0.5rem 0.75rem',
    backgroundColor: '#f39c12',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.75rem',
    fontWeight: '500',
  },
  editButton: {
    padding: '0.5rem 0.75rem',
    backgroundColor: '#3498db',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.75rem',
    fontWeight: '500',
  },
  deleteButton: {
    padding: '0.5rem 0.75rem',
    backgroundColor: '#e74c3c',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.75rem',
    fontWeight: '500',
  },
  resumeButton: {
    padding: '0.5rem 0.75rem',
    backgroundColor: '#27ae60',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.75rem',
    fontWeight: '500',
  },
  description: {
    margin: '0 0 1rem 0',
    color: '#666',
    fontSize: '0.875rem',
  },
  progressSection: {
    marginBottom: '1rem',
  },
  amounts: {
    display: 'flex',
    justifyContent: 'space-between',
    marginBottom: '0.5rem',
  },
  currentAmount: {
    fontSize: '1.5rem',
    fontWeight: 'bold',
    color: '#2c3e50',
  },
  targetAmount: {
    fontSize: '1rem',
    color: '#666',
    fontWeight: '600',
  },
  progressBar: {
    height: '12px',
    backgroundColor: '#f0f0f0',
    borderRadius: '6px',
    overflow: 'hidden',
    marginBottom: '0.5rem',
  },
  progressFill: {
    height: '100%',
    transition: 'width 0.3s ease',
  },
  progressStats: {
    display: 'flex',
    justifyContent: 'space-between',
    fontSize: '0.875rem',
    color: '#666',
  },
  contributionForm: {
    padding: '1rem',
    backgroundColor: '#f8f9fa',
    borderRadius: '4px',
    marginBottom: '1rem',
  },
  contributionRow: {
    display: 'flex',
    gap: '0.5rem',
  },
  contributionInput: {
    flex: 1,
    padding: '0.5rem',
    border: '1px solid #ddd',
    borderRadius: '4px',
  },
  contributionSelect: {
    padding: '0.5rem',
    border: '1px solid #ddd',
    borderRadius: '4px',
  },
  contributionButton: {
    padding: '0.5rem 1rem',
    backgroundColor: '#3498db',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontWeight: '500',
  },
  details: {
    display: 'flex',
    gap: '2rem',
    marginBottom: '1rem',
    paddingTop: '1rem',
    borderTop: '1px solid #f0f0f0',
  },
  detailItem: {
    display: 'flex',
    flexDirection: 'column',
    gap: '0.25rem',
  },
  detailLabel: {
    fontSize: '0.75rem',
    color: '#888',
  },
  detailValue: {
    fontSize: '0.875rem',
    fontWeight: '600',
    color: '#2c3e50',
  },
  savingsRequired: {
    backgroundColor: '#e3f2fd',
    padding: '1rem',
    borderRadius: '4px',
  },
  savingsTitle: {
    margin: '0 0 0.75rem 0',
    fontSize: '0.875rem',
    color: '#1976d2',
    fontWeight: '600',
  },
  savingsGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(3, 1fr)',
    gap: '1rem',
  },
  savingsItem: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
  },
  savingsAmount: {
    fontSize: '1.125rem',
    fontWeight: 'bold',
    color: '#1976d2',
  },
  savingsPeriod: {
    fontSize: '0.75rem',
    color: '#666',
  },
};

export default GoalCard;
