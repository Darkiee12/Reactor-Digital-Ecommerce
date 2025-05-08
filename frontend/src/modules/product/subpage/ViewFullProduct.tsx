import { Product } from '@/modules/product/model/Product';
import ProductImageCarousel from '@/modules/product/components/ProductImageCarousel';
import ProductMetadata from '@/modules/product/components/ProductMetadata';
import ProductIcon from '@/modules/product/components/ProductIcon';
import ProductSpecifications from '@/modules/product/components/ProductSpecifications';

const FullProductViewer = ({ product }: { product: Product }) => (
  <div className="flex flex-col gap-2 pr-8">
    <span className="text-xl border-b border-b-[#3D444D] pb-4 flex gap-3 items-center mb-4">
      {ProductIcon('brand', product.brandName, 24)}
      {product.name}
    </span>
    <div className="flex flex-wrap">
      <div className="sm:w-4/5 px-3">
        <ProductImageCarousel imagesUuid={product.imagesUuid} />
        <ProductSpecifications data={product.specifications} />
      </div>
      <div className="sm:w-1/5">
        <ProductMetadata
          uuid={product.uuid}
          brandName={product.brandName}
          price={product.price}
          createdAt={product.createdAt}
          categories={product.categories}
          description={product.description}
        />
      </div>
    </div>
  </div>
);

export default FullProductViewer;
