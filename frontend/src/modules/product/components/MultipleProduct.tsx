import { Product } from '@/modules/product/model/Product';
import store from '@/store';
import { Cat, ChartBarStacked, Eye, Star, Tag } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { fetchProductSuccess } from '../state/productSlice';

const DisplayMultipleProducts = ({ products }: { products: Product[] }) => {
  const navigate = useNavigate();
  const onViewProduct = (product: Product) => {
    store.dispatch(fetchProductSuccess(product));
    navigate(`/products/${product.uuid}`);
  };
  return (
    <div>
      {products.length === 0 ? (
        <div className="text-center">No products found.</div>
      ) : (
        <div className="flex flex-col gap-y-2 text-[#9198A1]">
          {products.map(product => (
            <div className="p-4 pb-6 shadow-md border-b border-b-[#3D444D] ">
              <div key={product.uuid} className="w-full flex justify-between">
                <h3 className="text-xl font-semibold text-blue-500">{product.name}</h3>
                <div className="flex items-center shadow-md border-2 dark:border-[#3D444D] px-4 rounded-md h-10">
                  <span className="flex items-center border-r-2 dark:border-[#3D444D] pr-2 h-full">
                    <Star className="w-4 h-4 mr-1" />
                    <span className="text-sm">5</span>
                  </span>
                  <div className="flex items-center justify-center pl-2 h-full">
                    <button
                      onClick={() => onViewProduct(product)}
                      className="flex items-center justify-center"
                    >
                      <Eye className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              </div>
              <div className="w-full flex gap-x-4 text-sm">
                <span className="">
                  <Cat className="inline mr-0.5 w-4 h-4" /> {product.brandName}
                </span>
                <span className="">
                  <Tag className="inline mr-0.5 w-4 h-4" /> ₫
                  {Intl.NumberFormat().format(product.price)}
                </span>
                <span className="">
                  <ChartBarStacked className="inline mr-0.5 w-4 h-4" />
                  {product.categories.join(', ')}
                </span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default DisplayMultipleProducts;
