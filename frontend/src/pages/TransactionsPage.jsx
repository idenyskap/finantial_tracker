import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { transactionService } from '../services/transactionService';
import { categoryService } from '../services/categoryService';
import { savedSearchService } from '../services/savedSearchService';
import TransactionSearch from '../components/transactions/TransactionSearch';
import SavedSearchItem from '../components/savedSearches/SavedSearchItem';
import { toast } from 'sonner';
import { ArrowDownTrayIcon, PlusIcon } from '@heroicons/react/24/outline';
import ImportCSV from '../components/transactions/ImportCSV';

function TransactionsPage() {
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
      toast.error('Export failed');
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount);
  };

  return (
    <div>
      <div style={styles.header}>
        <h1>Transactions</h1>
        <div style={styles.headerButtons}>
          <ImportCSV onImportComplete={() => queryClient.invalidateQueries(['transactions'])} />
          <button onClick={handleExportCSV} style={styles.exportButton}>
            <ArrowDownTrayIcon style={styles.icon} />
            Export
          </button>
          <button onClick={() => setShowForm(!showForm)} style={styles.addButton}>
            <PlusIcon style={styles.icon} />
            Add Transaction
          </button>
        </div>
      </div>

      <TransactionSearch onSearch={handleSearch} categories={categories} />

      {savedSearches.length > 0 && (
        <div style={{ marginBottom: '2rem' }}>
          <h3 style={{ marginBottom: '1rem' }}>Saved Searches</h3>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '1rem' }}>
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

      {showForm && (
        <div style={styles.formContainer}>
          <h3>New Transaction</h3>
          <form onSubmit={handleSubmit} style={styles.form}>
            <input
              name="amount"
              type="number"
              step="0.01"
              placeholder="Amount"
              value={form.amount}
              onChange={handleChange}
              required
              style={styles.input}
            />
            <select
              name="type"
              value={form.type}
              onChange={handleChange}
              style={styles.select}
            >
              <option value="EXPENSE">Expense</option>
              <option value="INCOME">Income</option>
            </select>
            <select
              name="categoryId"
              value={form.categoryId}
              onChange={handleChange}
              required
              style={styles.select}
            >
              <option value="">Select {form.type === 'INCOME' ? 'Income' : 'Expense'} Category</option>
              {filteredCategories.map(cat => (
                <option key={cat.id} value={cat.id}>{cat.name}</option>
              ))}
            </select>
            <input
              name="date"
              type="date"
              value={form.date}
              onChange={handleChange}
              required
              style={styles.input}
            />
            <input
              name="description"
              type="text"
              placeholder="Description (optional)"
              value={form.description}
              onChange={handleChange}
              style={styles.input}
            />
            <button type="submit" disabled={createMutation.isPending} style={styles.submitButton}>
              {createMutation.isPending ? 'Creating...' : 'Create'}
            </button>
          </form>
        </div>
      )}

      {isLoading ? (
        <div style={styles.loading}>Loading transactions...</div>
      ) : (
        <div style={styles.transactionsList}>
          {transactions.length === 0 ? (
            <p style={styles.empty}>No transactions found</p>
          ) : (
            transactions.map(tx => (
              <div key={tx.id} style={styles.transactionItem}>
                <div style={styles.transactionLeft}>
                  <div
                    style={{
                      ...styles.categoryDot,
                      backgroundColor: tx.categoryColor,
                    }}
                  />
                  <div>
                    <p style={{ ...styles.transactionCategory, color: '#000000' }}>{tx.categoryName}</p>
                    <p style={{ ...styles.transactionDescription, color: '#000000' }}>{tx.description || 'No description'}</p>
                    <p style={{ ...styles.transactionDate, color: '#000000' }}>{new Date(tx.date).toLocaleDateString()}</p>
                  </div>
                </div>
                <div style={styles.transactionAmount}>
                  <span style={{ color: tx.type === 'INCOME' ? '#27ae60' : '#e74c3c' }}>
                    {tx.type === 'INCOME' ? '+' : '-'}
                    {formatCurrency(tx.amount)}
                  </span>
                </div>
              </div>
            ))
          )}
        </div>
      )}
    </div>
  );
}

const styles = {
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '2rem',
  },
  headerButtons: {
    display: 'flex',
    gap: '0.5rem',
  },
  exportButton: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
    padding: '0.75rem 1rem',
    backgroundColor: '#95a5a6',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
  },
  addButton: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
    padding: '0.75rem 1rem',
    backgroundColor: '#27ae60',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
  },
  icon: {
    width: '20px',
    height: '20px',
  },
  formContainer: {
    backgroundColor: 'white',
    padding: '1.5rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    marginBottom: '2rem',
  },
  form: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
    gap: '1rem',
  },
  input: {
    padding: '0.75rem',
    border: '1px solid #ddd',
    borderRadius: '4px',
  },
  select: {
    padding: '0.75rem',
    border: '1px solid #ddd',
    borderRadius: '4px',
  },
  submitButton: {
    padding: '0.75rem',
    backgroundColor: '#3498db',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
  },
  loading: {
    textAlign: 'center',
    padding: '2rem',
  },
  transactionsList: {
    backgroundColor: 'white',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  empty: {
    textAlign: 'center',
    padding: '2rem',
    color: '#666',
  },
  transactionItem: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '1rem',
    borderBottom: '1px solid #f0f0f0',
  },
  transactionLeft: {
    display: 'flex',
    gap: '1rem',
  },
  categoryDot: {
    width: '12px',
    height: '12px',
    borderRadius: '50%',
    marginTop: '4px',
  },
  transactionCategory: {
    fontWeight: '500',
    margin: '0 0 0.25rem 0',
  },
  transactionDescription: {
    fontSize: '0.875rem',
    color: '#666',
    margin: '0 0 0.25rem 0',
  },
  transactionDate: {
    fontSize: '0.875rem',
    color: '#999',
    margin: 0,
  },
  transactionAmount: {
    fontSize: '1.125rem',
    fontWeight: 'bold',
  },
};

export default TransactionsPage;
