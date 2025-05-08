import { useState } from 'react';

import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from '@/components/ui/pagination';
import useReviews from '@/modules/review/hooks/useReviews';
import { Product } from '../model/Product';
import ReviewItem from '@/modules/review/components/ReviewItem';

const ProductReview: React.FC<{ product: Product }> = ({ product }) => {
  const [currentPage, setCurrentPage] = useState(0);
  const { data, isLoading, isError, error } = useReviews(product.uuid, currentPage);

  if (isLoading) {
    return <div className="w-full text-center py-4">Loading...</div>;
  }

  if (isError) {
    return <div className="w-full text-center py-4 text-red-500">Error: {error.message}</div>;
  }

  const { items: reviews, page: pageInfo } = data!;
  const { page: currentPageNum, totalPages, hasNext, hasPrevious } = pageInfo;

  const handlePrevious = (e: React.MouseEvent<HTMLAnchorElement>) => {
    e.preventDefault();
    if (hasPrevious) setCurrentPage(prev => prev - 1);
  };

  const handleNext = (e: React.MouseEvent<HTMLAnchorElement>) => {
    e.preventDefault();
    if (hasNext) setCurrentPage(prev => prev + 1);
  };

  return (
    <div className="w-full">
      <h2 className="text-2xl font-bold mb-4">Reviews</h2>
      {reviews.length === 0 ? (
        <p className="text-gray-500">No reviews yet.</p>
      ) : (
        <div className="space-y-4">
          {reviews.map(review => (
            <ReviewItem key={review.uuid} review={review} />
          ))}
        </div>
      )}
      {totalPages > 1 && (
        <Pagination className="mt-4">
          <PaginationContent>
            {hasPrevious && <PaginationPrevious href="#" onClick={handlePrevious} />}
            <PaginationItem>
              <PaginationLink href="#" isActive>
                {currentPageNum}
              </PaginationLink>
            </PaginationItem>
            {hasNext && <PaginationNext href="#" onClick={handleNext} />}
          </PaginationContent>
        </Pagination>
      )}
    </div>
  );
};

export default ProductReview;
