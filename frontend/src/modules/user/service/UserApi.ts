import api from '../../../shared/response/api/axios';
import AccessToken from '../model/AccessToken';
import DataResponse from '@/shared/response/model/DataResponse';
import ErrorResponse from '@/modules/error/model/Error';
import { AxiosError } from 'axios';
import LoginInput from '../model/LoginInput';
import { User } from '../model/User';
import store from '@/store';
import { Result } from 'ts-results';
import { AxiosResponse } from 'axios';

export const refreshAccessToken = async () => {
  return await Result.wrapAsync<
    AxiosResponse<DataResponse<AccessToken>, any>,
    AxiosError<ErrorResponse>
  >(() => api.get<DataResponse<AccessToken>>('/auth/refresh'));
};

api.interceptors.response.use(
  response => response,
  async (error: AxiosError<ErrorResponse>) => {
    const originalRequest = error.config;
    const errCode = error.response?.data.code;
    const errStatus = error.response?.status;
    if (originalRequest && errStatus === 401 && errCode === 'ACCOUNT_107') {
      await refreshAccessToken();
      const state = store.getState();
      const newToken = state.auth.accessToken;
      originalRequest.headers['Authorization'] = `Bearer ${newToken}`;
      return api(originalRequest);
    }
    return Promise.reject(error);
  }
);

export const login = async (user: LoginInput) => {
  return await Result.wrapAsync<
    AxiosResponse<DataResponse<AccessToken>, any>,
    AxiosError<ErrorResponse>
  >(() => api.post<DataResponse<AccessToken>>('auth/login', user));
};

export const getCurrentUser = async () => {
  return await Result.wrapAsync<AxiosResponse<DataResponse<User>, any>, AxiosError<ErrorResponse>>(
    () => api.get<DataResponse<User>>('users/me')
  );
};

export const logout = async () => {
  return await Result.wrapAsync<AxiosResponse<DataResponse<void>, any>, AxiosError<ErrorResponse>>(
    () => api.post<DataResponse<void>>('auth/logout')
  );
};

const UserService = {
  login,
  logout,
  getCurrentUser,
  refreshAccessToken,
};
export default UserService;
