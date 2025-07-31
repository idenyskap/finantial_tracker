import api from './api';

export const savedSearchService = {
  getAll: () => api.get('/saved-searches'),
  getById: (id) => api.get(`/saved-searches/${id}`),
  create: (data) => api.post('/saved-searches', data),
  delete: (id) => api.delete(`/saved-searches/${id}`),
  execute: (id, params) => api.get(`/transactions/search/saved/${id}`, { params }),
};
