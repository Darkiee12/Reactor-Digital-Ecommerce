import { QueryObserverOptions, useQuery } from '@tanstack/react-query';
import CategoryService from '@/modules/category/service/categoryApi';
import { Category } from '../model/Category';
import PageResponse from '@/shared/response/model/PageResponse';

export const useCategories = ({
  page = 0,
  size = 20,
  options,
}: {
  page?: number;
  size?: number;
  options?: QueryObserverOptions<{
    items: Category[];
    page: PageResponse<Category>['page'];
  }>;
}) => {
  return useQuery({
    queryKey: ['categories', page, size],
    queryFn: () => CategoryService.getCategories(page, size),
    staleTime: 5 * 60 * 1000,
    gcTime: 10 * 60 * 1000,
    retry: 1,
    ...options,
  });
};
