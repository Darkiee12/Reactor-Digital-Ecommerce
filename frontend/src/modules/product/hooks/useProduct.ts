import { useQuery } from '@tanstack/react-query';
import ProductService from '@/modules/product/service/ProductApi';
const useProductByUuid = (uuid: string) => {
  return useQuery({
    queryKey: ['product', uuid],
    queryFn: () => ProductService.getProductByUuid(uuid),
    enabled: !!uuid, // only run if uuid is truthy
  });
};

export default useProductByUuid;
