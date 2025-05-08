import { useQuery, UseQueryOptions } from '@tanstack/react-query';
import ProductService from '@/modules/product/service/ProductApi';
import { Product } from '../model/Product';
import store from '@/store';
import { fetchProductSuccess } from '../state/productSlice';

const useProductByUuid = (uuid: string, options?: UseQueryOptions<Product>) => {
  return useQuery({
    queryKey: ['product', uuid],
    queryFn: async () => {
      const product = await ProductService.getProductByUuid(uuid);
      store.dispatch(fetchProductSuccess(product));
      return product;
    },

    enabled: !!uuid,
    ...options,
  });
};

export default useProductByUuid;
