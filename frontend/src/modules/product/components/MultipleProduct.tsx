import { Product } from '@/modules/product/model/Product';

const DisplayMultipleProducts = ({ products }: { products: Product[] }) => {
  return (
    <div>
      {products.length === 0 ? (
        <div className="text-center">No products found.</div>
      ) : (
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
          {products.map(product => (
            <div key={product.uuid} className="border rounded-lg p-4 bg-white shadow-md">
              <h3 className="text-lg font-semibold">{product.name}</h3>
              <p className="text-gray-600">Brand: {product.brandName}</p>
              <p className="text-gray-800 font-bold">Price: ${product.price}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default DisplayMultipleProducts;
