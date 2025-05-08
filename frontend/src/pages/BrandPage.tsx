import BrandTable from '@/modules/brand/component/BrandTable';
import useBrands from '@/modules/brand/hooks/useBrand';
import { Cat, Loader } from 'lucide-react';
import { useState } from 'preact/hooks';

const BrandPage = () => {
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(100);

  const { data: brands, isLoading } = useBrands({ page, size });

  return (
    <div className="px-8 w-full">
      {isLoading && <Loader />}
      {brands?.items.length === 0 && (
        <div className="w-full flex items-center justify-center">
          <Cat />
          <span>Nothing displays here</span>
        </div>
      )}
      {brands && brands.items.length > 0 && <BrandTable brands={brands.items} />}
    </div>
  );
};

export default BrandPage;
