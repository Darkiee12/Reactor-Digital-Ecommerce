import { useState } from 'preact/hooks';
import { Review } from '../model/Review';
import StarRating from './StarRating';

const ReviewItem: React.FC<{ review: Review }> = ({ review }) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const { rating, comment, fullName, createdAt } = review;

  const displayComment = isExpanded
    ? comment
    : comment.slice(0, 50) + (comment.length > 50 ? '...' : '');

  return (
    <div className="border-b py-4">
      <div className="flex items-center space-x-2">
        <StarRating rating={rating} />
        <span className="text-sm font-medium">{rating.toFixed(1)}</span>
      </div>
      <p className="mt-2">{displayComment}</p>
      {comment.length > 50 && (
        <button
          className="text-blue-500 hover:underline text-sm"
          onClick={() => setIsExpanded(!isExpanded)}
        >
          {isExpanded ? 'See accelerate' : 'See more'}
        </button>
      )}
      <div className="text-sm text-gray-500 mt-2">
        {fullName} on {new Date(createdAt).toLocaleDateString()}
      </div>
    </div>
  );
};

export default ReviewItem;
