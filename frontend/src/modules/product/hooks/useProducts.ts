import PageResponse from '@/shared/response/model/PageResponse';
import { Product } from '../model/Product';
import { useQuery, UseQueryOptions } from '@tanstack/react-query';
import { ProductsFinder } from '../model/enums';
import { AxiosError } from 'axios';
import ErrorResponse from '@/modules/error/model/Error';
import ProductService from '../service/ProductApi';

const useProducts = (
  finder: ProductsFinder,
  page: number = 0,
  options?: UseQueryOptions<PageResponse<Product>, AxiosError<ErrorResponse>>
) => {
  return useQuery<PageResponse<Product>, AxiosError<ErrorResponse>>({
    queryKey: ['products', finder.type, finder.value, page],
    queryFn: () => ProductService.getProducts(finder).then(result => result.data),
    enabled: !!finder.value,
    ...options,
  });
};

export default useProducts;
