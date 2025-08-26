import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { transactionService } from '../services/transactionService';
import { categoryService } from '../services/categoryService';
import { savedSearchService } from '../services/savedSearchService';
import TransactionSearch from '../components/transactions/TransactionSearch';
import SavedSearchItem from '../components/savedSearches/SavedSearchItem';
import { toast } from 'sonner';
import ImportCSV from '../components/transactions/ImportCSV';
import { useThemedStyles } from '../hooks/useThemedStyles';
import { useCurrency } from '../contexts/CurrencyContext';
import { useLanguage } from '../hooks/useLanguage';

function TransactionsPage() {
  const styles = useThemedStyles(getStyles);
  const { formatCurrency } = useCurrency();
  const { t } = useLanguage();
  const queryClient = useQueryClient();
  const [searchParams, setSearchParams] = useState({});
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({
    amount: '',
    type: 'EXPENSE',
    categoryId: '',
    date: new Date().toISOString().split('T')[0],
    description: '',
  });

  const { data: categoriesData } = useQuery({
    queryKey: ['categories'],
    queryFn: () => categoryService.getAll(),
  });

  const { data: transactionsData, isLoading } = useQuery({
    queryKey: ['transactions', searchParams],
    queryFn: () => transactionService.search(searchParams),
  });

  const { data: savedSearchesData } = useQuery({
    queryKey: ['saved-searches'],
    queryFn: () => savedSearchService.getAll(),
  });

  const createMutation = useMutation({
    mutationFn: transactionService.create,
    onSuccess: (response) => {
      queryClient.invalidateQueries(['transactions']);
      queryClient.invalidateQueries(['dashboard']);

      if (response.data.budgetWarning) {
        const warning = response.data.budgetWarning;
        if (warning.level === 'EXCEEDED') {
          toast.error(warning.message);
        } else if (warning.level === 'ALERT') {
          toast.warning(warning.message);
        } else {
          toast.info(warning.message);
        }
      } else {
        toast.success('Transaction created successfully');
      }

      setShowForm(false);
      resetForm();
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error creating transaction');
    },
  });

  const deleteSavedSearchMutation = useMutation({
    mutationFn: savedSearchService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries(['saved-searches']);
      toast.success('Saved search deleted');
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error deleting saved search');
    },
  });

  const categories = categoriesData?.data || [];
  const transactions = transactionsData?.data?.content || [];
  const savedSearches = savedSearchesData?.data || [];

  const filteredCategories = categories.filter(cat => {
    if (!cat.type) return true;
    return cat.type === form.type;
  });

  const handleSearch = (params) => {
    setSearchParams(params);
  };

  const handleExecuteSavedSearch = (search) => {
    setSearchParams(search.searchCriteria);
  };

  const handleDeleteSavedSearch = (id) => {
    if (window.confirm('Delete this saved search?')) {
      deleteSavedSearchMutation.mutate(id);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    createMutation.mutate(form);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === 'type') {
      setForm(prev => ({
        ...prev,
        [name]: value,
        categoryId: '',
      }));
    } else {
      setForm(prev => ({
        ...prev,
        [name]: value,
      }));
    }
  };

  const resetForm = () => {
    setForm({
      amount: '',
      type: 'EXPENSE',
      categoryId: '',
      date: new Date().toISOString().split('T')[0],
      description: '',
    });
  };

  const handleExportCSV = async () => {
    try {
      const response = await transactionService.exportCSV(searchParams);
      const blob = new Blob([response.data], { type: 'text/csv' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `transactions_${new Date().toISOString().split('T')[0]}.csv`;
      a.click();
      window.URL.revokeObjectURL(url);
      toast.success('Exported to CSV');
    } catch (error) {
      console.error('Export failed:', error);
      toast.error('Export failed');
    }
  };


  return (
    <div style={styles.container}>
      {/* Header */}
      <div style={styles.header}>
        <div style={styles.headerContent}>
          <h1 style={styles.title}>{t('transactions.title')}</h1>
          <p style={styles.subtitle}>{t('transactions.subtitle')}</p>
        </div>
        <div style={styles.headerButtons}>
          <ImportCSV onImportComplete={() => queryClient.invalidateQueries(['transactions'])} />
          <button onClick={handleExportCSV} style={styles.exportButton}>
            <span style={styles.buttonIcon}>📥</span>
            {t('transactions.exportCSV')}
          </button>
          <button onClick={() => setShowForm(!showForm)} style={styles.addButton}>
            <span style={styles.addButtonIcon}>+</span>
            {t('transactions.addTransaction')}
          </button>
        </div>
      </div>

      {/* Search Section */}
      <div style={styles.searchSection}>
        <TransactionSearch onSearch={handleSearch} categories={categories} />
      </div>

      {/* Saved Searches */}
      {savedSearches.length > 0 && (
        <div style={styles.savedSearchesSection}>
          <div style={styles.sectionHeader}>
            <h3 style={styles.sectionTitle}>
              <span style={styles.sectionIcon}>🔖</span>
              {t('transactions.savedSearches')}
            </h3>
          </div>
          <div style={styles.savedSearchesGrid}>
            {savedSearches.map(search => (
              <SavedSearchItem
                key={search.id}
                search={search}
                onExecute={handleExecuteSavedSearch}
                onDelete={handleDeleteSavedSearch}
              />
            ))}
          </div>
        </div>
      )}

      {/* Form */}
      {showForm && (
        <div style={styles.formCard}>
          <div style={styles.formHeader}>
            <h3 style={styles.formTitle}>{t('transactions.addNewTransaction')}</h3>
            <button onClick={() => setShowForm(false)} style={styles.closeButton}>×</button>
          </div>
          
          <form onSubmit={handleSubmit} style={styles.form}>
            <div style={styles.formRow}>
              <div style={styles.formGroup}>
                <label style={styles.label}>{t('transactions.amount')}</label>
                <input
                  name="amount"
                  type="number"
                  step="0.01"
                  placeholder={t('transactions.enterAmount')}
                  value={form.amount}
                  onChange={handleChange}
                  required
                  style={styles.input}
                />
              </div>
              
              <div style={styles.formGroup}>
                <label style={styles.label}>{t('transactions.transactionType')}</label>
                <select
                  name="type"
                  value={form.type}
                  onChange={handleChange}
                  style={styles.select}
                >
                  <option value="EXPENSE">💸 {t('transactions.expense')}</option>
                  <option value="INCOME">💰 {t('transactions.income')}</option>
                </select>
              </div>
            </div>
            
            <div style={styles.formRow}>
              <div style={styles.formGroup}>
                <label style={styles.label}>Category</label>
                <select
                  name="categoryId"
                  value={form.categoryId}
                  onChange={handleChange}
                  required
                  style={styles.select}
                >
                  <option value="">Select {form.type === 'INCOME' ? t('transactions.income') : t('transactions.expense')} {t('transactions.category')}</option>
                  {filteredCategories.map(cat => (
                    <option key={cat.id} value={cat.id}>{cat.name}</option>
                  ))}
                </select>
              </div>
              
              <div style={styles.formGroup}>
                <label style={styles.label}>{t('transactions.date')}</label>
                <input
                  name="date"
                  type="date"
                  value={form.date}
                  onChange={handleChange}
                  required
                  style={styles.input}
                />
              </div>
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>{t('transactions.description')} {t('transactions.optional')}</label>
              <input
                name="description"
                type="text"
                placeholder="Add a description for this transaction"
                value={form.description}
                onChange={handleChange}
                style={styles.input}
              />
            </div>
            
            <div style={styles.formActions}>
              <button type="button" onClick={() => setShowForm(false)} style={styles.cancelButton}>
                {t('common.cancel')}
              </button>
              <button type="submit" disabled={createMutation.isPending} style={styles.submitButton}>
                {createMutation.isPending ? t('common.loading') : t('transactions.addTransaction')}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Transactions List */}
      {isLoading ? (
        <div style={styles.loading}>
          <div style={styles.loadingSpinner}></div>
          <p>{t('common.loading')}</p>
        </div>
      ) : (
        <div style={styles.transactionsSection}>
          <div style={styles.sectionHeader}>
            <h3 style={styles.sectionTitle}>
              <span style={styles.sectionIcon}>📊</span>
              {t('dashboard.recentTransactions')}
              <span style={styles.badge}>{transactions.length}</span>
            </h3>
          </div>
          
          {transactions.length === 0 ? (
            <div style={styles.emptyState}>
              <div style={styles.emptyIcon}>💳</div>
              <p style={styles.emptyText}>{t('transactions.noTransactionsFound')}</p>
              <p style={styles.emptySubtext}>{t('transactions.noTransactionsSubtext')}</p>
            </div>
          ) : (
            <div style={styles.transactionsList}>
              {transactions.map(tx => (
                <div key={tx.id} style={styles.transactionItem}>
                  <div style={styles.transactionLeft}>
                    <div
                      style={{
                        ...styles.categoryIndicator,
                        backgroundColor: tx.categoryColor,
                      }}
                    />
                    <div style={styles.transactionDetails}>
                      <p style={styles.transactionCategory}>{tx.categoryName}</p>
                      <p style={styles.transactionDescription}>{tx.description || t('transactions.noDescription')}</p>
                      <p style={styles.transactionDate}>{new Date(tx.date).toLocaleDateString()}</p>
                    </div>
                  </div>
                  <div style={styles.transactionRight}>
                    <div style={styles.transactionAmount}>
                      <span style={{
                        ...styles.amountText,
                        color: tx.type === 'INCOME' ? '#10b981' : '#ef4444'
                      }}>
                        {tx.type === 'INCOME' ? '+' : '-'}
                        {formatCurrency(tx.amount)}
                      </span>
                      <span style={styles.transactionType}>
                        {tx.type === 'INCOME' ? '💰' : '💸'}
                      </span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}

const getStyles = (theme) => ({
  container: {
    padding: '1.5rem',
    maxWidth: '1200px',
    margin: '0 auto',
    backgroundColor: theme.background,
    minHeight: '100vh',
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: '2rem',
    padding: '1.5rem',
    backgroundColor: theme.cardBackground,
    borderRadius: '12px',
    boxShadow: theme.shadow,
    border: `1px solid ${theme.cardBorder}`,
  },
  headerContent: {
    flex: 1,
  },
  title: {
    fontSize: '2rem',
    fontWeight: '700',
    color: theme.text,
    marginBottom: '0.5rem',
    margin: 0,
  },
  subtitle: {
    color: theme.textSecondary,
    fontSize: '1rem',
    margin: 0,
  },
  headerButtons: {
    display: 'flex',
    gap: '0.75rem',
  },
  exportButton: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
    padding: '0.75rem 1rem',
    backgroundColor: '#64748b',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '0.875rem',
    fontWeight: '500',
    transition: 'all 0.2s ease',
  },
  addButton: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
    padding: '0.75rem 1.5rem',
    backgroundColor: '#059669',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '0.95rem',
    fontWeight: '600',
    transition: 'all 0.2s ease',
    boxShadow: '0 2px 4px rgba(5, 150, 105, 0.2)',
  },
  buttonIcon: {
    fontSize: '1rem',
  },
  addButtonIcon: {
    fontSize: '1.2rem',
    fontWeight: 'bold',
  },
  searchSection: {
    marginBottom: '2rem',
  },
  savedSearchesSection: {
    backgroundColor: theme.cardBackground,
    borderRadius: '12px',
    boxShadow: theme.shadow,
    marginBottom: '2rem',
    overflow: 'hidden',
    border: `1px solid ${theme.cardBorder}`,
  },
  sectionHeader: {
    padding: '1.5rem 1.5rem 1rem',
    borderBottom: `1px solid ${theme.border}`,
    backgroundColor: theme.backgroundSecondary,
  },
  sectionTitle: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.75rem',
    fontSize: '1.25rem',
    fontWeight: '600',
    color: theme.text,
    margin: 0,
  },
  sectionIcon: {
    fontSize: '1.5rem',
  },
  badge: {
    backgroundColor: '#e2e8f0',
    color: '#475569',
    padding: '0.25rem 0.5rem',
    borderRadius: '12px',
    fontSize: '0.75rem',
    fontWeight: '600',
    marginLeft: '0.5rem',
  },
  savedSearchesGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
    gap: '1rem',
    padding: '1.5rem',
  },
  formCard: {
    backgroundColor: theme.cardBackground,
    borderRadius: '12px',
    boxShadow: theme.shadowLarge,
    marginBottom: '2rem',
    overflow: 'hidden',
    border: `1px solid ${theme.cardBorder}`,
  },
  formHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '1.5rem',
    borderBottom: `1px solid ${theme.border}`,
    backgroundColor: theme.backgroundSecondary,
  },
  formTitle: {
    fontSize: '1.25rem',
    fontWeight: '600',
    color: theme.text,
    margin: 0,
  },
  closeButton: {
    background: 'none',
    border: 'none',
    fontSize: '1.5rem',
    color: '#64748b',
    cursor: 'pointer',
    padding: '0.25rem',
    borderRadius: '4px',
    transition: 'color 0.2s ease',
  },
  form: {
    padding: '1.5rem',
    display: 'flex',
    flexDirection: 'column',
    gap: '1.5rem',
  },
  formRow: {
    display: 'grid',
    gridTemplateColumns: '1fr 1fr',
    gap: '1.5rem',
  },
  formGroup: {
    display: 'flex',
    flexDirection: 'column',
    gap: '0.5rem',
  },
  label: {
    fontSize: '0.875rem',
    fontWeight: '600',
    color: theme.text,
  },
  input: {
    padding: '0.75rem',
    border: `1px solid ${theme.inputBorder}`,
    borderRadius: '8px',
    fontSize: '1rem',
    transition: 'border-color 0.2s ease',
    backgroundColor: theme.inputBackground,
    color: theme.inputText,
  },
  select: {
    padding: '0.75rem',
    border: `1px solid ${theme.inputBorder}`,
    borderRadius: '8px',
    fontSize: '1rem',
    backgroundColor: theme.inputBackground,
    color: theme.inputText,
    cursor: 'pointer',
  },
  formActions: {
    display: 'flex',
    gap: '0.75rem',
    paddingTop: '1rem',
    borderTop: '1px solid #e2e8f0',
  },
  cancelButton: {
    flex: 1,
    padding: '0.75rem 1.5rem',
    backgroundColor: '#f1f5f9',
    color: '#475569',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontWeight: '500',
    transition: 'all 0.2s ease',
  },
  submitButton: {
    flex: 1,
    padding: '0.75rem 1.5rem',
    backgroundColor: '#059669',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontWeight: '600',
    transition: 'all 0.2s ease',
  },
  loading: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '3rem',
    backgroundColor: 'white',
    borderRadius: '12px',
    boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)',
  },
  loadingSpinner: {
    width: '40px',
    height: '40px',
    border: '4px solid #e2e8f0',
    borderTop: '4px solid #059669',
    borderRadius: '50%',
    animation: 'spin 1s linear infinite',
    marginBottom: '1rem',
  },
  transactionsSection: {
    backgroundColor: theme.cardBackground,
    borderRadius: '12px',
    boxShadow: theme.shadow,
    overflow: 'hidden',
    border: `1px solid ${theme.cardBorder}`,
  },
  emptyState: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '3rem',
    textAlign: 'center',
  },
  emptyIcon: {
    fontSize: '3rem',
    marginBottom: '1rem',
    opacity: 0.5,
  },
  emptyText: {
    fontSize: '1.125rem',
    fontWeight: '600',
    color: theme.text,
    marginBottom: '0.5rem',
  },
  emptySubtext: {
    color: theme.textSecondary,
    fontSize: '0.875rem',
  },
  transactionsList: {
    padding: '0',
  },
  transactionItem: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '1.5rem',
    borderBottom: `1px solid ${theme.borderLight}`,
    transition: 'background-color 0.2s ease',
  },
  transactionLeft: {
    display: 'flex',
    alignItems: 'center',
    gap: '1rem',
    flex: 1,
  },
  categoryIndicator: {
    width: '12px',
    height: '12px',
    borderRadius: '3px',
    flexShrink: 0,
  },
  transactionDetails: {
    flex: 1,
  },
  transactionCategory: {
    fontWeight: '700',
    color: theme.text,
    margin: '0 0 0.25rem 0',
    fontSize: '1rem',
  },
  transactionDescription: {
    fontSize: '0.875rem',
    color: theme.textSecondary,
    margin: '0 0 0.25rem 0',
    fontWeight: '500',
  },
  transactionDate: {
    fontSize: '0.75rem',
    color: theme.textTertiary,
    margin: 0,
    textTransform: 'uppercase',
    letterSpacing: '0.05em',
    fontWeight: '500',
  },
  transactionRight: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.75rem',
  },
  transactionAmount: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
  },
  amountText: {
    fontSize: '1.125rem',
    fontWeight: '700',
  },
  transactionType: {
    fontSize: '1.25rem',
  },
});

export default TransactionsPage;
