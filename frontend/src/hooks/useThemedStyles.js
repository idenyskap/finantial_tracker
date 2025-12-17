import { useTheme } from './useTheme';
import { getTheme } from '../styles/theme';
import { useIsMobile, useIsTablet } from './useMediaQuery';

export const useThemedStyles = (stylesFn) => {
  const { theme } = useTheme();
  const colors = getTheme(theme);
  const isMobile = useIsMobile();
  const isTablet = useIsTablet();
  return stylesFn(colors, { isMobile, isTablet });
};
