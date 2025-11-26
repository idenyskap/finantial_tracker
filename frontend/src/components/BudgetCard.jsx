import React from 'react';
import { ExclamationTriangleIcon } from '@heroicons/react/24/outline';
import { useCurrency } from '../hooks/useCurrency';
import { useLanguage } from '../hooks/useLanguage';

function BudgetCard({ budget, onEdit, onDelete }) {
  const { formatCurrency } = useCurrency();
  const { t } = useLanguage();
  const percentage = budget.spent && budget.amount
    ? (budget.spent / budget.amount) * 100
    : 0;

  const getProgressColor = () => {
    if (percentage >= 100) return '#e74c3c';
    if (percentage >= budget.notifyThreshold) return '#f39c12';
    return '#27ae60';
  };


  const isOverBudget = percentage > 100;
  const isNearLimit = percentage >= budget.notifyThreshold;

  return (
    <div style={{
      ...styles.card,
      borderColor: isOverBudget ? '#e74c3c' : styles.card.borderColor,
    }}>
      <div style={styles.header}>
        <div style={styles.titleSection}>
          <h3 style={styles.name}>{budget.name}</h3>
          {budget.categoryName && (
            <span style={styles.category}>{budget.categoryName}</span>
          )}
        </div>

        <div style={styles.actions}>
          <button onClick={() => onEdit(budget)} style={styles.actionButton}>
            {t('common.edit')}
          </button>
          <button onClick={() => onDelete(budget.id)} style={{...styles.actionButton, ...styles.deleteButton}}>
            {t('common.delete')}
          </button>
        </div>
      </div>

      {(isOverBudget || isNearLimit) && (
        <div style={{
          ...styles.alert,
          backgroundColor: isOverBudget ? '#fee' : '#fef3cd',
          color: isOverBudget ? '#e74c3c' : '#856404',
        }}>
          <ExclamationTriangleIcon style={styles.alertIcon} />
          {isOverBudget
            ? t('budgets.budgetExceeded')
            : `${percentage.toFixed(0)}% ${t('budgets.budgetUsed')}`}
        </div>
      )}

      <div style={styles.amounts}>
        <div>
          <span style={styles.spent}>{formatCurrency(budget.spent)}</span>
          <span style={styles.of}> {t('budgets.of')} </span>
          <span style={styles.limit}>{formatCurrency(budget.amount)}</span>
        </div>
        <span style={styles.remaining}>
          {formatCurrency(budget.amount - budget.spent)} {t('budgets.remaining')}
        </span>
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
        </div>
        <span style={styles.percentage}>{percentage.toFixed(0)}%</span>
      </div>

      <div style={styles.period}>
        <span style={styles.periodLabel}>{t('budgets.period')}:</span>
        <span style={styles.periodValue}>{t(`budgets.${budget.period.toLowerCase()}`)}</span>
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
    border: '2px solid transparent',
    position: 'relative',
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
    margin: '0 0 0.25rem 0',
    fontSize: '1.25rem',
    fontWeight: '600',
    color: '#2c3e50',
  },
  category: {
    fontSize: '0.875rem',
    color: '#666',
  },
  actions: {
    display: 'flex',
    gap: '0.5rem',
  },
  actionButton: {
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
    backgroundColor: '#e74c3c',
  },
  alert: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
    padding: '0.75rem',
    borderRadius: '4px',
    marginBottom: '1rem',
    fontSize: '0.875rem',
  },
  alertIcon: {
    width: '20px',
    height: '20px',
  },
  amounts: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'baseline',
    marginBottom: '0.75rem',
  },
  spent: {
    fontSize: '1.5rem',
    fontWeight: 'bold',
    color: '#2c3e50',
  },
  of: {
    color: '#666',
    fontSize: '0.875rem',
  },
  limit: {
    fontSize: '1rem',
    color: '#666',
  },
  remaining: {
    fontSize: '0.875rem',
    color: '#27ae60',
  },
  progressContainer: {
    marginBottom: '1rem',
  },
  progressBar: {
    height: '8px',
    backgroundColor: '#e9ecef',
    borderRadius: '4px',
    overflow: 'hidden',
    marginBottom: '0.25rem',
  },
  progressFill: {
    height: '100%',
    transition: 'width 0.3s ease',
  },
  percentage: {
    fontSize: '0.75rem',
    color: '#666',
  },
  period: {
    display: 'flex',
    justifyContent: 'space-between',
    fontSize: '0.875rem',
  },
  periodLabel: {
    color: '#666',
  },
  periodValue: {
    fontWeight: '500',
    color: '#2c3e50',
  },
};

export default BudgetCard;
