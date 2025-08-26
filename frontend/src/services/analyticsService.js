import api from './api';

export const analyticsService = {
  getFullAnalytics: (startDate, endDate) => {
    const params = new URLSearchParams();
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    return api.get(`/analytics/full?${params.toString()}`);
  },

  getMonthlyStats: (startDate, endDate) => {
    const params = new URLSearchParams();
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    return api.get(`/analytics/monthly?${params.toString()}`);
  },

  getCategoryMonthlyStats: (limit = 10, startDate, endDate) => {
    const params = new URLSearchParams();
    params.append('limit', limit);
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    return api.get(`/analytics/categories/monthly?${params.toString()}`);
  },

  getTopExpenseCategories: (limit = 10, startDate, endDate) => {
    const params = new URLSearchParams();
    params.append('limit', limit);
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    return api.get(`/analytics/categories/expenses?${params.toString()}`);
  },

  getTopIncomeCategories: (limit = 10, startDate, endDate) => {
    const params = new URLSearchParams();
    params.append('limit', limit);
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    return api.get(`/analytics/categories/income?${params.toString()}`);
  },

  getComparisonStats: () => {
    return api.get('/analytics/comparison');
  }
};