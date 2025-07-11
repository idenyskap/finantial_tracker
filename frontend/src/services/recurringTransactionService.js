import api from './api';

export const recurringTransactionService = {
  getAll: () => api.get('/recurring-transactions'),
  create: (data) => api.post('/recurring-transactions', data),
  update: (id, data) => api.put(`/recurring-transactions/${id}`, data),
  delete: (id) => api.delete(`/recurring-transactions/${id}`),
  executeNow: (id) => api.post(`/recurring-transactions/${id}/execute`),
};
