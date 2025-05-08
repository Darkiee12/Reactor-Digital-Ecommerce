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
  size: number = 20,
  options?: UseQueryOptions<{
    items: Product[];
    page: PageResponse<Product>['page'];
  }>
) => {
  return useQuery({
    queryKey: ['products', finder.type, finder.value, page, size],
    queryFn: () => ProductService.getProducts(finder, page, size),
    enabled: !!finder.value,
    ...options,
  });
};

export default useProducts;
