import { Product } from '@/modules/product/model/Product';
import ProductIcon from '@/modules/product/components/ProductIcon';
const FullProductViewer = ({ product }: { product: Product }) => {
  return (
    <div className="flex flex-col gap-2 px-8">
      <span className="text-xl border-b border-b-[#3D444D] pb-4 flex gap-3 items-center">
        {ProductIcon('brand', product.brandName)}
        {product.name}
      </span>
    </div>
  );
};

export default FullProductViewer;
