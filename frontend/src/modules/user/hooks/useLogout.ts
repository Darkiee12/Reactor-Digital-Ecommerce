import { useMutation, useQuery } from '@tanstack/react-query';
import UserService from '@/modules/user/service/UserApi';
import { clearAccessToken } from '../state/AuthSlice';
import { clearUser } from '../state/UserSlice';
import store from '@/store';
import { setError } from '@/modules/error/state/ErrorSlice';
import ErrorResponse from '@/modules/error/model/Error';
const useLogout = () =>
  useMutation({
    mutationFn: () => UserService.logout(),
    onSuccess: () => {
      store.dispatch(clearUser());
      store.dispatch(clearAccessToken());
    },
    onError: (error: ErrorResponse) => {
      store.dispatch(setError(error));
    },
  });

export default useLogout;
