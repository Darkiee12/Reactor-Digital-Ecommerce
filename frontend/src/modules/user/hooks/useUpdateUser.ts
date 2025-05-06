import { useMutation, useQueryClient } from '@tanstack/react-query';
import UserService from '../service/UserApi';
import { Finder } from '../model/enums';
import UpdateUser from '../model/UpdateUser';
import store from '@/store';
import { setUser } from '../state/UserSlice';

const useUpdateUser = () =>
  useMutation({
    mutationFn: async (params: { finder: Finder; user: UpdateUser }) => {
      const result = await UserService.updateUser(params.finder, params.user);
      if (result.ok) {
        const user = result.val.data.item;
        store.dispatch(setUser(user));
        return result;
      } else {
        throw result.err;
      }
    },
  });

export default useUpdateUser;
