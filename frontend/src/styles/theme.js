export const lightTheme = {
  background: '#ffffff',
  backgroundSecondary: '#f8f9fa',
  backgroundTertiary: '#e9ecef',

  text: '#2c3e50',
  textSecondary: '#666666',
  textTertiary: '#888888',

  border: '#dee2e6',
  borderLight: '#e9ecef',

  cardBackground: '#ffffff',
  cardBorder: '#dee2e6',

  inputBackground: '#ffffff',
  inputBorder: '#ced4da',
  inputText: '#495057',

  primary: '#3498db',
  success: '#27ae60',
  danger: '#e74c3c',
  warning: '#f39c12',
  info: '#3498db',

  shadow: '0 2px 4px rgba(0,0,0,0.1)',
  shadowLarge: '0 4px 6px rgba(0,0,0,0.1)',
};

export const darkTheme = {
  background: '#1a1a1a',
  backgroundSecondary: '#2d2d2d',
  backgroundTertiary: '#3a3a3a',

  text: '#e0e0e0',
  textSecondary: '#a0a0a0',
  textTertiary: '#808080',

  border: '#404040',
  borderLight: '#333333',

  cardBackground: '#2d2d2d',
  cardBorder: '#404040',

  inputBackground: '#333333',
  inputBorder: '#404040',
  inputText: '#e0e0e0',

  primary: '#3498db',
  success: '#27ae60',
  danger: '#e74c3c',
  warning: '#f39c12',
  info: '#3498db',

  shadow: '0 2px 4px rgba(0,0,0,0.3)',
  shadowLarge: '0 4px 6px rgba(0,0,0,0.4)',
};

export const getTheme = (theme) => {
  return theme === 'dark' ? darkTheme : lightTheme;
};
