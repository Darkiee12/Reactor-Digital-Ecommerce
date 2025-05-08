import CategoryTable from '@/modules/category/components/CategoryTable';
import { useCategories } from '@/modules/category/hooks/useCategories';
import { Cat, Loader } from 'lucide-react';
import { useState } from 'preact/hooks';

const CategoryPage = () => {
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(100);

  const { data: brands, isLoading } = useCategories({ page, size });

  return (
    <div className="px-8 w-full">
      {isLoading && <Loader />}
      {brands?.items.length === 0 && (
        <div className="w-full flex items-center justify-center">
          <Cat />
          <span>Nothing displays here</span>
        </div>
      )}
      {brands && brands.items.length > 0 && <CategoryTable categories={brands.items} />}
    </div>
  );
};

export default CategoryPage;
