import { useMutation } from '@tanstack/react-query';
import UserService from '../service/UserApi';
import { Finder } from '../model/enums';
import UpdateUser from '../model/UpdateUser';
import store from '@/store';
import { setUser } from '../state/UserSlice';
import { User } from '@/modules/user/model/User';

const useUpdateUser = () =>
  useMutation({
    mutationFn: async (params: { finder: Finder; user: UpdateUser }) =>
      await UserService.updateUser(params.finder, params.user),
    onSuccess: (user: User) => {
      store.dispatch(setUser(user));
    },
  });

export default useUpdateUser;
