import api from './api';

export const goalService = {
  getAll: (activeOnly = false) => api.get(`/goals?activeOnly=${activeOnly}`),
  getById: (id) => api.get(`/goals/${id}`),
  create: (data) => api.post('/goals', data),
  update: (id, data) => api.put(`/goals/${id}`, data),
  delete: (id) => api.delete(`/goals/${id}`),
  contribute: (data) => api.post('/goals/contribute', data),
  pause: (id) => api.patch(`/goals/${id}/pause`),
  resume: (id) => api.patch(`/goals/${id}/resume`),
  cancel: (id) => api.patch(`/goals/${id}/cancel`),
};
