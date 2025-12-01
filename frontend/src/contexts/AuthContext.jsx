import { useState, useEffect } from 'react';
import { authService } from '../services/authService';
import { AuthContext } from './contexts';

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [requires2FA, setRequires2FA] = useState(false);

  useEffect(() => {
    loadUser();
  }, []);

  const loadUser = async () => {
    try {
      const response = await authService.getProfile();
      setUser(response.data);
    } catch (error) {
      setUser(null);
    } finally {
      setLoading(false);
    }
  };

  const login = async (credentials) => {
    try {
      const response = await authService.login(credentials);

      if (response.data.requiresTwoFactor) {
        setRequires2FA(true);
        return {
          success: false,
          requires2FA: true,
          tempCredentials: credentials
        };
      }

      await loadUser();

      return { success: true };
    } catch (error) {
      const data = error.response?.data;
      let errorMessage = 'Login failed';

      if (data?.fieldErrors) {
        const firstError = Object.values(data.fieldErrors)[0];
        errorMessage = firstError || data.message;
      } else if (data?.message) {
        errorMessage = data.message;
      }

      return {
        success: false,
        error: errorMessage
      };
    }
  };

  const register = async (userData) => {
    try {
      await authService.register(userData);
      await loadUser();
      return { success: true };
    } catch (error) {
      const data = error.response?.data;
      let errorMessage = 'Registration failed';

      if (data?.fieldErrors) {
        const firstError = Object.values(data.fieldErrors)[0];
        errorMessage = firstError || data.message;
      } else if (data?.message) {
        errorMessage = data.message;
      }

      return {
        success: false,
        error: errorMessage
      };
    }
  };

  const logout = async () => {
    try {
      await authService.logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      setUser(null);
      setRequires2FA(false);
    }
  };

  const value = {
    user,
    login,
    register,
    logout,
    loading,
    isAuthenticated: !!user,
    requires2FA,
    setRequires2FA,
    loadUser
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
