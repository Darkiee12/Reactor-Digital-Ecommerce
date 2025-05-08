import { Star } from 'lucide-react';
const StarRating: React.FC<{ rating: number }> = ({ rating }) => {
  const stars = [];
  for (let i = 1; i <= 5; i++) {
    if (rating >= i) {
      stars.push(<Star key={i} size={16} className="text-yellow-400" fill="currentColor" />);
    } else if (rating >= i - 0.5) {
      stars.push(
        <div key={i} className="relative w-4 h-4">
          <Star size={16} className="absolute" />
          <div className="absolute overflow-hidden w-2">
            <Star size={16} className="text-yellow-400" fill="currentColor" />
          </div>
        </div>
      );
    } else {
      stars.push(<Star key={i} size={16} className="text-gray-300" />);
    }
  }
  return <div className="flex space-x-1">{stars}</div>;
};

export default StarRating;
