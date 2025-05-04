import { useMutation } from '@tanstack/react-query';
import UserService from '../service/UserApi';
import { setError } from '@/modules/error/state/ErrorSlice';
import ErrorResponse from '@/modules/error/model/Error';
import { setAccessToken } from '../state/AuthSlice';
import { setUser } from '../state/UserSlice';
import store from '@/store';
const useRefreshToken = () =>
  useMutation({
    mutationFn: () => UserService.refreshAccessToken(),
    onSuccess: async res => {
      if (res.ok) {
        const accessToken = res.val.data.item;
        store.dispatch(setAccessToken(accessToken));
        const response = await UserService.getCurrentUser();
        if (response.ok) {
          const user = response.val.data.item;
          store.dispatch(setUser(user));
        } else {
          store.dispatch(setError(response.val.response?.data as ErrorResponse));
          return;
        }
      } else {
        setError(res.val.response?.data as ErrorResponse);
      }
    },
  });

export default useRefreshToken;
