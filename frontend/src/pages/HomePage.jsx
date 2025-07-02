import { useNavigate } from 'react-router-dom';

function HomePage() {
  const navigate = useNavigate();

  return (
    <div style={styles.container}>
      <h1 style={styles.title}>Welcome to Financial Tracker</h1>
      <p style={styles.subtitle}>
        A simple app to manage your expenses and income.
      </p>
      <div style={styles.buttonGroup}>
        <button onClick={() => navigate('/register')} style={styles.button}>
          Register
        </button>
        <button onClick={() => navigate('/login')} style={styles.button}>
          Login
        </button>
      </div>
    </div>
  );
}

const styles = {
  container: {
    textAlign: 'center',
    paddingTop: '100px',
    fontFamily: 'Arial, sans-serif',
    backgroundColor: '#f5f6fa',
    minHeight: '100vh',
  },
  title: {
    fontSize: '2.5rem',
    color: '#2f3640',
  },
  subtitle: {
    fontSize: '1.2rem',
    color: '#718093',
    marginBottom: '30px',
  },
  buttonGroup: {
    display: 'flex',
    justifyContent: 'center',
    gap: '20px',
  },
  button: {
    padding: '10px 20px',
    fontSize: '1rem',
    cursor: 'pointer',
    backgroundColor: '#44bd32',
    border: 'none',
    borderRadius: '5px',
    color: 'white',
    transition: 'background-color 0.2s ease-in-out',
  }
};

export default HomePage;
