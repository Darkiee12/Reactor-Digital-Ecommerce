import { Product } from '../model/Product';
import { ProductsFinder } from '../model/enums';
import API from '@/shared/response/api/axios';

const api = API.getClient();
const getProductByUuid = async (uuid: string) => {
  return await api.getData<Product>(`/products/${uuid}`);
};

const getProducts = async (finder: ProductsFinder, page: number = 0, size: number = 20) => {
  const { type, value } = finder;
  let url = '';
  switch (type) {
    case 'category':
      url = `/products/byCategory/${value}?page=${page}&size=${size}`;
      break;
    case 'brand':
      url = `/products/byBrand/${value}?page=${page}&size=${size}`;
      break;
    case 'name':
      url = `/products/search?name=${value}&page=${page}&size=${size}`;
      break;
    default:
      throw new Error('Invalid finder type');
  }
  return await api.getPageData<Product>(url);
};

const ProductService = {
  getProductByUuid,
  getProducts,
};
export default ProductService;
