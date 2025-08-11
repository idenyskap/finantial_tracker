import React, { createContext, useContext, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import api from '../services/api';

const LanguageContext = createContext();

export const useLanguage = () => {
  const context = useContext(LanguageContext);
  if (!context) {
    throw new Error('useLanguage must be used within a LanguageProvider');
  }
  return context;
};

export const LanguageProvider = ({ children }) => {
  const { i18n, t } = useTranslation();
  
  // Initialize with the current i18n language or fallback to 'en'
  const [currentLanguage, setCurrentLanguage] = useState(() => {
    const saved = localStorage.getItem('i18nextLng');
    if (saved && ['en', 'uk'].includes(saved)) return saved;
    return 'en';
  });

  // Simple save function since backend endpoint doesn't exist
  const saveLanguagePreference = {
    mutate: (language) => {
      console.log('Saving language preference locally:', language);
    },
    isPending: false
  };

  // Initialize language on mount
  useEffect(() => {
    if (currentLanguage !== i18n.language) {
      i18n.changeLanguage(currentLanguage);
    }
  }, []); // Only run once on mount

  const changeLanguage = async (language) => {
    if (language === currentLanguage) return;
    
    console.log('Changing language from', currentLanguage, 'to', language);
    
    try {
      // Update state first
      setCurrentLanguage(language);
      
      // Change i18n language
      await i18n.changeLanguage(language);
      
      // Save to localStorage for persistence
      localStorage.setItem('i18nextLng', language);
      
      // Log for debugging
      saveLanguagePreference.mutate(language);
      
      console.log('Language changed successfully to', language);
      
    } catch (error) {
      console.error('Failed to change language:', error);
      // Revert state if failed
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

  // Format date according to current language
  const formatDate = (date, options = {}) => {
    const locale = currentLanguage === 'uk' ? 'uk-UA' : 'en-US';
    return new Date(date).toLocaleDateString(locale, {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      ...options,
    });
  };

  // Format date and time according to current language
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

  // Format numbers according to current language
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
    t, // Translation function
    isChangingLanguage: saveLanguagePreference.isPending,
  };

  return (
    <LanguageContext.Provider value={value}>
      {children}
    </LanguageContext.Provider>
  );
};