import axios from 'axios';

const api = axios.create({
  baseURL: '/as2/api/v1',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Response interceptor for handling authentication errors
api.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config;

    // If 401 and not already retrying, attempt token refresh
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        await axios.post('/as2/api/v1/auth/refresh', {}, {
          withCredentials: true
        });
        return api.request(originalRequest);
      } catch (refreshError) {
        // Refresh failed, redirect to login only if not already there
        if (!window.location.pathname.includes('/login')) {
          window.location.href = '/as2/admin/login';
        }
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;
