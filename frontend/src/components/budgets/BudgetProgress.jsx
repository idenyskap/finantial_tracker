function BudgetProgress({ budget }) {
  const percentage = Math.min(budget.percentUsed || 0, 100);
  const isOverBudget = budget.overBudget;
  const isWarning = percentage >= budget.notifyThreshold;

  const getProgressColor = () => {
    if (isOverBudget) return '#e74c3c';
    if (isWarning) return '#f39c12';
    if (percentage > 50) return '#f1c40f';
    return '#27ae60';
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount || 0);
  };

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <div>
          <h3 style={styles.name}>{budget.name}</h3>
          <p style={styles.category}>
            {budget.categoryName || 'All Categories'} • {budget.period}
          </p>
        </div>
        <div style={styles.amount}>
          <span style={{ color: getProgressColor(), fontWeight: 'bold' }}>
            {formatCurrency(budget.spent)}
          </span>
          <span style={styles.separator}> / </span>
          <span style={styles.limit}>{formatCurrency(budget.amount)}</span> {/* Изменено */}
        </div>
      </div>

      <div style={styles.progressContainer}>
        <div style={styles.progressBar}>
          <div
            style={{
              ...styles.progressFill,
              width: `${Math.min(percentage, 100)}%`,
              backgroundColor: getProgressColor(),
            }}
          />
          {isOverBudget && (
            <div
              style={{
                ...styles.overBudgetFill,
                width: `${Math.min(percentage - 100, 100)}%`,
              }}
            />
          )}
        </div>
        <span style={styles.percentageText}>{percentage.toFixed(0)}%</span> {/* Изменено */}
      </div>

      <div style={styles.footer}>
        <span style={styles.remaining}>
          {budget.remaining >= 0 ? 'Remaining: ' : 'Over budget: '}
          <strong style={{ color: budget.remaining >= 0 ? '#27ae60' : '#e74c3c' }}>
            {formatCurrency(Math.abs(budget.remaining))}
          </strong>
        </span>
        {isWarning && !isOverBudget && (
          <span style={styles.warning}>⚠️ Approaching limit</span>
        )}
        {isOverBudget && (
          <span style={styles.exceeded}>❌ Budget exceeded!</span>
        )}
      </div>
    </div>
  );
}

const styles = {
  container: {
    backgroundColor: 'white',
    padding: '1.5rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: '1rem',
  },
  name: {
    margin: '0 0 0.25rem 0',
    fontSize: '1.125rem',
    color: '#333',
  },
  category: {
    margin: 0,
    fontSize: '0.875rem',
    color: '#666',
  },
  amount: {
    textAlign: 'right',
    fontSize: '1.125rem',
  },
  separator: {
    color: '#999',
  },
  limit: {
    color: '#333',
    fontWeight: '500',
  },
  progressContainer: {
    display: 'flex',
    alignItems: 'center',
    gap: '1rem',
    marginBottom: '1rem',
  },
  progressBar: {
    flex: 1,
    height: '12px',
    backgroundColor: '#ecf0f1',
    borderRadius: '6px',
    overflow: 'hidden',
    position: 'relative',
  },
  progressFill: {
    height: '100%',
    transition: 'width 0.3s ease',
  },
  overBudgetFill: {
    position: 'absolute',
    left: '100%',
    height: '100%',
    backgroundColor: '#c0392b',
  },
  percentageText: {
    fontSize: '0.875rem',
    fontWeight: '500',
    minWidth: '40px',
    color: '#333',
  },
  footer: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    fontSize: '0.875rem',
  },
  remaining: {
    color: '#666',
  },
  warning: {
    color: '#f39c12',
    fontWeight: '500',
  },
  exceeded: {
    color: '#e74c3c',
    fontWeight: '500',
  },
};

export default BudgetProgress;
