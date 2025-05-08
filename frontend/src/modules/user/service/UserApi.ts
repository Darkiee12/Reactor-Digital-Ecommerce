import API from '@/shared/response/api/axios';
import AccessToken from '@/modules/user/model/AccessToken';
import LoginInput from '@/modules/user/model/LoginInput';
import { User } from '@/modules/user/model/User';
import { Finder } from '@/modules/user/model/enums';
import UpdateUser from '@/modules/user/model/UpdateUser';

const api = API.getClient();

const refreshAccessToken = async () => {
  return await api.getData<AccessToken>('/auth/refresh');
};

const login = async (user: LoginInput) => {
  return await api.postData<AccessToken>('auth/login', user);
};

const getCurrentUser = async () => {
  return await api.getData<User>('users/me');
};

const logout = async () => {
  return await api.postMessage('auth/logout');
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
      endpoint = `users/${finder.value}`;
      break;
    default:
      throw new Error(`Invalid finder type: ${finder.type}`);
  }

  return await api.patchData<User>(endpoint, user);
};

const UserService = {
  login,
  logout,
  getCurrentUser,
  refreshAccessToken,
  updateUser,
};
export default UserService;
