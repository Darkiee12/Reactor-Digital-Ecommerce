import { useMutation } from '@tanstack/react-query';
import { useDispatch } from 'react-redux';
import LoginInput from '@/modules/user/model/LoginInput';
import UserService from '@/modules/user/service/UserApi';
import { setAccessToken } from '../state/AuthSlice';
import ErrorResponse from '@/modules/error/model/Error';
import { setError } from '@/modules/error/state/ErrorSlice';

export const useLogin = () => {
  const dispatch = useDispatch();
  return useMutation({
    mutationFn: (prompt: LoginInput) => UserService.login(prompt),
    onSuccess: res => {
      dispatch(setAccessToken(res.item));
    },
    onError: (error: ErrorResponse) => {
      dispatch(setError(error));
    },
  });
};
