import api from './api';

export const authService = {
  login: (credentials) => api.post('/auth/login', credentials),

  register: (userData) => api.post('/auth/register', userData),

  logout: () => api.post('/auth/logout'),

  getProfile: () => api.get('/users/me'),

  requestPasswordReset: (email) => api.post('/auth/request-password-reset', { email }),

  resetPassword: (token, newPassword) => api.post('/auth/reset-password', { token, newPassword }),

  verifyEmail: (token) => api.post('/auth/verify-email', { token }),

  resendVerification: (email) => api.post('/auth/resend-verification', { email }),
};
