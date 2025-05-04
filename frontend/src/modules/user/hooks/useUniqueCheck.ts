import MessageResponse from '@/shared/response/model/MessageResponse';
import { QueryFunctionContext, useQuery, UseQueryOptions } from '@tanstack/react-query';
import { Finder } from '@/modules/user/model/enums';
import UserService from '@/modules/user//service/UserApi';
import { AxiosError, AxiosResponse } from 'axios';
import ErrorResponse from '@/modules/error/model/Error';

const checkExist = (response: MessageResponse): boolean =>
  !response.message.includes('does not exist');

const useUniqueCheck = (
  field: Finder,
  options?: UseQueryOptions<boolean, AxiosError<ErrorResponse>, boolean, ['uniqueCheck', Finder]>
) =>
  useQuery<boolean, AxiosError<ErrorResponse>, boolean, ['uniqueCheck', Finder]>({
    queryKey: ['uniqueCheck', field],
    queryFn: async ({ queryKey }: QueryFunctionContext<['uniqueCheck', Finder]>) => {
      const [, field] = queryKey;

      const response = await UserService.checkUserExists(field);

      if (response.ok) {
        return checkExist(response.val.data);
      } else {
        throw response.val;
      }
    },
    enabled: options?.enabled ?? !!field.value,
  });

export default useUniqueCheck;
