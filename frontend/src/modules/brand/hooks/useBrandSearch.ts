import { useQuery, UseQueryOptions } from '@tanstack/react-query';

import BrandService from '../service/brandApi';
import PageResponse from '@/shared/response/model/PageResponse';
import Brand from '../model/brand';

const useBrandSearch = (
  searchTerm: string,
  page: number = 0,
  options?: UseQueryOptions<{
    items: Brand[];
    page: PageResponse<Brand>['page'];
  }>
) => {
  return useQuery({
    queryKey: ['brands', 'search', searchTerm, page],
    queryFn: () => BrandService.search(searchTerm, page),
    enabled: !!searchTerm,
    placeholderData: (previousData, _previousQuery) => previousData ?? undefined,
    ...options,
  });
};

export default useBrandSearch;
