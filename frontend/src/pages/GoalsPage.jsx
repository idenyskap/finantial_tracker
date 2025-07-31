import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { goalService } from '../services/goalService';
import { categoryService } from '../services/categoryService';
import GoalCard from '../components/goals/GoalCard';
import { toast } from 'sonner';
import { PlusIcon, FunnelIcon } from '@heroicons/react/24/outline';

function GoalsPage() {
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

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount);
  };

  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  const minDate = tomorrow.toISOString().split('T')[0];

  return (
    <div>
      <div style={styles.header}>
        <h1>Financial Goals</h1>
        <div style={styles.headerActions}>
          <button
            onClick={() => setActiveOnly(!activeOnly)}
            style={styles.filterButton}
          >
            <FunnelIcon style={styles.icon} />
            {activeOnly ? 'Show All' : 'Active Only'}
          </button>
          <button onClick={() => setShowForm(!showForm)} style={styles.addButton}>
            <PlusIcon style={styles.icon} />
            New Goal
          </button>
        </div>
      </div>

      <div style={styles.statsGrid}>
        <div style={styles.statCard}>
          <h3 style={styles.statValue}>{activeGoals.length}</h3>
          <p style={styles.statLabel}>Active Goals</p>
        </div>
        <div style={styles.statCard}>
          <h3 style={styles.statValue}>{completedGoals.length}</h3>
          <p style={styles.statLabel}>Completed</p>
        </div>
        <div style={styles.statCard}>
          <h3 style={styles.statValue}>{formatCurrency(totalSaved)}</h3>
          <p style={styles.statLabel}>Total Saved</p>
        </div>
        <div style={styles.statCard}>
          <h3 style={styles.statValue}>{formatCurrency(totalTarget)}</h3>
          <p style={styles.statLabel}>Total Target</p>
        </div>
      </div>

      {showForm && (
        <div style={styles.formContainer}>
          <h3>{editingGoal ? 'Edit' : 'New'} Goal</h3>

          <form onSubmit={handleSubmit} style={styles.form}>
            <div style={styles.formRow}>
              <div style={styles.fieldGroup}>
                <label style={styles.label}>Goal Name *</label>
                <input
                  type="text"
                  placeholder="e.g., Emergency Fund, New Car, Vacation"
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                  required
                  style={styles.input}
                />
              </div>

              <div style={styles.fieldGroup}>
                <label style={styles.label}>Target Amount *</label>
                <input
                  type="number"
                  step="0.01"
                  placeholder="0.00"
                  value={form.targetAmount}
                  onChange={(e) => setForm({ ...form, targetAmount: e.target.value })}
                  required
                  style={styles.input}
                />
              </div>
            </div>

            <div style={styles.formRow}>
              <div style={styles.fieldGroup}>
                <label style={styles.label}>Target Date *</label>
                <input
                  type="date"
                  value={form.targetDate}
                  onChange={(e) => setForm({ ...form, targetDate: e.target.value })}
                  min={minDate}
                  required
                  style={styles.input}
                />
              </div>

              <div style={styles.fieldGroup}>
                <label style={styles.label}>Priority</label>
                <select
                  value={form.priority}
                  onChange={(e) => setForm({ ...form, priority: e.target.value })}
                  style={styles.select}
                >
                  <option value="LOW">Low</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="HIGH">High</option>
                  <option value="CRITICAL">Critical</option>
                </select>
              </div>
            </div>

            <div style={styles.formRow}>
              <div style={styles.fieldGroup}>
                <label style={styles.label}>Link to Category (optional)</label>
                <select
                  value={form.categoryId}
                  onChange={(e) => setForm({ ...form, categoryId: e.target.value })}
                  style={styles.select}
                >
                  <option value="">No category</option>
                  {categories.map(cat => (
                    <option key={cat.id} value={cat.id}>{cat.name}</option>
                  ))}
                </select>
              </div>

              <div style={styles.fieldGroup}>
                <label style={styles.label}>Color</label>
                <input
                  type="color"
                  value={form.color}
                  onChange={(e) => setForm({ ...form, color: e.target.value })}
                  style={styles.colorInput}
                />
              </div>
            </div>

            <div style={styles.fieldGroup}>
              <label style={styles.label}>Description</label>
              <textarea
                placeholder="What is this goal for? Any specific plans?"
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
              <button type="submit" style={styles.submitButton}>
                {editingGoal ? 'Update Goal' : 'Create Goal'}
              </button>
              <button type="button" onClick={resetForm} style={styles.cancelButton}>
                Cancel
              </button>
            </div>
          </form>
        </div>
      )}

      {isLoading ? (
        <div style={styles.loading}>Loading goals...</div>
      ) : goals.length === 0 ? (
        <div style={styles.empty}>
          <h3>No goals yet</h3>
          <p>Set your first financial goal and start saving!</p>
          <p>Whether it's an emergency fund, a vacation, or a major purchase - every journey starts with a goal.</p>
        </div>
      ) : (
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
  headerActions: {
    display: 'flex',
    gap: '1rem',
  },
  filterButton: {
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
  statsGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
    gap: '1rem',
    marginBottom: '2rem',
  },
  statCard: {
    backgroundColor: 'white',
    padding: '1.5rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    textAlign: 'center',
  },
  statValue: {
    margin: '0 0 0.5rem 0',
    fontSize: '2rem',
    color: '#2c3e50',
  },
  statLabel: {
    margin: 0,
    color: '#666',
    fontSize: '0.875rem',
  },
  formContainer: {
    backgroundColor: 'white',
    padding: '1.5rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    marginBottom: '2rem',
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
  colorInput: {
    width: '100%',
    height: '42px',
    padding: '0.25rem',
    border: '1px solid #ddd',
    borderRadius: '4px',
    cursor: 'pointer',
  },
  textarea: {
    width: '100%',
    padding: '0.75rem',
    border: '1px solid #ddd',
    borderRadius: '4px',
    fontSize: '0.875rem',
    resize: 'vertical',
  },
  calculationHelper: {
    backgroundColor: '#e3f2fd',
    padding: '1rem',
    borderRadius: '4px',
    marginBottom: '1rem',
  },
  helperTitle: {
    margin: '0 0 0.5rem 0',
    fontSize: '0.875rem',
    color: '#1976d2',
    fontWeight: '500',
  },
  helperGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(3, 1fr)',
    gap: '1rem',
    fontSize: '0.875rem',
    color: '#333',
  },
  formActions: {
    display: 'flex',
    gap: '0.5rem',
    justifyContent: 'flex-end',
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

export default GoalsPage;
