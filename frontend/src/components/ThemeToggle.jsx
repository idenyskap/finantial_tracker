import { useTheme } from '../hooks/useTheme';
import { SunIcon, MoonIcon } from '@heroicons/react/24/outline';

function ThemeToggle() {
  const { theme, toggleTheme } = useTheme();

  return (
    <button
      onClick={toggleTheme}
      style={styles.button}
      aria-label="Toggle theme"
      title={`Switch to ${theme === 'light' ? 'dark' : 'light'} mode`}
    >
      {theme === 'light' ? (
        <MoonIcon style={styles.icon} />
      ) : (
        <SunIcon style={styles.icon} />
      )}
    </button>
  );
}

const styles = {
  button: {
    padding: '0.5rem',
    backgroundColor: 'transparent',
    border: '1px solid currentColor',
    borderRadius: '4px',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    transition: 'all 0.2s ease',
    color: 'inherit',
  },
  icon: {
    width: '20px',
    height: '20px',
  },
};

export default ThemeToggle;
