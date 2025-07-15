function RecurringTransactionCard({ transaction, onEdit, onDelete, onExecute }) {
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount);
  };

  const getFrequencyLabel = (frequency) => {
    const labels = {
      DAILY: 'Daily',
      WEEKLY: 'Weekly',
      BIWEEKLY: 'Bi-weekly',
      MONTHLY: 'Monthly',
      QUARTERLY: 'Quarterly',
      YEARLY: 'Yearly',
    };
    return labels[frequency] || frequency;
  };

  const getDayLabel = () => {
    if (transaction.frequency === 'MONTHLY' && transaction.dayOfMonth) {
      return `on day ${transaction.dayOfMonth}`;
    }
    if (transaction.frequency === 'WEEKLY' && transaction.dayOfWeek) {
      const days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
      return `on ${days[transaction.dayOfWeek - 1]}`;
    }
    return '';
  };

  return (
    <div style={styles.card}>
      <div style={styles.header}>
        <div style={styles.titleSection}>
          <h3 style={styles.name}>{transaction.name}</h3>
          <div style={styles.badges}>
            <span style={{
              ...styles.typeBadge,
              backgroundColor: transaction.type === 'INCOME' ? '#27ae60' : '#e74c3c',
            }}>
              {transaction.type}
            </span>
            <span style={{
              ...styles.statusBadge,
              backgroundColor: transaction.active ? '#3498db' : '#95a5a6',
            }}>
              {transaction.active ? 'Active' : 'Inactive'}
            </span>
          </div>
        </div>
        <div style={styles.actions}>
          <button
            onClick={() => onExecute(transaction.id)}
            style={styles.actionButton}
            title="Execute now"
          >
            Run Now
          </button>
          <button
            onClick={() => onEdit(transaction)}
            style={styles.editButton}
            title="Edit"
          >
            Edit
          </button>
          <button
            onClick={() => onDelete(transaction.id)}
            style={styles.deleteButton}
            title="Delete"
          >
            Delete
          </button>
        </div>
      </div>

      <div style={styles.body}>
        <div style={styles.amount}>
          <span style={{
            color: transaction.type === 'INCOME' ? '#27ae60' : '#e74c3c',
          }}>
            {transaction.type === 'INCOME' ? '+' : '-'}
            {formatCurrency(transaction.amount)}
          </span>
          <span style={styles.frequency}>
            {getFrequencyLabel(transaction.frequency)} {getDayLabel()}
          </span>
        </div>

        <div style={styles.details}>
          <div style={styles.categoryTag}>
            <div style={{
              ...styles.categoryDot,
              backgroundColor: transaction.categoryColor,
            }} />
            <span style={styles.categoryName}>{transaction.categoryName}</span>
          </div>
          {transaction.description && (
            <p style={styles.description}>{transaction.description}</p>
          )}
        </div>

        <div style={styles.dates}>
          <div>
            <span style={styles.dateLabel}>Next execution:</span>
            <span style={styles.dateValue}>
              {new Date(transaction.nextExecutionDate).toLocaleDateString()}
            </span>
          </div>
          {transaction.lastExecutionDate && (
            <div>
              <span style={styles.dateLabel}>Last executed:</span>
              <span style={styles.dateValue}>
                {new Date(transaction.lastExecutionDate).toLocaleDateString()}
              </span>
            </div>
          )}
        </div>
      </div>
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
  name: {
    margin: '0 0 0.5rem 0',
    fontSize: '1.25rem',
    fontWeight: '600',
    color: '#2c3e50',
  },
  badges: {
    display: 'flex',
    gap: '0.5rem',
  },
  typeBadge: {
    padding: '0.25rem 0.75rem',
    borderRadius: '4px',
    color: 'white',
    fontSize: '0.75rem',
    fontWeight: 'bold',
  },
  statusBadge: {
    padding: '0.25rem 0.75rem',
    borderRadius: '4px',
    color: 'white',
    fontSize: '0.75rem',
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
  body: {
    borderTop: '1px solid #f0f0f0',
    paddingTop: '1rem',
  },
  amount: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '1rem',
    fontSize: '1.5rem',
    fontWeight: 'bold',
  },
  frequency: {
    fontSize: '0.875rem',
    color: '#666',
    fontWeight: 'normal',
  },
  details: {
    marginBottom: '1rem',
  },
  categoryTag: {
    display: 'inline-flex',
    alignItems: 'center',
    gap: '0.5rem',
    padding: '0.25rem 0.75rem',
    backgroundColor: '#f8f9fa',
    borderRadius: '4px',
    fontSize: '0.875rem',
  },
  categoryDot: {
    width: '12px',
    height: '12px',
    borderRadius: '50%',
  },
  categoryName: {
    fontWeight: '600',
    color: '#2c3e50',
  },
  description: {
    margin: '0.5rem 0 0 0',
    color: '#666',
    fontSize: '0.875rem',
  },
  dates: {
    display: 'flex',
    gap: '2rem',
    fontSize: '0.875rem',
  },
  dateLabel: {
    color: '#666',
    marginRight: '0.5rem',
  },
  dateValue: {
    fontWeight: '500',
    color: '#2c3e50',
  },
};

export default RecurringTransactionCard;
