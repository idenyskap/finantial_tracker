import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { budgetService } from '../services/budgetService';
import { categoryService } from '../services/categoryService';
import BudgetProgress from '../components/budgets/BudgetProgress';
import { toast } from 'sonner';

function BudgetsPage() {
  const queryClient = useQueryClient();
  const [showForm, setShowForm] = useState(false);
  const [editingBudget, setEditingBudget] = useState(null);
  const [form, setForm] = useState({
    name: '',
    amount: '',
    period: 'MONTHLY',
    categoryId: '',
    notifyThreshold: 80,
  });

  const { data: budgetsData, isLoading } = useQuery({
    queryKey: ['budgets'],
    queryFn: () => budgetService.getAll(),
  });

  const { data: categoriesData } = useQuery({
    queryKey: ['categories'],
    queryFn: () => categoryService.getAll(),
  });

  const createMutation = useMutation({
    mutationFn: budgetService.create,
    onSuccess: () => {
      queryClient.invalidateQueries(['budgets']);
      toast.success('Budget created successfully');
      resetForm();
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error creating budget');
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }) => budgetService.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries(['budgets']);
      toast.success('Budget updated successfully');
      resetForm();
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error updating budget');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: budgetService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries(['budgets']);
      toast.success('Budget deleted successfully');
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error deleting budget');
    },
  });

  const budgets = budgetsData?.data || [];
  const categories = categoriesData?.data || [];
  const expenseCategories = categories.filter(cat => cat.type === 'EXPENSE');

  const handleSubmit = (e) => {
    e.preventDefault();

    const data = {
      ...form,
      amount: parseFloat(form.amount),
      categoryId: form.categoryId || null,
    };

    if (editingBudget) {
      updateMutation.mutate({ id: editingBudget.id, data });
    } else {
      createMutation.mutate(data);
    }
  };

  const handleEdit = (budget) => {
    setEditingBudget(budget);
    setForm({
      name: budget.name,
      amount: budget.amount,
      period: budget.period,
      categoryId: budget.categoryId || '',
      notifyThreshold: budget.notifyThreshold,
    });
    setShowForm(true);
  };

  const handleDelete = (id) => {
    if (window.confirm('Are you sure you want to delete this budget?')) {
      deleteMutation.mutate(id);
    }
  };

  const resetForm = () => {
    setForm({
      name: '',
      amount: '',
      period: 'MONTHLY',
      categoryId: '',
      notifyThreshold: 80,
    });
    setEditingBudget(null);
    setShowForm(false);
  };

  const periods = [
    { value: 'WEEKLY', label: 'Weekly' },
    { value: 'MONTHLY', label: 'Monthly' },
    { value: 'QUARTERLY', label: 'Quarterly' },
    { value: 'YEARLY', label: 'Yearly' },
  ];

  return (
    <div>
      <div style={styles.header}>
        <h1>Budgets</h1>
        <button onClick={() => setShowForm(!showForm)} style={styles.addButton}>
          + Add Budget
        </button>
      </div>

      {showForm && (
        <div style={styles.formContainer}>
          <h3>{editingBudget ? 'Edit Budget' : 'New Budget'}</h3>
          <form onSubmit={handleSubmit} style={styles.form}>
            <div style={styles.formRow}>
              <input
                type="text"
                placeholder="Budget name"
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
                required
                style={styles.input}
              />

              <input
                type="number"
                step="0.01"
                placeholder="Amount"
                value={form.amount}
                onChange={(e) => setForm({ ...form, amount: e.target.value })}
                required
                style={styles.input}
              />
            </div>

            <div style={styles.formRow}>
              <select
                value={form.period}
                onChange={(e) => setForm({ ...form, period: e.target.value })}
                style={styles.select}
              >
                {periods.map(period => (
                  <option key={period.value} value={period.value}>
                    {period.label}
                  </option>
                ))}
              </select>

              <select
                value={form.categoryId}
                onChange={(e) => setForm({ ...form, categoryId: e.target.value })}
                style={styles.select}
              >
                <option value="">All Categories</option>
                {expenseCategories.map(cat => (
                  <option key={cat.id} value={cat.id}>
                    {cat.name}
                  </option>
                ))}
              </select>
            </div>

            <div style={styles.thresholdContainer}>
              <label style={styles.label}>
                Alert when spending reaches: {form.notifyThreshold}%
              </label>
              <input
                type="range"
                min="50"
                max="100"
                step="5"
                value={form.notifyThreshold}
                onChange={(e) => setForm({ ...form, notifyThreshold: parseInt(e.target.value) })}
                style={styles.slider}
              />
            </div>

            <div style={styles.formButtons}>
              <button type="submit" style={styles.submitButton}>
                {editingBudget ? 'Update' : 'Create'}
              </button>
              <button type="button" onClick={resetForm} style={styles.cancelButton}>
                Cancel
              </button>
            </div>
          </form>
        </div>
      )}

      {isLoading ? (
        <div style={styles.loading}>Loading budgets...</div>
      ) : budgets.length === 0 ? (
        <div style={styles.empty}>
          <p>No budgets yet. Create your first budget to start tracking your spending!</p>
        </div>
      ) : (
        <div style={styles.budgetsGrid}>
          {budgets.map(budget => (
            <div key={budget.id} style={styles.budgetItem}>
              <BudgetProgress budget={budget} />
              <div style={styles.budgetActions}>
                <button onClick={() => handleEdit(budget)} style={styles.editButton}>
                  Edit
                </button>
                <button onClick={() => handleDelete(budget.id)} style={styles.deleteButton}>
                  Delete
                </button>
              </div>
            </div>
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
    padding: '0.75rem 1rem',
    backgroundColor: '#27ae60',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '1rem',
    fontWeight: '500',
  },
  formContainer: {
    backgroundColor: 'white',
    padding: '1.5rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    marginBottom: '2rem',
  },
  form: {
    display: 'flex',
    flexDirection: 'column',
    gap: '1rem',
  },
  formRow: {
    display: 'grid',
    gridTemplateColumns: '1fr 1fr',
    gap: '1rem',
  },
  input: {
    padding: '0.75rem',
    border: '1px solid #ddd',
    borderRadius: '4px',
    fontSize: '1rem',
  },
  select: {
    padding: '0.75rem',
    border: '1px solid #ddd',
    borderRadius: '4px',
    fontSize: '1rem',
  },
  thresholdContainer: {
    marginTop: '0.5rem',
  },
  label: {
    display: 'block',
    marginBottom: '0.5rem',
    fontSize: '0.875rem',
    color: '#666',
  },
  slider: {
    width: '100%',
    cursor: 'pointer',
  },
  formButtons: {
    display: 'flex',
    gap: '0.5rem',
  },
  submitButton: {
    flex: 1,
    padding: '0.75rem',
    backgroundColor: '#3498db',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontWeight: '500',
  },
  cancelButton: {
    flex: 1,
    padding: '0.75rem',
    backgroundColor: '#95a5a6',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
  },
  loading: {
    textAlign: 'center',
    padding: '2rem',
  },
  empty: {
    textAlign: 'center',
    padding: '4rem',
    backgroundColor: 'white',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    color: '#666',
  },
  budgetsGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(350px, 1fr))',
    gap: '1.5rem',
  },
  budgetItem: {
    position: 'relative',
  },
  budgetActions: {
    position: 'absolute',
    top: '1rem',
    right: '1rem',
    display: 'flex',
    gap: '0.5rem',
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
};

export default BudgetsPage;
