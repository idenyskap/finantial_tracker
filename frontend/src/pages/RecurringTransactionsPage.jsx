import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { recurringTransactionService } from '../services/recurringTransactionService';
import { categoryService } from '../services/categoryService';
import RecurringTransactionCard from '../components/recurring/RecurringTransactionCard';
import { toast } from 'sonner';
import { PlusIcon } from '@heroicons/react/24/outline';

function RecurringTransactionsPage() {
  const queryClient = useQueryClient();
  const [showForm, setShowForm] = useState(false);
  const [editingTransaction, setEditingTransaction] = useState(null);
  const [form, setForm] = useState({
    name: '',
    amount: '',
    type: 'EXPENSE',
    categoryId: '',
    description: '',
    frequency: 'MONTHLY',
    startDate: new Date().toISOString().split('T')[0],
    endDate: '',
    dayOfMonth: '',
    dayOfWeek: '',
    active: true,
  });

  const { data: transactionsData, isLoading } = useQuery({
    queryKey: ['recurring-transactions'],
    queryFn: () => recurringTransactionService.getAll(),
  });

  const { data: categoriesData } = useQuery({
    queryKey: ['categories'],
    queryFn: () => categoryService.getAll(),
  });

  const createMutation = useMutation({
    mutationFn: recurringTransactionService.create,
    onSuccess: () => {
      queryClient.invalidateQueries(['recurring-transactions']);
      toast.success('Recurring transaction created successfully');
      resetForm();
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error creating recurring transaction');
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }) => recurringTransactionService.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries(['recurring-transactions']);
      toast.success('Recurring transaction updated successfully');
      resetForm();
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error updating recurring transaction');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: recurringTransactionService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries(['recurring-transactions']);
      toast.success('Recurring transaction deleted successfully');
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error deleting recurring transaction');
    },
  });

  const executeMutation = useMutation({
    mutationFn: recurringTransactionService.executeNow,
    onSuccess: () => {
      queryClient.invalidateQueries(['transactions']);
      queryClient.invalidateQueries(['dashboard']);
      toast.success('Transaction executed successfully');
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error executing transaction');
    },
  });

  const transactions = transactionsData?.data || [];
  const categories = categoriesData?.data || [];

  const filteredCategories = categories.filter(cat => {
    if (!cat.type) return true;
    return cat.type === form.type;
  });

  const handleSubmit = (e) => {
    e.preventDefault();

    const data = {
      ...form,
      amount: parseFloat(form.amount),
      dayOfMonth: form.dayOfMonth ? parseInt(form.dayOfMonth) : null,
      dayOfWeek: form.dayOfWeek ? parseInt(form.dayOfWeek) : null,
    };

    if (editingTransaction) {
      updateMutation.mutate({ id: editingTransaction.id, data });
    } else {
      createMutation.mutate(data);
    }
  };

  const handleEdit = (transaction) => {
    setEditingTransaction(transaction);
    setForm({
      name: transaction.name,
      amount: transaction.amount,
      type: transaction.type,
      categoryId: transaction.categoryId,
      description: transaction.description || '',
      frequency: transaction.frequency,
      startDate: transaction.startDate,
      endDate: transaction.endDate || '',
      dayOfMonth: transaction.dayOfMonth || '',
      dayOfWeek: transaction.dayOfWeek || '',
      active: transaction.active,
    });
    setShowForm(true);
  };

  const handleDelete = (id) => {
    if (window.confirm('Are you sure you want to delete this recurring transaction?')) {
      deleteMutation.mutate(id);
    }
  };

  const handleExecute = (id) => {
    if (window.confirm('Execute this transaction now?')) {
      executeMutation.mutate(id);
    }
  };

  const resetForm = () => {
    setForm({
      name: '',
      amount: '',
      type: 'EXPENSE',
      categoryId: '',
      description: '',
      frequency: 'MONTHLY',
      startDate: new Date().toISOString().split('T')[0],
      endDate: '',
      dayOfMonth: '',
      dayOfWeek: '',
      active: true,
    });
    setEditingTransaction(null);
    setShowForm(false);
  };

  const setFormPreset = (type) => {
    switch(type) {
      case 'rent':
        setForm({
          ...form,
          name: 'Monthly Rent',
          type: 'EXPENSE',
          frequency: 'MONTHLY',
          dayOfMonth: '1',
        });
        break;
      case 'salary':
        setForm({
          ...form,
          name: 'Monthly Salary',
          type: 'INCOME',
          frequency: 'MONTHLY',
          dayOfMonth: '25',
        });
        break;
      case 'weekly':
        setForm({
          ...form,
          name: 'Weekly Groceries',
          type: 'EXPENSE',
          frequency: 'WEEKLY',
          dayOfWeek: '6',
        });
        break;
      case 'subscription':
        setForm({
          ...form,
          name: 'Streaming Subscription',
          type: 'EXPENSE',
          frequency: 'MONTHLY',
          dayOfMonth: '15',
        });
        break;
    }
  };

  const getNextExecutionPreview = () => {
    if (!form.startDate || !form.frequency) return null;

    const startDate = new Date(form.startDate);
    let previewText = `First transaction: ${startDate.toLocaleDateString()}`;

    if (form.frequency === 'MONTHLY' && form.dayOfMonth) {
      previewText = `First transaction: Day ${form.dayOfMonth} of ${startDate.toLocaleDateString('en-US', { month: 'long', year: 'numeric' })}`;
    } else if (form.frequency === 'WEEKLY' && form.dayOfWeek) {
      const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
      previewText = `First transaction: Next ${days[form.dayOfWeek - 1]} after ${startDate.toLocaleDateString()}`;
    }

    return previewText;
  };

  return (
    <div>
      <div style={styles.header}>
        <h1>Recurring Transactions</h1>
        <button onClick={() => setShowForm(!showForm)} style={styles.addButton}>
          <PlusIcon style={styles.icon} />
          Add Recurring Transaction
        </button>
      </div>

      {showForm && (
        <div style={styles.formContainer}>
          <h3>{editingTransaction ? 'Edit' : 'New'} Recurring Transaction</h3>

          {!editingTransaction && (
            <div style={styles.examples}>
              <p style={styles.exampleTitle}>Quick setup templates:</p>
              <div style={styles.presets}>
                <button type="button" onClick={() => setFormPreset('rent')} style={styles.presetButton}>
                  🏠 Monthly Rent (1st)
                </button>
                <button type="button" onClick={() => setFormPreset('salary')} style={styles.presetButton}>
                  💰 Monthly Salary (25th)
                </button>
                <button type="button" onClick={() => setFormPreset('weekly')} style={styles.presetButton}>
                  🛒 Weekly Shopping
                </button>
                <button type="button" onClick={() => setFormPreset('subscription')} style={styles.presetButton}>
                  📺 Monthly Subscription
                </button>
              </div>
            </div>
          )}

          <form onSubmit={handleSubmit} style={styles.form}>
            <div style={styles.formRow}>
              <div style={styles.fieldGroup}>
                <label style={styles.label}>
                  Transaction Name *
                  <span style={styles.hint}>e.g., "Monthly Rent", "Netflix Subscription"</span>
                </label>
                <input
                  type="text"
                  placeholder="Enter name"
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                  required
                  style={styles.input}
                />
              </div>

              <div style={styles.fieldGroup}>
                <label style={styles.label}>
                  Amount *
                  <span style={styles.hint}>How much per transaction</span>
                </label>
                <input
                  type="number"
                  step="0.01"
                  placeholder="0.00"
                  value={form.amount}
                  onChange={(e) => setForm({ ...form, amount: e.target.value })}
                  required
                  style={styles.input}
                />
              </div>
            </div>

            <div style={styles.formRow}>
              <div style={styles.fieldGroup}>
                <label style={styles.label}>Type *</label>
                <select
                  value={form.type}
                  onChange={(e) => setForm({ ...form, type: e.target.value, categoryId: '' })}
                  style={styles.select}
                >
                  <option value="EXPENSE">Expense (money out)</option>
                  <option value="INCOME">Income (money in)</option>
                </select>
              </div>

              <div style={styles.fieldGroup}>
                <label style={styles.label}>Category *</label>
                <select
                  value={form.categoryId}
                  onChange={(e) => setForm({ ...form, categoryId: e.target.value })}
                  required
                  style={styles.select}
                >
                  <option value="">Select Category</option>
                  {filteredCategories.map(cat => (
                    <option key={cat.id} value={cat.id}>{cat.name}</option>
                  ))}
                </select>
              </div>
            </div>

            <div style={styles.formRow}>
              <div style={styles.fieldGroup}>
                <label style={styles.label}>
                  How often? *
                  <span style={styles.hint}>Frequency of the transaction</span>
                </label>
                <select
                  value={form.frequency}
                  onChange={(e) => setForm({ ...form, frequency: e.target.value, dayOfMonth: '', dayOfWeek: '' })}
                  style={styles.select}
                >
                  <option value="DAILY">Every day</option>
                  <option value="WEEKLY">Every week</option>
                  <option value="BIWEEKLY">Every 2 weeks</option>
                  <option value="MONTHLY">Every month</option>
                  <option value="QUARTERLY">Every 3 months</option>
                  <option value="YEARLY">Every year</option>
                </select>
              </div>

              {form.frequency === 'MONTHLY' && (
                <div style={styles.fieldGroup}>
                  <label style={styles.label}>
                    Day of month
                    <span style={styles.hint}>Which day? (1-31)</span>
                  </label>
                  <input
                    type="number"
                    min="1"
                    max="31"
                    placeholder="e.g., 1 for first day"
                    value={form.dayOfMonth}
                    onChange={(e) => setForm({ ...form, dayOfMonth: e.target.value })}
                    style={styles.input}
                  />
                </div>
              )}

              {form.frequency === 'WEEKLY' && (
                <div style={styles.fieldGroup}>
                  <label style={styles.label}>
                    Day of week
                    <span style={styles.hint}>Which day?</span>
                  </label>
                  <select
                    value={form.dayOfWeek}
                    onChange={(e) => setForm({ ...form, dayOfWeek: e.target.value })}
                    style={styles.select}
                  >
                    <option value="">Select day</option>
                    <option value="1">Every Monday</option>
                    <option value="2">Every Tuesday</option>
                    <option value="3">Every Wednesday</option>
                    <option value="4">Every Thursday</option>
                    <option value="5">Every Friday</option>
                    <option value="6">Every Saturday</option>
                    <option value="7">Every Sunday</option>
                  </select>
                </div>
              )}
            </div>

            <div style={styles.formRow}>
              <div style={styles.fieldGroup}>
                <label style={styles.label}>
                  Start Date *
                  <span style={styles.hint}>When should this begin?</span>
                </label>
                <input
                  type="date"
                  value={form.startDate}
                  onChange={(e) => setForm({ ...form, startDate: e.target.value })}
                  required
                  style={styles.input}
                />
              </div>

              <div style={styles.fieldGroup}>
                <label style={styles.label}>
                  End Date
                  <span style={styles.hint}>Optional - leave empty for ongoing</span>
                </label>
                <input
                  type="date"
                  placeholder="No end date"
                  value={form.endDate}
                  onChange={(e) => setForm({ ...form, endDate: e.target.value })}
                  style={styles.input}
                />
              </div>
            </div>

            {getNextExecutionPreview() && (
              <div style={styles.nextExecutionHint}>
                💡 {getNextExecutionPreview()}
              </div>
            )}

            <div style={styles.fieldGroup}>
              <label style={styles.label}>
                Description
                <span style={styles.hint}>Optional notes</span>
              </label>
              <textarea
                placeholder="Add any notes here..."
                value={form.description}
                onChange={(e) => setForm({ ...form, description: e.target.value })}
                style={styles.textarea}
                rows="3"
              />
            </div>

            <div style={styles.formActions}>
              <label style={styles.checkboxLabel}>
                <input
                  type="checkbox"
                  checked={form.active}
                  onChange={(e) => setForm({ ...form, active: e.target.checked })}
                />
                <span>Active (uncheck to pause)</span>
              </label>

              <div style={styles.buttons}>
                <button type="submit" style={styles.submitButton}>
                  {editingTransaction ? 'Update' : 'Create'}
                </button>
                <button type="button" onClick={resetForm} style={styles.cancelButton}>
                  Cancel
                </button>
              </div>
            </div>
          </form>
        </div>
      )}

      {isLoading ? (
        <div style={styles.loading}>Loading recurring transactions...</div>
      ) : transactions.length === 0 ? (
        <div style={styles.empty}>
          <h3>No recurring transactions yet</h3>
          <p>Create recurring transactions to automate your regular payments and income!</p>
          <p>Perfect for rent, salary, subscriptions, and other regular transactions.</p>
        </div>
      ) : (
        <div style={styles.grid}>
          {transactions.map(transaction => (
            <RecurringTransactionCard
              key={transaction.id}
              transaction={transaction}
              onEdit={handleEdit}
              onDelete={handleDelete}
              onExecute={handleExecute}
            />
          ))}
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
  examples: {
    marginBottom: '1.5rem',
    padding: '1rem',
    backgroundColor: '#f8f9fa',
    borderRadius: '4px',
  },
  exampleTitle: {
    margin: '0 0 0.75rem 0',
    fontSize: '0.875rem',
    color: '#666',
  },
  presets: {
    display: 'flex',
    flexWrap: 'wrap',
    gap: '0.5rem',
  },
  presetButton: {
    padding: '0.5rem 1rem',
    backgroundColor: 'white',
    border: '1px solid #e0e0e0',
    borderRadius: '20px',
    fontSize: '0.875rem',
    cursor: 'pointer',
    transition: 'all 0.2s',
  },
  form: {
    marginTop: '1rem',
  },
  formRow: {
    display: 'grid',
    gridTemplateColumns: '1fr 1fr',
    gap: '1rem',
    marginBottom: '1rem',
  },
  fieldGroup: {
    display: 'flex',
    flexDirection: 'column',
    gap: '0.25rem',
  },
  label: {
    fontSize: '0.875rem',
    fontWeight: '500',
    color: '#333',
  },
  hint: {
    display: 'block',
    fontSize: '0.75rem',
    color: '#666',
    fontWeight: 'normal',
    marginTop: '0.125rem',
  },
  input: {
    padding: '0.75rem',
    border: '1px solid #ddd',
    borderRadius: '4px',
    fontSize: '0.875rem',
  },
  select: {
    padding: '0.75rem',
    border: '1px solid #ddd',
    borderRadius: '4px',
    fontSize: '0.875rem',
  },
  textarea: {
    width: '100%',
    padding: '0.75rem',
    border: '1px solid #ddd',
    borderRadius: '4px',
    fontSize: '0.875rem',
    resize: 'vertical',
  },
  nextExecutionHint: {
    padding: '1rem',
    backgroundColor: '#e3f2fd',
    borderRadius: '4px',
    marginBottom: '1rem',
    fontSize: '0.875rem',
    color: '#1976d2',
  },
  formActions: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: '1.5rem',
  },
  checkboxLabel: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
    fontSize: '0.875rem',
  },
  buttons: {
    display: 'flex',
    gap: '0.5rem',
  },
  submitButton: {
    padding: '0.75rem 1.5rem',
    backgroundColor: '#3498db',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontWeight: '500',
  },
  cancelButton: {
    padding: '0.75rem 1.5rem',
    backgroundColor: '#95a5a6',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
  },
  loading: {
    textAlign: 'center',
    padding: '2rem',
    fontSize: '1.125rem',
    color: '#666',
  },
  empty: {
    textAlign: 'center',
    padding: '4rem 2rem',
    backgroundColor: 'white',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    color: '#666',
  },
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(350px, 1fr))',
    gap: '1.5rem',
  },
};

export default RecurringTransactionsPage;
