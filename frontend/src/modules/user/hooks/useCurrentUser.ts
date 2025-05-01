import { useQuery, UseQueryOptions } from '@tanstack/react-query';
import UserService from '../service/UserApi';
import DataResponse from '@/shared/response/model/DataResponse';
import { User } from '../model/User';
import store from '@/store';
import { setUser } from '../state/UserSlice';

const useCurrentUser = (options?: UseQueryOptions<DataResponse<User>>) =>
  useQuery({
    queryKey: ['currentUser'],
    queryFn: async () => {
      const result = await UserService.getCurrentUser();
      if (result.ok) {
        const data = result.val.data;
        store.dispatch(setUser(data.item));
        return data;
      } else {
        throw result.err;
      }
    },
    staleTime: 1000 * 60 * 5,
    enabled: true,
    ...options,
  });

export default useCurrentUser;
