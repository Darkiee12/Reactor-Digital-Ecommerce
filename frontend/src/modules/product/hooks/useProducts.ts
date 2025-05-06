import PageResponse from '@/shared/response/model/PageResponse';
import { Product } from '../model/Product';
import { useQuery, UseQueryOptions } from '@tanstack/react-query';
import { ProductsFinder } from '../model/enums';
import { AxiosError } from 'axios';
import ErrorResponse from '@/modules/error/model/Error';
import ProductService from '../service/ProductApi';

const useProducts = (
  finder: ProductsFinder,
  options?: UseQueryOptions<PageResponse<Product>, AxiosError<ErrorResponse>>
) => {
  return useQuery<PageResponse<Product>, AxiosError<ErrorResponse>>({
    queryKey: ['products', finder.type, finder.value],
    queryFn: async () => {
      const result = await ProductService.getProducts(finder);
      if (result.status !== 200) {
        throw new Error('Error fetching products');
      }
      return result.data;
    },
    ...options,
  });
};

export default useProducts;
