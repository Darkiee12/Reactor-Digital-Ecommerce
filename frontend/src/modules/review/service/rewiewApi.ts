import API from '@/shared/response/api/axios';
import { Review } from '../model/Review';

const api = API.getClient();

const getReviewByProductId = async (productId: string, page: number = 0, size: number = 20) => {
  return await api.getPageData<Review>(`/reviews/byProduct/${productId}?page=${page}&size=${size}`);
};

const getAverageRatingByProductId = async (productId: string) => {
  return await api.getData<number>(`/reviews/byProduct/${productId}/rating`);
};

const ReviewService = {
  getReviewByProductId,
};

export default ReviewService;
