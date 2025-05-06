import { useQuery, UseQueryOptions } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import Brand from '../model/brand';
import PageResponse from '@/shared/response/model/PageResponse';
import ErrorResponse from '@/modules/error/model/Error';
import BrandService from '../service/brandApi';

const useBrands = (
  searchTerm: string,
  page: number = 0,
  options?: UseQueryOptions<
    PageResponse<Brand>,
    AxiosError<ErrorResponse>,
    PageResponse<Brand>,
    ['brands', 'search', string, number]
  >
) => {
  return useQuery<
    PageResponse<Brand>,
    AxiosError<ErrorResponse>,
    PageResponse<Brand>,
    ['brands', 'search', string, number]
  >({
    queryKey: ['brands', 'search', searchTerm, page],
    queryFn: () => BrandService.search(searchTerm, page).then(result => result.data),
    enabled: !!searchTerm,
    placeholderData: (previousData, _previousQuery) => previousData ?? undefined,
    ...options,
  });
};

export default useBrands;
