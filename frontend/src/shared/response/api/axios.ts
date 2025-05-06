import useRefreshToken from '@/modules/user/hooks/useRefreshToken';
import store from '@/store';
import axios from 'axios';

const baseURL = import.meta.env.VITE_API;

const api = axios.create({
  baseURL: `${baseURL}/api/v1`,
  withCredentials: true,
});

api.defaults.headers.common['Content-Type'] = 'application/json';
api.interceptors.request.use(config => {
  const token = store.getState().auth.accessToken;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
