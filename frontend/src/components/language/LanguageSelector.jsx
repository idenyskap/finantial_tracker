import React, { useState } from 'react';
import { useLanguage } from '../../hooks/useLanguage';
import { useThemedStyles } from '../../hooks/useThemedStyles';
import { Globe, ChevronDown, Check } from 'lucide-react';

const LanguageSelector = ({ compact = false }) => {
  const styles = useThemedStyles(getStyles);
  const { currentLanguage, changeLanguage, getAvailableLanguages, getCurrentLanguageInfo, isChangingLanguage } = useLanguage();
  const [isOpen, setIsOpen] = useState(false);

  const languages = getAvailableLanguages();
  const currentLangInfo = getCurrentLanguageInfo();

  const handleLanguageChange = async (languageCode) => {
    if (languageCode !== currentLanguage) {
      await changeLanguage(languageCode);
    }
    setIsOpen(false);
  };

  if (compact) {
    return (
      <div style={styles.compactContainer}>
        <button
          onClick={() => setIsOpen(!isOpen)}
          style={{
            ...styles.compactTrigger,
            opacity: isChangingLanguage ? 0.6 : 1
          }}
          disabled={isChangingLanguage}
        >
          <Globe style={styles.compactIcon} />
          <span style={styles.compactFlag}>{currentLangInfo.flag}</span>
          <ChevronDown style={{
            ...styles.chevron,
            transform: isOpen ? 'rotate(180deg)' : 'rotate(0deg)'
          }} />
        </button>

        {isOpen && (
          <div style={styles.compactDropdown}>
            {languages.map((language) => (
              <button
                key={language.code}
                onClick={() => handleLanguageChange(language.code)}
                style={{
                  ...styles.compactOption,
                  backgroundColor: language.code === currentLanguage ? styles.compactOptionActive.backgroundColor : 'transparent'
                }}
              >
                <span style={styles.flag}>{language.flag}</span>
                <span style={styles.languageCode}>{language.code.toUpperCase()}</span>
                {language.code === currentLanguage && (
                  <Check style={styles.checkIcon} />
                )}
              </button>
            ))}
          </div>
        )}
      </div>
    );
  }

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <Globe style={styles.icon} />
        <h3 style={styles.title}>Language / Мова</h3>
      </div>
      
      <div style={styles.languageGrid}>
        {languages.map((language) => (
          <button
            key={language.code}
            onClick={() => handleLanguageChange(language.code)}
            style={{
              ...styles.languageCard,
              ...(language.code === currentLanguage ? styles.languageCardActive : {}),
              opacity: isChangingLanguage ? 0.6 : 1
            }}
            disabled={isChangingLanguage}
          >
            <span style={styles.flag}>{language.flag}</span>
            <div style={styles.languageInfo}>
              <span style={styles.languageName}>{language.name}</span>
              <span style={styles.nativeName}>{language.nativeName}</span>
            </div>
            {language.code === currentLanguage && (
              <Check style={styles.checkIcon} />
            )}
          </button>
        ))}
      </div>
      
      {isChangingLanguage && (
        <div style={styles.loadingText}>
          Changing language...
        </div>
      )}
    </div>
  );
};

const getStyles = (theme) => ({
  container: {
    display: 'flex',
    flexDirection: 'column',
    gap: '1rem',
  },
  header: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.75rem',
  },
  icon: {
    width: '1.5rem',
    height: '1.5rem',
    color: theme.primary,
  },
  title: {
    margin: 0,
    fontSize: '1.125rem',
    fontWeight: '600',
    color: theme.text,
  },
  languageGrid: {
    display: 'flex',
    flexDirection: 'column',
    gap: '0.5rem',
  },
  languageCard: {
    display: 'flex',
    alignItems: 'center',
    gap: '1rem',
    padding: '1rem',
    backgroundColor: theme.cardBackground,
    border: `2px solid ${theme.cardBorder}`,
    borderRadius: '8px',
    cursor: 'pointer',
    transition: 'all 0.2s ease',
    textAlign: 'left',
  },
  languageCardActive: {
    borderColor: theme.primary,
    backgroundColor: theme.primaryLight || theme.backgroundSecondary,
  },
  flag: {
    fontSize: '1.5rem',
    flexShrink: 0,
  },
  languageInfo: {
    display: 'flex',
    flexDirection: 'column',
    gap: '0.25rem',
    flex: 1,
  },
  languageName: {
    fontSize: '1rem',
    fontWeight: '600',
    color: theme.text,
  },
  nativeName: {
    fontSize: '0.875rem',
    color: theme.textSecondary,
  },
  checkIcon: {
    width: '1.25rem',
    height: '1.25rem',
    color: theme.primary,
    flexShrink: 0,
  },
  loadingText: {
    textAlign: 'center',
    fontSize: '0.875rem',
    color: theme.textSecondary,
    fontStyle: 'italic',
  },
  
  // Compact styles
  compactContainer: {
    position: 'relative',
  },
  compactTrigger: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
    padding: '0.5rem',
    backgroundColor: theme.cardBackground,
    border: `1px solid ${theme.cardBorder}`,
    borderRadius: '6px',
    cursor: 'pointer',
    transition: 'all 0.2s ease',
  },
  compactIcon: {
    width: '1rem',
    height: '1rem',
    color: theme.textSecondary,
  },
  compactFlag: {
    fontSize: '1rem',
  },
  chevron: {
    width: '0.875rem',
    height: '0.875rem',
    color: theme.textSecondary,
    transition: 'transform 0.2s ease',
  },
  compactDropdown: {
    position: 'absolute',
    top: '100%',
    right: 0,
    marginTop: '0.5rem',
    backgroundColor: theme.cardBackground,
    border: `1px solid ${theme.cardBorder}`,
    borderRadius: '6px',
    boxShadow: theme.shadow,
    zIndex: 50,
    minWidth: '120px',
  },
  compactOption: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
    padding: '0.75rem',
    width: '100%',
    border: 'none',
    backgroundColor: 'transparent',
    cursor: 'pointer',
    transition: 'all 0.2s ease',
    fontSize: '0.875rem',
  },
  compactOptionActive: {
    backgroundColor: theme.backgroundSecondary,
  },
  languageCode: {
    fontSize: '0.75rem',
    fontWeight: '600',
    color: theme.text,
    flex: 1,
  },
});

export default LanguageSelector;