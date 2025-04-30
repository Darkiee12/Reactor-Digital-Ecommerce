import { useQuery, UseQueryOptions } from '@tanstack/react-query';
import UserService from '../service/UserApi';
import DataResponse from '@/shared/response/model/DataResponse';
import { User } from '../model/User';

export const useCurrentUser = (options?: UseQueryOptions<DataResponse<User>>) =>
  useQuery({
    queryKey: ['currentUser'],
    queryFn: () => UserService.getCurrentUser(),
    staleTime: 1000 * 60 * 5, // 5 minutes
    enabled: true,
    ...options,
  });
