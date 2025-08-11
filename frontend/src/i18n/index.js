import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';

// Import translation files directly
import enTranslations from '../locales/en/translation.json';
import ukTranslations from '../locales/uk/translation.json';

i18n
  .use(initReactI18next)
  .init({
    lng: 'en', // Set default language explicitly
    fallbackLng: 'en',
    debug: true, // Enable for development

    interpolation: {
      escapeValue: false, // React already does escaping
    },

    resources: {
      en: {
        translation: enTranslations
      },
      uk: {
        translation: ukTranslations
      }
    },

    react: {
      useSuspense: false,
    },
  });

export default i18n;