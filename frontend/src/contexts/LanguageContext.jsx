import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { LanguageContext } from './contexts';

export const LanguageProvider = ({ children }) => {
  const { i18n, t } = useTranslation();

  const [currentLanguage, setCurrentLanguage] = useState(() => {
    const saved = localStorage.getItem('i18nextLng');
    if (saved && ['en', 'uk'].includes(saved)) return saved;
    return 'en';
  });

  useEffect(() => {
    if (currentLanguage !== i18n.language) {
      i18n.changeLanguage(currentLanguage);
    }
  }, [currentLanguage, i18n]);

  const changeLanguage = async (language) => {
    if (language === currentLanguage) return;

    try {
      setCurrentLanguage(language);
      await i18n.changeLanguage(language);
      localStorage.setItem('i18nextLng', language);
    } catch (error) {
      console.error('Failed to change language:', error);
      setCurrentLanguage(currentLanguage);
    }
  };

  const getAvailableLanguages = () => [
    { code: 'en', name: 'English', nativeName: 'English', flag: '🇺🇸' },
    { code: 'uk', name: 'Ukrainian', nativeName: 'Українська', flag: '🇺🇦' },
  ];

  const getCurrentLanguageInfo = () => {
    const languages = getAvailableLanguages();
    return languages.find(lang => lang.code === currentLanguage) || languages[0];
  };

  const formatDate = (date, options = {}) => {
    const locale = currentLanguage === 'uk' ? 'uk-UA' : 'en-US';
    return new Date(date).toLocaleDateString(locale, {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      ...options,
    });
  };

  const formatDateTime = (date, options = {}) => {
    const locale = currentLanguage === 'uk' ? 'uk-UA' : 'en-US';
    return new Date(date).toLocaleString(locale, {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      ...options,
    });
  };

  const formatNumber = (number, options = {}) => {
    const locale = currentLanguage === 'uk' ? 'uk-UA' : 'en-US';
    return new Intl.NumberFormat(locale, options).format(number);
  };

  const value = {
    currentLanguage,
    changeLanguage,
    getAvailableLanguages,
    getCurrentLanguageInfo,
    formatDate,
    formatDateTime,
    formatNumber,
    t,
    isChangingLanguage: false,
  };

  return (
    <LanguageContext.Provider value={value}>
      {children}
    </LanguageContext.Provider>
  );
};
