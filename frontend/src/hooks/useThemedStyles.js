import { useTheme } from '../contexts/ThemeContext';
import { getTheme } from '../styles/theme';

export const useThemedStyles = (stylesFn) => {
  const { theme } = useTheme();
  const colors = getTheme(theme);
  return stylesFn(colors);
};
