import { Button } from '@/components/ui/button';
import { RootState } from '@/store';
import { useState } from 'preact/hooks';
import * as ProductComponent from '@/modules/product/components';
const ProductPage = () => {
  const [selection, setSelection] = useState(0);
  return (
    <div className="w-full flex pt-5">
      <div className="w-full md:w-1/4">
        <ProfileNav selection={selection} setSelection={setSelection} />
      </div>
      <div className="w-full md:w-3/4 px-4">
        {selection === 0 && <ProductComponent.FindByUuidSegment />}
        {selection === 1 && <ProductComponent.FindAllProductsByBrandSegment />}
        {selection === 2 && <ProductComponent.FindAllProductsByCategorySegment />}
      </div>
    </div>
  );
};

interface ProfileNavProps {
  selection: number;
  setSelection: (selection: number) => void;
}

const ProfileNav = ({ selection, setSelection }: ProfileNavProps) => {
  return (
    <div className="w-full pl-4 flex flex-col gap-2">
      <Button
        variant="ghost"
        onClick={() => setSelection(0)}
        className={`w-full ${selection === 0 ? 'text-black bg-white' : 'bg-transparent'}`}
      >
        Find by UUID
      </Button>
      <Button
        variant="ghost"
        onClick={() => setSelection(1)}
        className={`w-full ${selection === 1 ? 'text-black bg-white' : 'bg-transparent'}`}
      >
        Find all products by brand
      </Button>
      <Button
        variant="ghost"
        onClick={() => setSelection(2)}
        className={`w-full ${selection === 2 ? 'text-black bg-white' : 'bg-transparent'}`}
      >
        Find all products by category
      </Button>
    </div>
  );
};

export default ProductPage;
