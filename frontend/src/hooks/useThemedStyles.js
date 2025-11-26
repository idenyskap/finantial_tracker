import { useTheme } from './useTheme';
import { getTheme } from '../styles/theme';

export const useThemedStyles = (stylesFn) => {
  const { theme } = useTheme();
  const colors = getTheme(theme);
  return stylesFn(colors);
};
