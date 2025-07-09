import { useState } from 'react';
import { MagnifyingGlassIcon, FunnelIcon } from '@heroicons/react/24/outline';

function TransactionSearch({ onSearch, categories }) {
  const [showFilters, setShowFilters] = useState(false);
  const [filters, setFilters] = useState({
    searchText: '',
    type: '',
    categoryId: '',
    quickDateFilter: 'LAST_30_DAYS',
    minAmount: '',
    maxAmount: '',
  });

  const quickFilters = [
    { value: 'TODAY', label: 'Today' },
    { value: 'LAST_7_DAYS', label: 'Last 7 Days' },
    { value: 'LAST_30_DAYS', label: 'Last 30 Days' },
    { value: 'THIS_MONTH', label: 'This Month' },
    { value: 'LAST_MONTH', label: 'Last Month' },
    { value: 'THIS_YEAR', label: 'This Year' },
  ];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({ ...prev, [name]: value }));
  };

  const handleSearch = (e) => {
    e.preventDefault();
    onSearch(filters);
  };

  const handleReset = () => {
    setFilters({
      searchText: '',
      type: '',
      categoryId: '',
      quickDateFilter: 'LAST_30_DAYS',
      minAmount: '',
      maxAmount: '',
    });
    onSearch({});
  };

  return (
    <div style={styles.container}>
      <form onSubmit={handleSearch} style={styles.searchForm}>
        <div style={styles.searchBar}>
          <input
            type="text"
            name="searchText"
            placeholder="Search transactions..."
            value={filters.searchText}
            onChange={handleChange}
            style={styles.searchInput}
          />
          <button type="submit" style={styles.searchButton}>
            <MagnifyingGlassIcon style={styles.icon} />
          </button>
          <button
            type="button"
            onClick={() => setShowFilters(!showFilters)}
            style={styles.filterButton}
          >
            <FunnelIcon style={styles.icon} />
            Filters
          </button>
        </div>

        {showFilters && (
          <div style={styles.filtersPanel}>
            <div style={styles.filterRow}>
              <select
                name="quickDateFilter"
                value={filters.quickDateFilter}
                onChange={handleChange}
                style={styles.select}
              >
                {quickFilters.map(filter => (
                  <option key={filter.value} value={filter.value}>
                    {filter.label}
                  </option>
                ))}
              </select>

              <select
                name="type"
                value={filters.type}
                onChange={handleChange}
                style={styles.select}
              >
                <option value="">All Types</option>
                <option value="INCOME">Income</option>
                <option value="EXPENSE">Expense</option>
              </select>

              <select
                name="categoryId"
                value={filters.categoryId}
                onChange={handleChange}
                style={styles.select}
              >
                <option value="">All Categories</option>
                {categories?.map(cat => (
                  <option key={cat.id} value={cat.id}>
                    {cat.name}
                  </option>
                ))}
              </select>
            </div>

            <div style={styles.filterRow}>
              <input
                type="number"
                name="minAmount"
                placeholder="Min amount"
                value={filters.minAmount}
                onChange={handleChange}
                style={styles.input}
              />
              <input
                type="number"
                name="maxAmount"
                placeholder="Max amount"
                value={filters.maxAmount}
                onChange={handleChange}
                style={styles.input}
              />
              <button
                type="button"
                onClick={handleReset}
                style={styles.resetButton}
              >
                Reset
              </button>
            </div>
          </div>
        )}
      </form>
    </div>
  );
}

const styles = {
  container: {
    marginBottom: '2rem',
  },
  searchForm: {
    backgroundColor: 'white',
    padding: '1rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  searchBar: {
    display: 'flex',
    gap: '0.5rem',
  },
  searchInput: {
    flex: 1,
    padding: '0.75rem',
    border: '1px solid #ddd',
    borderRadius: '4px',
    fontSize: '1rem',
  },
  searchButton: {
    padding: '0.75rem 1rem',
    backgroundColor: '#3498db',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
  },
  filterButton: {
    padding: '0.75rem 1rem',
    backgroundColor: '#95a5a6',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
  },
  icon: {
    width: '20px',
    height: '20px',
  },
  filtersPanel: {
    marginTop: '1rem',
    paddingTop: '1rem',
    borderTop: '1px solid #eee',
  },
  filterRow: {
    display: 'flex',
    gap: '0.5rem',
    marginBottom: '0.5rem',
  },
  select: {
    flex: 1,
    padding: '0.5rem',
    border: '1px solid #ddd',
    borderRadius: '4px',
  },
  input: {
    flex: 1,
    padding: '0.5rem',
    border: '1px solid #ddd',
    borderRadius: '4px',
  },
  resetButton: {
    padding: '0.5rem 1rem',
    backgroundColor: '#e74c3c',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
  },
};

export default TransactionSearch;
