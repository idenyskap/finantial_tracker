import { useQuery } from '@tanstack/react-query';
import { goalService } from '../../services/goalService';
import { Link } from 'react-router-dom';
import { TrophyIcon, ClockIcon } from '@heroicons/react/24/outline';

function GoalsWidget() {
  const { data: goalsData } = useQuery({
    queryKey: ['goals', true],
    queryFn: () => goalService.getAll(true),
  });

  const goals = goalsData?.data || [];
  const topGoals = goals.slice(0, 3);

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount);
  };

  if (goals.length === 0) {
    return (
      <div style={styles.widget}>
        <h3 style={styles.title}>
          <TrophyIcon style={styles.titleIcon} />
          Financial Goals
        </h3>
        <div style={styles.empty}>
          <p>No active goals yet</p>
          <Link to="/goals" style={styles.link}>Create your first goal →</Link>
        </div>
      </div>
    );
  }

  return (
    <div style={styles.widget}>
      <div style={styles.header}>
        <h3 style={styles.title}>
          <TrophyIcon style={styles.titleIcon} />
          Financial Goals
        </h3>
        <Link to="/goals" style={styles.viewAll}>View all</Link>
      </div>

      <div style={styles.goalsList}>
        {topGoals.map(goal => {
          const progressPercentage = Math.min(goal.progressPercentage || 0, 100);

          return (
            <div key={goal.id} style={styles.goalItem}>
              <div style={styles.goalHeader}>
                <h4 style={styles.goalName}>{goal.name}</h4>
                {goal.daysRemaining < 30 && goal.daysRemaining > 0 && (
                  <div style={styles.urgentBadge}>
                    <ClockIcon style={styles.urgentIcon} />
                    {goal.daysRemaining}d
                  </div>
                )}
              </div>

              <div style={styles.goalProgress}>
                <div style={styles.progressInfo}>
                  <span>{formatCurrency(goal.currentAmount)}</span>
                  <span style={styles.targetAmount}>
                    of {formatCurrency(goal.targetAmount)}
                  </span>
                </div>

                <div style={styles.progressBar}>
                  <div
                    style={{
                      ...styles.progressFill,
                      width: `${progressPercentage}%`,
                      backgroundColor: goal.isOverdue ? '#e74c3c' :
                        progressPercentage >= 75 ? '#f39c12' : '#3498db'
                    }}
                  />
                </div>

                <div style={styles.progressStats}>
                  <span>{progressPercentage.toFixed(0)}%</span>
                  {goal.requiredMonthlySaving > 0 && (
                    <span style={styles.monthlySaving}>
                      {formatCurrency(goal.requiredMonthlySaving)}/mo
                    </span>
                  )}
                </div>
              </div>
            </div>
          );
        })}
      </div>

      <div style={styles.quickStats}>
        <div style={styles.stat}>
          <span style={styles.statValue}>{goals.length}</span>
          <span style={styles.statLabel}>Active Goals</span>
        </div>
        <div style={styles.stat}>
          <span style={styles.statValue}>
            {formatCurrency(goals.reduce((sum, g) => sum + g.currentAmount, 0))}
          </span>
          <span style={styles.statLabel}>Total Saved</span>
        </div>
      </div>
    </div>
  );
}

const styles = {
  widget: {
    backgroundColor: 'white',
    borderRadius: '8px',
    padding: '1.5rem',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '1rem',
  },
  title: {
    margin: 0,
    fontSize: '1.25rem',
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
  },
  titleIcon: {
    width: '24px',
    height: '24px',
    color: '#f39c12',
  },
  viewAll: {
    color: '#3498db',
    textDecoration: 'none',
    fontSize: '0.875rem',
  },
  empty: {
    textAlign: 'center',
    padding: '2rem',
    color: '#666',
  },
  link: {
    color: '#3498db',
    textDecoration: 'none',
  },
  goalsList: {
    display: 'flex',
    flexDirection: 'column',
    gap: '1rem',
  },
  goalItem: {
    padding: '1rem',
    backgroundColor: '#f8f9fa',
    borderRadius: '6px',
  },
  goalHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '0.75rem',
  },
  goalName: {
    margin: 0,
    fontSize: '1rem',
  },
  urgentBadge: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.25rem',
    padding: '0.25rem 0.5rem',
    backgroundColor: '#fee',
    color: '#e74c3c',
    borderRadius: '4px',
    fontSize: '0.75rem',
  },
  urgentIcon: {
    width: '14px',
    height: '14px',
  },
  goalProgress: {
    display: 'flex',
    flexDirection: 'column',
    gap: '0.5rem',
  },
  progressInfo: {
    display: 'flex',
    justifyContent: 'space-between',
    fontSize: '0.875rem',
  },
  targetAmount: {
    color: '#666',
  },
  progressBar: {
    height: '8px',
    backgroundColor: '#e0e0e0',
    borderRadius: '4px',
    overflow: 'hidden',
  },
  progressFill: {
    height: '100%',
    transition: 'width 0.3s ease',
  },
  progressStats: {
    display: 'flex',
    justifyContent: 'space-between',
    fontSize: '0.75rem',
    color: '#666',
  },
  monthlySaving: {
    color: '#3498db',
    fontWeight: '500',
  },
  quickStats: {
    display: 'grid',
    gridTemplateColumns: '1fr 1fr',
    gap: '1rem',
    marginTop: '1rem',
    paddingTop: '1rem',
    borderTop: '1px solid #e0e0e0',
  },
  stat: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
  },
  statValue: {
    fontSize: '1.25rem',
    fontWeight: 'bold',
    color: '#2c3e50',
  },
  statLabel: {
    fontSize: '0.75rem',
    color: '#666',
  },
};

export default GoalsWidget;
