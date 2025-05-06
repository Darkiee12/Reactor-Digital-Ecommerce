import { useQuery, UseQueryOptions } from '@tanstack/react-query';
import ProductService from '@/modules/product/service/ProductApi';
import DataResponse from '@/shared/response/model/DataResponse';
import { Product } from '../model/Product';
import { AxiosError } from 'axios';
import ErrorResponse from '@/modules/error/model/Error';
const useProductByUuid = (
  uuid: string,
  options?: UseQueryOptions<DataResponse<Product>, AxiosError<ErrorResponse>>
) => {
  return useQuery({
    queryKey: ['product', uuid],
    queryFn: () => ProductService.getProductByUuid(uuid).then(result => result.data),
    enabled: !!uuid,
    ...options,
  });
};

export default useProductByUuid;
