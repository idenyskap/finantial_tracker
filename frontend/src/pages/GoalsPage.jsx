import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { goalService } from '../services/goalService';
import { categoryService } from '../services/categoryService';
import GoalCard from '../components/goals/GoalCard';
import { toast } from 'sonner';
import { PlusIcon, FunnelIcon } from '@heroicons/react/24/outline';
import { useThemedStyles } from '../hooks/useThemedStyles';
import { useCurrency } from '../contexts/CurrencyContext';

function GoalsPage() {
  const styles = useThemedStyles(getStyles);
  const { formatCurrency } = useCurrency();
  const queryClient = useQueryClient();
  const [showForm, setShowForm] = useState(false);
  const [editingGoal, setEditingGoal] = useState(null);
  const [activeOnly, setActiveOnly] = useState(true);
  const [form, setForm] = useState({
    name: '',
    description: '',
    targetAmount: '',
    targetDate: '',
    categoryId: '',
    priority: 'MEDIUM',
    color: '#3498db',
    icon: '',
  });

  const { data: goalsData, isLoading } = useQuery({
    queryKey: ['goals', activeOnly],
    queryFn: () => goalService.getAll(activeOnly),
  });

  const { data: categoriesData } = useQuery({
    queryKey: ['categories'],
    queryFn: () => categoryService.getAll(),
  });

  const createMutation = useMutation({
    mutationFn: goalService.create,
    onSuccess: () => {
      queryClient.invalidateQueries(['goals']);
      queryClient.invalidateQueries(['dashboard']);
      toast.success('Goal created successfully');
      resetForm();
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error creating goal');
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }) => goalService.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries(['goals']);
      toast.success('Goal updated successfully');
      resetForm();
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error updating goal');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: goalService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries(['goals']);
      toast.success('Goal deleted successfully');
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error deleting goal');
    },
  });

  const contributeMutation = useMutation({
    mutationFn: goalService.contribute,
    onSuccess: () => {
      queryClient.invalidateQueries(['goals']);
      queryClient.invalidateQueries(['transactions']);
      queryClient.invalidateQueries(['dashboard']);
      toast.success('Contribution added successfully');
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error adding contribution');
    },
  });

  const statusMutation = useMutation({
    mutationFn: ({ id, action }) => {
      switch (action) {
        case 'pause':
          return goalService.pause(id);
        case 'resume':
          return goalService.resume(id);
        case 'cancel':
          return goalService.cancel(id);
        default:
          throw new Error('Invalid action');
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['goals']);
      toast.success('Goal status updated');
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error updating status');
    },
  });

  const goals = goalsData?.data || [];
  const categories = categoriesData?.data || [];

  const handleSubmit = (e) => {
    e.preventDefault();

    const data = {
      ...form,
      targetAmount: parseFloat(form.targetAmount),
      categoryId: form.categoryId || null,
    };

    if (editingGoal) {
      updateMutation.mutate({ id: editingGoal.id, data });
    } else {
      createMutation.mutate(data);
    }
  };

  const handleEdit = (goal) => {
    setEditingGoal(goal);
    setForm({
      name: goal.name,
      description: goal.description || '',
      targetAmount: goal.targetAmount,
      targetDate: goal.targetDate,
      categoryId: goal.categoryId || '',
      priority: goal.priority,
      color: goal.color || '#3498db',
      icon: goal.icon || '',
    });
    setShowForm(true);
  };

  const handleDelete = (id) => {
    if (window.confirm('Are you sure you want to delete this goal?')) {
      deleteMutation.mutate(id);
    }
  };

  const handleContribute = (contribution) => {
    contributeMutation.mutate(contribution);
  };

  const handleStatusChange = (id, action) => {
    statusMutation.mutate({ id, action });
  };

  const resetForm = () => {
    setForm({
      name: '',
      description: '',
      targetAmount: '',
      targetDate: '',
      categoryId: '',
      priority: 'MEDIUM',
      color: '#3498db',
      icon: '',
    });
    setEditingGoal(null);
    setShowForm(false);
  };

  const activeGoals = goals.filter(g => g.status === 'ACTIVE');
  const completedGoals = goals.filter(g => g.status === 'COMPLETED');
  const totalSaved = goals.reduce((sum, g) => sum + (g.currentAmount || 0), 0);
  const totalTarget = activeGoals.reduce((sum, g) => sum + (g.targetAmount || 0), 0);


  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  const minDate = tomorrow.toISOString().split('T')[0];

  return (
    <div style={styles.container}>
      {/* Header */}
      <div style={styles.header}>
        <div style={styles.headerContent}>
          <h1 style={styles.title}>Financial Goals</h1>
          <p style={styles.subtitle}>Track your savings targets and achieve your dreams</p>
        </div>
        <div style={styles.headerActions}>
          <button
            onClick={() => setActiveOnly(!activeOnly)}
            style={styles.filterButton}
          >
            <span style={styles.buttonIcon}>🔍</span>
            {activeOnly ? 'Show All' : 'Active Only'}
          </button>
          <button onClick={() => setShowForm(!showForm)} style={styles.addButton}>
            <span style={styles.addButtonIcon}>+</span>
            New Goal
          </button>
        </div>
      </div>

      {/* Stats Overview */}
      <div style={styles.statsSection}>
        <div style={styles.statsGrid}>
          <div style={styles.statCard}>
            <div style={styles.statIcon}>🎯</div>
            <div style={styles.statContent}>
              <h3 style={styles.statValue}>{activeGoals.length}</h3>
              <p style={styles.statLabel}>Active Goals</p>
            </div>
          </div>
          <div style={styles.statCard}>
            <div style={styles.statIcon}>✅</div>
            <div style={styles.statContent}>
              <h3 style={styles.statValue}>{completedGoals.length}</h3>
              <p style={styles.statLabel}>Completed</p>
            </div>
          </div>
          <div style={styles.statCard}>
            <div style={styles.statIcon}>💰</div>
            <div style={styles.statContent}>
              <h3 style={styles.statValue}>{formatCurrency(totalSaved)}</h3>
              <p style={styles.statLabel}>Total Saved</p>
            </div>
          </div>
          <div style={styles.statCard}>
            <div style={styles.statIcon}>🎯</div>
            <div style={styles.statContent}>
              <h3 style={styles.statValue}>{formatCurrency(totalTarget)}</h3>
              <p style={styles.statLabel}>Total Target</p>
            </div>
          </div>
        </div>
      </div>

      {/* Form */}
      {showForm && (
        <div style={styles.formCard}>
          <div style={styles.formHeader}>
            <h3 style={styles.formTitle}>
              {editingGoal ? 'Edit Goal' : 'Create New Goal'}
            </h3>
            <button onClick={resetForm} style={styles.closeButton}>×</button>
          </div>
          
          <form onSubmit={handleSubmit} style={styles.form}>
            <div style={styles.formRow}>
              <div style={styles.formGroup}>
                <label style={styles.label}>Goal Name</label>
                <input
                  type="text"
                  placeholder="e.g., Emergency Fund, New Car, Vacation"
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                  required
                  style={styles.input}
                />
              </div>

              <div style={styles.formGroup}>
                <label style={styles.label}>Target Amount</label>
                <input
                  type="number"
                  step="0.01"
                  placeholder="Enter target amount"
                  value={form.targetAmount}
                  onChange={(e) => setForm({ ...form, targetAmount: e.target.value })}
                  required
                  style={styles.input}
                />
              </div>
            </div>

            <div style={styles.formRow}>
              <div style={styles.formGroup}>
                <label style={styles.label}>Target Date</label>
                <input
                  type="date"
                  value={form.targetDate}
                  onChange={(e) => setForm({ ...form, targetDate: e.target.value })}
                  min={minDate}
                  required
                  style={styles.input}
                />
              </div>

              <div style={styles.formGroup}>
                <label style={styles.label}>Priority Level</label>
                <select
                  value={form.priority}
                  onChange={(e) => setForm({ ...form, priority: e.target.value })}
                  style={styles.select}
                >
                  <option value="LOW">🟢 Low Priority</option>
                  <option value="MEDIUM">🟡 Medium Priority</option>
                  <option value="HIGH">🟠 High Priority</option>
                  <option value="CRITICAL">🔴 Critical Priority</option>
                </select>
              </div>
            </div>

            <div style={styles.formRow}>
              <div style={styles.formGroup}>
                <label style={styles.label}>Category (Optional)</label>
                <select
                  value={form.categoryId}
                  onChange={(e) => setForm({ ...form, categoryId: e.target.value })}
                  style={styles.select}
                >
                  <option value="">📂 No category</option>
                  {categories.map(cat => (
                    <option key={cat.id} value={cat.id}>{cat.name}</option>
                  ))}
                </select>
              </div>

              <div style={styles.formGroup}>
                <label style={styles.label}>Goal Color</label>
                <input
                  type="color"
                  value={form.color}
                  onChange={(e) => setForm({ ...form, color: e.target.value })}
                  style={styles.colorInput}
                />
              </div>
            </div>

            <div style={styles.formGroup}>
              <label style={styles.label}>Description (Optional)</label>
              <textarea
                placeholder="What is this goal for? Any specific plans or motivation?"
                value={form.description}
                onChange={(e) => setForm({ ...form, description: e.target.value })}
                style={styles.textarea}
                rows="3"
              />
            </div>

            {form.targetAmount && form.targetDate && (
              <div style={styles.calculationHelper}>
                <p style={styles.helperTitle}>💡 To reach your goal:</p>
                {(() => {
                  const target = parseFloat(form.targetAmount) || 0;
                  const days = Math.ceil((new Date(form.targetDate) - new Date()) / (1000 * 60 * 60 * 24));
                  const perDay = target / days;
                  const perWeek = perDay * 7;
                  const perMonth = perDay * 30;

                  return (
                    <div style={styles.helperGrid}>
                      <span>Save {formatCurrency(perDay)} per day</span>
                      <span>Save {formatCurrency(perWeek)} per week</span>
                      <span>Save {formatCurrency(perMonth)} per month</span>
                    </div>
                  );
                })()}
              </div>
            )}

            <div style={styles.formActions}>
              <button type="button" onClick={resetForm} style={styles.cancelButton}>
                Cancel
              </button>
              <button type="submit" style={styles.submitButton}>
                {editingGoal ? 'Update Goal' : 'Create Goal'}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Content */}
      {isLoading ? (
        <div style={styles.loading}>
          <div style={styles.loadingSpinner}></div>
          <p>Loading your goals...</p>
        </div>
      ) : goals.length === 0 ? (
        <div style={styles.emptyState}>
          <div style={styles.emptyIcon}>🎯</div>
          <p style={styles.emptyText}>No financial goals yet</p>
          <p style={styles.emptySubtext}>Set your first goal and start your savings journey today!</p>
          <p style={styles.emptyDescription}>Whether it's an emergency fund, a vacation, or a major purchase - every journey starts with a goal.</p>
        </div>
      ) : (
        <div style={styles.content}>
          <div style={styles.grid}>
            {goals.map(goal => (
              <GoalCard
                key={goal.id}
                goal={goal}
                onEdit={handleEdit}
                onDelete={handleDelete}
                onContribute={handleContribute}
                onStatusChange={handleStatusChange}
              />
            ))}
          </div>
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
  headerActions: {
    display: 'flex',
    gap: '0.75rem',
  },
  filterButton: {
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
    backgroundColor: '#f59e0b',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '0.95rem',
    fontWeight: '600',
    transition: 'all 0.2s ease',
    boxShadow: '0 2px 4px rgba(245, 158, 11, 0.2)',
  },
  buttonIcon: {
    fontSize: '1rem',
  },
  addButtonIcon: {
    fontSize: '1.2rem',
    fontWeight: 'bold',
  },
  statsSection: {
    backgroundColor: theme.cardBackground,
    borderRadius: '12px',
    boxShadow: theme.shadow,
    marginBottom: '2rem',
    overflow: 'hidden',
    border: `1px solid ${theme.cardBorder}`,
  },
  statsGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
    gap: '0',
  },
  statCard: {
    display: 'flex',
    alignItems: 'center',
    gap: '1rem',
    padding: '1.5rem',
    borderRight: `1px solid ${theme.border}`,
    transition: 'background-color 0.2s ease',
  },
  statIcon: {
    fontSize: '2rem',
    opacity: 0.8,
  },
  statContent: {
    flex: 1,
  },
  statValue: {
    margin: '0 0 0.25rem 0',
    fontSize: '1.5rem',
    fontWeight: '700',
    color: theme.text,
  },
  statLabel: {
    margin: 0,
    color: theme.textSecondary,
    fontSize: '0.875rem',
    fontWeight: '500',
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
    color: theme.textSecondary,
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
  colorInput: {
    height: '42px',
    padding: '0.25rem',
    border: `1px solid ${theme.inputBorder}`,
    borderRadius: '8px',
    cursor: 'pointer',
    backgroundColor: theme.inputBackground,
  },
  textarea: {
    padding: '0.75rem',
    border: `1px solid ${theme.inputBorder}`,
    borderRadius: '8px',
    fontSize: '1rem',
    resize: 'vertical',
    backgroundColor: theme.inputBackground,
    color: theme.inputText,
    fontFamily: 'inherit',
  },
  calculationHelper: {
    backgroundColor: '#fef3c7',
    border: '1px solid #f59e0b',
    padding: '1rem',
    borderRadius: '8px',
    marginBottom: '1rem',
  },
  helperTitle: {
    margin: '0 0 0.75rem 0',
    fontSize: '0.875rem',
    color: '#92400e',
    fontWeight: '600',
  },
  helperGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(3, 1fr)',
    gap: '1rem',
    fontSize: '0.875rem',
    color: theme.text,
    fontWeight: '500',
  },
  formActions: {
    display: 'flex',
    gap: '0.75rem',
    paddingTop: '1rem',
    borderTop: `1px solid ${theme.border}`,
  },
  cancelButton: {
    flex: 1,
    padding: '0.75rem 1.5rem',
    backgroundColor: theme.backgroundSecondary,
    color: theme.textSecondary,
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontWeight: '500',
    transition: 'all 0.2s ease',
  },
  submitButton: {
    flex: 1,
    padding: '0.75rem 1.5rem',
    backgroundColor: theme.warning,
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
    backgroundColor: theme.cardBackground,
    borderRadius: '12px',
    boxShadow: theme.shadow,
    border: `1px solid ${theme.cardBorder}`,
    color: theme.text,
  },
  loadingSpinner: {
    width: '40px',
    height: '40px',
    border: `4px solid ${theme.borderLight}`,
    borderTop: `4px solid ${theme.warning}`,
    borderRadius: '50%',
    animation: 'spin 1s linear infinite',
    marginBottom: '1rem',
  },
  emptyState: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '3rem',
    backgroundColor: theme.cardBackground,
    borderRadius: '12px',
    boxShadow: theme.shadow,
    textAlign: 'center',
    border: `1px solid ${theme.cardBorder}`,
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
    marginBottom: '0.5rem',
  },
  emptyDescription: {
    color: theme.textTertiary,
    fontSize: '0.875rem',
    fontStyle: 'italic',
  },
  content: {
    backgroundColor: theme.cardBackground,
    borderRadius: '12px',
    boxShadow: theme.shadow,
    overflow: 'hidden',
    border: `1px solid ${theme.cardBorder}`,
  },
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(350px, 1fr))',
    gap: '1.5rem',
    padding: '1.5rem',
  },
});

export default GoalsPage;
