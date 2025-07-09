function StatsCard({ title, value, subtitle, color = '#3498db', icon }) {
  return (
    <div style={styles.card}>
      <div style={styles.header}>
        <h3 style={styles.title}>{title}</h3>
        {icon && <div style={{ ...styles.icon, backgroundColor: color }}>{icon}</div>}
      </div>
      <p style={{ ...styles.value, color }}>{value}</p>
      {subtitle && <p style={styles.subtitle}>{subtitle}</p>}
    </div>
  );
}

const styles = {
  card: {
    backgroundColor: 'white',
    padding: '1.5rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    flex: 1,
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '1rem',
  },
  title: {
    fontSize: '0.875rem',
    color: '#666',
    fontWeight: 'normal',
    margin: 0,
  },
  value: {
    fontSize: '2rem',
    fontWeight: 'bold',
    margin: '0.5rem 0',
  },
  subtitle: {
    fontSize: '0.875rem',
    color: '#888',
    margin: 0,
  },
  icon: {
    width: '40px',
    height: '40px',
    borderRadius: '8px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    color: 'white',
  },
};

export default StatsCard;
