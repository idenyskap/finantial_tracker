import { BookmarkIcon, MagnifyingGlassIcon, TrashIcon } from '@heroicons/react/24/outline';
import { useThemedStyles } from '../../hooks/useThemedStyles';

function SavedSearchItem({ search, onExecute, onDelete }) {
  const styles = useThemedStyles(getStyles);
  const getFilterSummary = () => {
    const filters = [];
    const criteria = search.searchCriteria;

    if (criteria.searchText) filters.push(`"${criteria.searchText}"`);
    if (criteria.type) filters.push(criteria.type);
    if (criteria.quickDateFilter) filters.push(criteria.quickDateFilter.replace(/_/g, ' '));
    if (criteria.minAmount || criteria.maxAmount) {
      filters.push(`$${criteria.minAmount || 0} - $${criteria.maxAmount || '∞'}`);
    }

    return filters.join(' • ') || 'All transactions';
  };

  return (
    <div style={styles.container}>
      <div style={styles.content}>
        <BookmarkIcon style={styles.bookmarkIcon} />
        <div style={styles.info}>
          <h4 style={styles.name}>{search.name}</h4>
          <p style={styles.filters}>{getFilterSummary()}</p>
        </div>
      </div>
      <div style={styles.actions}>
        <button
          onClick={() => onExecute(search)}
          style={styles.executeButton}
          title="Run search"
        >
          <MagnifyingGlassIcon style={styles.actionIcon} />
        </button>
        <button
          onClick={() => onDelete(search.id)}
          style={styles.deleteButton}
          title="Delete"
        >
          <TrashIcon style={styles.actionIcon} />
        </button>
      </div>
    </div>
  );
}

const getStyles = (theme) => ({
  container: {
    backgroundColor: theme.cardBackground,
    padding: '1rem',
    borderRadius: '8px',
    boxShadow: theme.shadow,
    border: `1px solid ${theme.cardBorder}`,
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    transition: 'transform 0.2s',
    cursor: 'pointer',
    '&:hover': {
      transform: 'translateY(-2px)',
    },
  },
  content: {
    display: 'flex',
    alignItems: 'center',
    gap: '1rem',
    flex: 1,
  },
  bookmarkIcon: {
    width: '24px',
    height: '24px',
    color: theme.primary,
  },
  info: {
    flex: 1,
  },
  name: {
    margin: '0 0 0.25rem 0',
    fontSize: '1rem',
    fontWeight: '500',
    color: theme.text,
  },
  filters: {
    margin: 0,
    fontSize: '0.875rem',
    color: theme.textSecondary,
  },
  actions: {
    display: 'flex',
    gap: '0.5rem',
  },
  executeButton: {
    padding: '0.5rem',
    backgroundColor: theme.primary,
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  deleteButton: {
    padding: '0.5rem',
    backgroundColor: theme.danger,
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  actionIcon: {
    width: '16px',
    height: '16px',
  },
});

export default SavedSearchItem;
