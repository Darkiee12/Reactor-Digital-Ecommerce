import { Calendar, Cat, ChartBarStacked, IdCard, Tag } from 'lucide-react';

const ProductMetadata = ({
  uuid,
  brandName,
  price,
  createdAt,
  categories,
  description,
}: {
  uuid: string;
  brandName: string;
  price: number;
  createdAt: number;
  categories: string[];
  description: string;
}) => (
  <div className="w-full px-2 flex flex-col gap-y-2 border-b border-b-[#3D444D] pb-4 dark:text-gray-400">
    <p className="font-bold">About</p>
    <p className="text-normal italic">
      {description !== '' ? description : 'No description, website, or topics provided.'}
    </p>
    <ol className="flex flex-col gap-y-2 text-sm">
      <li className="flex items-center gap-x-2">
        <IdCard className="inline" />
        <span>{uuid}</span>
      </li>
      <li className="flex items-center gap-x-2">
        <Cat className="w-5 h-5 inline" />
        <span>{brandName}</span>
      </li>
      <li className="flex items-center gap-x-2">
        <Tag className="w-5 h-5 inline" />
        <span>{price.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })}</span>
      </li>
      <li className="flex items-center gap-x-2">
        <Calendar className="w-5 h-5 inline" />
        <span>
          {new Date(createdAt * 1000).toLocaleDateString('vi-VN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
          })}
        </span>
      </li>
      <li className="flex items-center gap-x-2">
        <ChartBarStacked className="w-5 h-5 inline" />
        <span>{categories.join(',')}</span>
      </li>
    </ol>
  </div>
);

export default ProductMetadata;
