import { useQuery, UseQueryResult } from '@tanstack/react-query';
import ReviewService from '../service/rewiewApi';

const useAverageRating = (
  productId: string,
  options?: {
    staleTime?: number;
    cacheTime?: number;
  }
): UseQueryResult<number, Error> => {
  return useQuery<number, Error>(
    ['averageRating', productId],
    () => ReviewService.getAverageRatingByProductId(productId),
    {
      staleTime: options?.staleTime ?? 5 * 60 * 1000, // 5 minutes
      cacheTime: options?.cacheTime ?? 10 * 60 * 1000, // 10 minutes
      ...options,
    }
  );
};
