function CategoryCard({ category, onEdit, onDelete }) {
  const typeColors = {
    INCOME: '#27ae60',
    EXPENSE: '#e74c3c',
    BOTH: '#3498db',
  };

  return (
    <div style={styles.card}>
      <div style={styles.header}>
        <div style={styles.nameSection}>
          <div
            style={{
              ...styles.colorDot,
              backgroundColor: category.color,
            }}
          />
          <h3 style={styles.name}>{category.name}</h3>
        </div>
        <div style={styles.actions}>
          <button onClick={() => onEdit(category)} style={styles.editButton}>
            Edit
          </button>
          <button onClick={() => onDelete(category.id)} style={styles.deleteButton}>
            Delete
          </button>
        </div>
      </div>

      {category.type && (
        <span style={{
          ...styles.typeBadge,
          backgroundColor: typeColors[category.type] || '#95a5a6',
        }}>
          {category.type}
        </span>
      )}
    </div>
  );
}

const styles = {
  card: {
    backgroundColor: 'white',
    padding: '1rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  nameSection: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.75rem',
  },
  colorDot: {
    width: '24px',
    height: '24px',
    borderRadius: '50%',
    flexShrink: 0,
  },
  name: {
    margin: 0,
    fontSize: '1.125rem',
    fontWeight: '600',
    color: '#2c3e50',
  },
  actions: {
    display: 'flex',
    gap: '0.5rem',
  },
  editButton: {
    padding: '0.5rem 1rem',
    backgroundColor: '#3498db',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.875rem',
    fontWeight: '500',
  },
  deleteButton: {
    padding: '0.5rem 1rem',
    backgroundColor: '#e74c3c',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.875rem',
    fontWeight: '500',
  },
  typeBadge: {
    display: 'inline-block',
    padding: '0.25rem 0.75rem',
    borderRadius: '4px',
    color: 'white',
    fontSize: '0.75rem',
    fontWeight: 'bold',
    marginTop: '0.5rem',
  },
};

export default CategoryCard;
