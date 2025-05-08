import { useMutation } from '@tanstack/react-query';
import UserService from '@/modules/user/service/UserApi';
import ErrorResponse from '@/modules/error/model/Error';
import { setAccessToken } from '@/modules/user/state/AuthSlice';
import { setUser } from '@/modules/user/state/UserSlice';
import store from '@/store';
import { AxiosError } from 'axios';
const useRefreshToken = () =>
  useMutation({
    mutationFn: async () => {
      const accessToken = await UserService.refreshAccessToken();
      store.dispatch(setAccessToken(accessToken));

      const user = await UserService.getCurrentUser();
      store.dispatch(setUser(user));

      return accessToken;
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      console.error('Error refreshing token:', error.response?.data);
    },
  });

export default useRefreshToken;
