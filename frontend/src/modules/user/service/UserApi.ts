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
import MessageResponse from '@/shared/response/model/MessageResponse';
import { Finder } from '../model/enums';
import UpdateUser from '../model/UpdateUser';

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

const refreshAccessToken = async () => {
  return await Result.wrapAsync<
    AxiosResponse<DataResponse<AccessToken>, any>,
    AxiosError<ErrorResponse>
  >(() => api.get<DataResponse<AccessToken>>('/auth/refresh'));
};

const login = async (user: LoginInput) => {
  return await Result.wrapAsync<
    AxiosResponse<DataResponse<AccessToken>, any>,
    AxiosError<ErrorResponse>
  >(async () => await api.post<DataResponse<AccessToken>>('auth/login', user));
};

const getCurrentUser = async () => {
  return await Result.wrapAsync<AxiosResponse<DataResponse<User>, any>, AxiosError<ErrorResponse>>(
    async () => await api.get<DataResponse<User>>('users/me')
  );
};

const logout = async () => {
  return await Result.wrapAsync<AxiosResponse<DataResponse<void>, any>, AxiosError<ErrorResponse>>(
    async () => await api.post<DataResponse<void>>('auth/logout')
  );
};

const checkUserExists = async (finder: Finder) => {
  let endpoint: string;
  switch (finder.type) {
    case 'username':
      endpoint = `auth/exists/username/${finder.value}`;
      break;
    case 'email':
      endpoint = `auth/exists/email/${finder.value}`;
      break;
    case 'uuid':
      endpoint = `auth/exists/uuid/${finder.value}`;
      break;
  }

  return await Result.wrapAsync<AxiosResponse<MessageResponse, any>, AxiosError<ErrorResponse>>(
    async () => await api.get<MessageResponse>(endpoint)
  );
};

const updateUser = async (finder: Finder, user: UpdateUser) => {
  let endpoint: string;
  switch (finder.type) {
    case 'username':
      endpoint = `users/username/${finder.value}`;
      break;
    case 'email':
      endpoint = `users/email/${finder.value}`;
      break;
    case 'uuid':
      endpoint = `users/uuid/${finder.value}`;
      break;
    default:
      throw new Error(`Invalid finder type: ${finder.type}`);
  }

  return Result.wrapAsync<AxiosResponse<DataResponse<User>>, AxiosError<ErrorResponse>>(() =>
    api.patch<DataResponse<User>>(endpoint, user)
  );
};

const UserService = {
  login,
  logout,
  getCurrentUser,
  refreshAccessToken,
  checkUserExists,
  updateUser,
};
export default UserService;
