import { useQuery } from '@tanstack/react-query';
import { dashboardService } from '../services/dashboardService';
import StatsCard from '../components/dashboard/StatsCard';
import { ArrowUpIcon, ArrowDownIcon, ChartBarIcon, BanknotesIcon } from '@heroicons/react/24/outline';
import ExpenseIncomeChart from '../components/charts/ExpenseIncomeChart';
import CategoryPieChart from '../components/charts/CategoryPieChart';

function DashboardPage() {
  const { data, isLoading, error } = useQuery({
    queryKey: ['dashboard'],
    queryFn: () => dashboardService.getDashboard(),
  });

  if (isLoading) return <div style={styles.loading}>Loading dashboard...</div>;
  if (error) return <div style={styles.error}>Error loading dashboard</div>;

  const dashboard = data?.data;

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount || 0);
  };

  const formatPercent = (value) => {
    const formatted = Math.abs(value || 0).toFixed(1);
    return value >= 0 ? `+${formatted}%` : `-${formatted}%`;
  };

  return (
    <div>
      <h1 style={styles.title}>Dashboard</h1>

      {/* Stats Cards */}
      <div style={styles.statsGrid}>
        <StatsCard
          title="Current Balance"
          value={formatCurrency(dashboard?.currentBalance)}
          color="#3498db"
          icon={<BanknotesIcon style={{ width: 24, height: 24 }} />}
        />

        <StatsCard
          title="Monthly Income"
          value={formatCurrency(dashboard?.monthlyIncome)}
          subtitle={`${formatPercent(dashboard?.incomeChangePercent)} from last month`}
          color="#27ae60"
          icon={<ArrowUpIcon style={{ width: 24, height: 24 }} />}
        />

        <StatsCard
          title="Monthly Expenses"
          value={formatCurrency(dashboard?.monthlyExpense)}
          subtitle={`${formatPercent(dashboard?.expenseChangePercent)} from last month`}
          color="#e74c3c"
          icon={<ArrowDownIcon style={{ width: 24, height: 24 }} />}
        />

        <StatsCard
          title="Monthly Balance"
          value={formatCurrency(dashboard?.monthlyBalance)}
          color={dashboard?.monthlyBalance >= 0 ? '#27ae60' : '#e74c3c'}
          icon={<ChartBarIcon style={{ width: 24, height: 24 }} />}
        />
      </div>

      {/* Recent Transactions */}
      <div style={styles.section}>
        <h2 style={styles.sectionTitle}>Recent Transactions</h2>
        <div style={styles.transactionsList}>
          {dashboard?.recentTransactions?.map((transaction) => (
            <div key={transaction.id} style={styles.transactionItem}>
              <div style={styles.transactionLeft}>
                <div
                  style={{
                    ...styles.categoryDot,
                    backgroundColor: transaction.categoryColor,
                  }}
                />
                <div>
                  <p style={styles.transactionCategory}>{transaction.categoryName}</p>
                  <p style={styles.transactionDescription}>
                    {transaction.description || 'No description'}
                  </p>
                </div>
              </div>
              <div style={styles.transactionRight}>
                <p style={{
                  ...styles.transactionAmount,
                  color: transaction.type === 'INCOME' ? '#27ae60' : '#e74c3c',
                }}>
                  {transaction.type === 'INCOME' ? '+' : '-'}
                  {formatCurrency(transaction.amount)}
                </p>
                <p style={styles.transactionDate}>
                  {new Date(transaction.date).toLocaleDateString()}
                </p>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Top Categories */}
      <div style={styles.section}>
        <h2 style={styles.sectionTitle}>Top Expense Categories</h2>
        <div style={styles.categoriesList}>
          {dashboard?.topExpenseCategories?.map((category) => (
            <div key={category.categoryId} style={styles.categoryItem}>
              <div style={styles.categoryInfo}>
                <div
                  style={{
                    ...styles.categoryDot,
                    backgroundColor: category.categoryColor,
                  }}
                />
                <span>{category.categoryName}</span>
              </div>
              <div style={styles.categoryStats}>
                <span style={styles.categoryAmount}>
                  {formatCurrency(category.totalAmount)}
                </span>
                <span style={styles.categoryPercent}>
                  {category.percentage?.toFixed(1)}%
                </span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Charts Section - ПЕРЕНЕСЕНО ВНУТРЬ RETURN */}
      <div style={styles.chartsSection}>
        <h2 style={styles.sectionTitle}>Analytics</h2>

        {/* Income vs Expenses Chart */}
        <div style={styles.chartCard}>
          <ExpenseIncomeChart dailyStats={dashboard?.dailyStats || []} />
        </div>

        {/* Category Distribution */}
        {dashboard?.topExpenseCategories?.length > 0 && (
          <div style={styles.chartRow}>
            <div style={styles.chartCard}>
              <CategoryPieChart
                categories={dashboard.topExpenseCategories}
                title="Expense Distribution by Category"
              />
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

const styles = {
  loading: {
    textAlign: 'center',
    padding: '2rem',
    fontSize: '1.2rem',
  },
  error: {
    textAlign: 'center',
    padding: '2rem',
    color: '#e74c3c',
  },
  title: {
    fontSize: '2rem',
    marginBottom: '2rem',
  },
  statsGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
    gap: '1rem',
    marginBottom: '2rem',
  },
  section: {
    marginBottom: '2rem',
  },
  sectionTitle: {
    fontSize: '1.5rem',
    marginBottom: '1rem',
  },
  transactionsList: {
    backgroundColor: 'white',
    borderRadius: '8px',
    padding: '1rem',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  transactionItem: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '0.75rem 0',
    borderBottom: '1px solid #f0f0f0',
  },
  transactionLeft: {
    display: 'flex',
    alignItems: 'center',
    gap: '1rem',
  },
  transactionRight: {
    textAlign: 'right',
  },
  categoryDot: {
    width: '12px',
    height: '12px',
    borderRadius: '50%',
  },
  transactionCategory: {
    fontWeight: '500',
    margin: 0,
  },
  transactionDescription: {
    fontSize: '0.875rem',
    color: '#666',
    margin: 0,
  },
  transactionAmount: {
    fontWeight: 'bold',
    margin: 0,
  },
  transactionDate: {
    fontSize: '0.875rem',
    color: '#888',
    margin: 0,
  },
  categoriesList: {
    backgroundColor: 'white',
    borderRadius: '8px',
    padding: '1rem',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  categoryItem: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '0.75rem 0',
    borderBottom: '1px solid #f0f0f0',
  },
  categoryInfo: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.75rem',
  },
  categoryStats: {
    display: 'flex',
    alignItems: 'center',
    gap: '1rem',
  },
  categoryAmount: {
    fontWeight: '500',
  },
  categoryPercent: {
    fontSize: '0.875rem',
    color: '#666',
  },
  chartsSection: {
    marginTop: '3rem',
  },
  chartCard: {
    backgroundColor: 'white',
    padding: '1.5rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    marginBottom: '1.5rem',
  },
  chartRow: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(400px, 1fr))',
    gap: '1.5rem',
  },
};

export default DashboardPage;
