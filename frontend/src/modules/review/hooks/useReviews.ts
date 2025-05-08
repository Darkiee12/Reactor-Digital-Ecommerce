import { useQuery } from '@tanstack/react-query';
import ReviewService from '../service/rewiewApi';

export default function useReviews(productId: string, page = 0, size = 20, enabled = true) {
  return useQuery({
    queryKey: ['reviews', productId],
    queryFn: () => ReviewService.getReviewByProductId(productId, page, size),
    enabled: !!productId && enabled,
    staleTime: 5 * 60 * 1000,
    refetchOnWindowFocus: false,
  });
}
