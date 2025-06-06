import { useMutation } from '@tanstack/react-query';
import LoginInput from '@/modules/user/model/LoginInput';
import UserService from '@/modules/user/service/UserApi';
import { setAccessToken } from '../state/AuthSlice';
import ErrorResponse from '@/modules/error/model/Error';
import { setError } from '@/modules/error/state/ErrorSlice';
import store from '@/store';
import { setUser } from '@/modules/user/state/UserSlice';
import { useNavigate } from 'react-router-dom';
const useLogin = () => {
  const navigate = useNavigate();
  return useMutation({
    mutationFn: (prompt: LoginInput) => UserService.login(prompt),
    onSuccess: async res => {
      if (res.ok) {
        const accessToken = res.val.data.item;
        store.dispatch(setAccessToken(accessToken));
        const response = await UserService.getCurrentUser();
        store.dispatch(setUser(response.data.item));
        navigate('/profile');
      } else {
        store.dispatch(setError(res.val.response?.data as ErrorResponse));
      }
    },
    onError: (error: ErrorResponse) => {
      store.dispatch(setError(error));
    },
  });
};

export default useLogin;
