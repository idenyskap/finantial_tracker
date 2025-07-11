import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { categoryService } from '../services/categoryService';
import { toast } from 'sonner';
import { PlusIcon, PencilIcon, TrashIcon } from '@heroicons/react/24/outline';

function CategoriesPage() {
  const queryClient = useQueryClient();
  const [showForm, setShowForm] = useState(false);
  const [editingCategory, setEditingCategory] = useState(null);
  const [form, setForm] = useState({
    name: '',
    color: '#3498db',
    type: 'EXPENSE',
  });

  const { data: categoriesData, isLoading } = useQuery({
    queryKey: ['categories'],
    queryFn: () => categoryService.getAll(),
  });

  const createMutation = useMutation({
    mutationFn: categoryService.create,
    onSuccess: () => {
      queryClient.invalidateQueries(['categories']);
      toast.success('Category created successfully');
      resetForm();
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error creating category');
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }) => categoryService.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries(['categories']);
      toast.success('Category updated successfully');
      resetForm();
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error updating category');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: categoryService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries(['categories']);
      toast.success('Category deleted successfully');
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Error deleting category');
    },
  });

  const categories = categoriesData?.data || [];

  const incomeCategories = categories.filter(cat => cat.type === 'INCOME');
  const expenseCategories = categories.filter(cat => cat.type === 'EXPENSE');

  const handleSubmit = (e) => {
    e.preventDefault();

    if (editingCategory) {
      updateMutation.mutate({ id: editingCategory.id, data: form });
    } else {
      createMutation.mutate(form);
    }
  };

  const handleEdit = (category) => {
    setEditingCategory(category);
    setForm({
      name: category.name,
      color: category.color,
      type: category.type || 'EXPENSE',
    });
    setShowForm(true);
  };

  const handleDelete = (id) => {
    if (window.confirm('Are you sure you want to delete this category?')) {
      deleteMutation.mutate(id);
    }
  };

  const resetForm = () => {
    setForm({
      name: '',
      color: '#3498db',
      type: 'EXPENSE',
    });
    setEditingCategory(null);
    setShowForm(false);
  };

  const colors = [
    '#e74c3c', '#3498db', '#2ecc71', '#f39c12', '#9b59b6',
    '#1abc9c', '#34495e', '#e67e22', '#95a5a6', '#16a085',
  ];

  return (
    <div>
      <div style={styles.header}>
        <h1>Categories</h1>
        <button
          onClick={() => setShowForm(!showForm)}
          style={styles.addButton}
        >
          <PlusIcon style={styles.icon} />
          Add Category
        </button>
      </div>

      {showForm && (
        <div style={styles.formContainer}>
          <h3>{editingCategory ? 'Edit Category' : 'New Category'}</h3>
          <form onSubmit={handleSubmit} style={styles.form}>
            <input
              type="text"
              placeholder="Category name"
              value={form.name}
              onChange={(e) => setForm({ ...form, name: e.target.value })}
              required
              style={styles.input}
            />

            <select
              value={form.type}
              onChange={(e) => setForm({ ...form, type: e.target.value })}
              style={styles.select}
            >
              <option value="EXPENSE">Expense Category</option>
              <option value="INCOME">Income Category</option>
            </select>

            <div style={styles.colorSection}>
              <label style={styles.label}>Color:</label>
              <div style={styles.colorGrid}>
                {colors.map(color => (
                  <button
                    key={color}
                    type="button"
                    onClick={() => setForm({ ...form, color })}
                    style={{
                      ...styles.colorButton,
                      backgroundColor: color,
                      border: form.color === color ? '3px solid #333' : 'none',
                    }}
                  />
                ))}
              </div>
              <input
                type="color"
                value={form.color}
                onChange={(e) => setForm({ ...form, color: e.target.value })}
                style={styles.colorPicker}
              />
            </div>

            <div style={styles.formButtons}>
              <button type="submit" style={styles.submitButton}>
                {editingCategory ? 'Update' : 'Create'}
              </button>
              <button type="button" onClick={resetForm} style={styles.cancelButton}>
                Cancel
              </button>
            </div>
          </form>
        </div>
      )}

      {isLoading ? (
        <div style={styles.loading}>Loading categories...</div>
      ) : (
        <>
          <div style={styles.section}>
            <h2 style={styles.sectionTitle}>Income Categories</h2>
            <div style={styles.categoriesGrid}>
              {incomeCategories.length === 0 ? (
                <p style={styles.empty}>No income categories yet</p>
              ) : (
                incomeCategories.map(category => (
                  <div key={category.id} style={styles.categoryCard}>
                    <div style={styles.categoryHeader}>
                      <div style={styles.categoryInfo}>
                        <div
                          style={{
                            ...styles.colorDot,
                            backgroundColor: category.color,
                          }}
                        />
                        <span style={styles.categoryName}>{category.name}</span>
                      </div>
                      <div style={styles.categoryActions}>
                        <button
                          onClick={() => handleEdit(category)}
                          style={styles.iconButton}
                        >
                          <PencilIcon style={styles.smallIcon} />
                        </button>
                        <button
                          onClick={() => handleDelete(category.id)}
                          style={styles.iconButton}
                        >
                          <TrashIcon style={styles.smallIcon} />
                        </button>
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>

          <div style={styles.section}>
            <h2 style={styles.sectionTitle}>Expense Categories</h2>
            <div style={styles.categoriesGrid}>
              {expenseCategories.length === 0 ? (
                <p style={styles.empty}>No expense categories yet</p>
              ) : (
                expenseCategories.map(category => (
                  <div key={category.id} style={styles.categoryCard}>
                    <div style={styles.categoryHeader}>
                      <div style={styles.categoryInfo}>
                        <div
                          style={{
                            ...styles.colorDot,
                            backgroundColor: category.color,
                          }}
                        />
                        <span style={styles.categoryName}>{category.name}</span>
                      </div>
                      <div style={styles.categoryActions}>
                        <button
                          onClick={() => handleEdit(category)}
                          style={styles.iconButton}
                        >
                          <PencilIcon style={styles.smallIcon} />
                        </button>
                        <button
                          onClick={() => handleDelete(category.id)}
                          style={styles.iconButton}
                        >
                          <TrashIcon style={styles.smallIcon} />
                        </button>
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        </>
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
  form: {
    display: 'flex',
    flexDirection: 'column',
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
  colorSection: {
    marginBottom: '1rem',
  },
  label: {
    display: 'block',
    marginBottom: '0.5rem',
    fontWeight: '500',
  },
  colorGrid: {
    display: 'flex',
    gap: '0.5rem',
    marginBottom: '0.5rem',
    flexWrap: 'wrap',
  },
  colorButton: {
    width: '32px',
    height: '32px',
    borderRadius: '4px',
    cursor: 'pointer',
    transition: 'transform 0.2s',
  },
  colorPicker: {
    width: '100%',
    height: '40px',
    border: '1px solid #ddd',
    borderRadius: '4px',
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
  section: {
    marginBottom: '2rem',
  },
  sectionTitle: {
    fontSize: '1.5rem',
    marginBottom: '1rem',
  },
  categoriesGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))',
    gap: '1rem',
  },
  empty: {
    color: '#666',
    fontStyle: 'italic',
  },
  categoryCard: {
    backgroundColor: 'white',
    padding: '1rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  categoryHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  categoryInfo: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.75rem',
  },
  colorDot: {
    width: '24px',
    height: '24px',
    borderRadius: '50%',
  },
  categoryName: {
    fontWeight: '500',
  },
  categoryActions: {
    display: 'flex',
    gap: '0.25rem',
  },
  iconButton: {
    padding: '0.5rem',
    backgroundColor: 'transparent',
    border: '1px solid #ddd',
    borderRadius: '4px',
    cursor: 'pointer',
    transition: 'background-color 0.2s',
  },
  smallIcon: {
    width: '16px',
    height: '16px',
  },
};

export default CategoriesPage;
