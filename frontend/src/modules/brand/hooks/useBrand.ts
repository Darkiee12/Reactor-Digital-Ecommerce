import Brand from '@/modules/brand/model/brand';
import { QueryObserverOptions, useQuery } from '@tanstack/react-query';
import BrandService from '../service/brandApi';
import PageResponse from '@/shared/response/model/PageResponse';

const useBrands = ({
  page = 0,
  size = 20,
  options,
}: {
  page?: number;
  size?: number;
  options?: QueryObserverOptions<{
    items: Brand[];
    page: PageResponse<Brand>['page'];
  }>;
}) => {
  return useQuery({
    queryKey: ['brands', { page, size }],
    queryFn: () => BrandService.getAllBrands(page, size),
    gcTime: 10 * 60 * 1000,
    ...options,
  });
};

export default useBrands;
