import api from '../../../shared/response/api/axios';
import AccessToken from '../model/AccessToken';
import DataResponse from '@/shared/response/model/DataResponse';
import ErrorResponse from '@/modules/error/model/Error';
import { AxiosError } from 'axios';
import LoginInput from '../model/LoginInput';
import { User } from '../model/User';
import store from '@/store';
import { setAccessToken } from '../state/AuthSlice';

export const refreshAccessTokenFn = async () => {
  const response = await api.get<DataResponse<AccessToken>>('/auth/refresh');
  store.dispatch(setAccessToken(response.data.item));
  return response.data.item;
};

api.interceptors.response.use(
  response => response,
  async (error: AxiosError<ErrorResponse>) => {
    const originalRequest = error.config;
    const errCode = error.response?.data.code;
    const errStatus = error.response?.status;
    if (originalRequest && errStatus === 401 && errCode === 'ACCOUNT_107') {
      await refreshAccessTokenFn();
      return api(originalRequest);
    }
    return Promise.reject(error);
  }
);

export const login = async (user: LoginInput) => {
  const response = await api.post<DataResponse<AccessToken>>('auth/login', user);
  return response.data;
};

export const getCurrentUser = async () => {
  const response = await api.get<DataResponse<User>>('users/me');
  return response.data;
};

const UserService = {
  login,
  getCurrentUser,
};
export default UserService;
