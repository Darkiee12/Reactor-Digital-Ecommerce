import api from '@/shared/response/api/axios';
import { Product } from '../model/Product';
import DataResponse from '@/shared/response/model/DataResponse';
import PageResponse from '@/shared/response/model/PageResponse';
import { ProductsFinder } from '../model/enums';

const getProductByUuid = async (uuid: string) => {
  return await api.get<DataResponse<Product>>(`/products/uuid/${uuid}`);
};

const getProducts = async (finder: ProductsFinder, page: number = 0) => {
  const { type, value } = finder;
  let url = '';
  switch (type) {
    case 'category':
      url = `/products/category/${value}?page=0&size=20`;
      break;
    case 'brand':
      url = `/products/brand/${value}?page=${page}&size=20`;
      break;
    case 'name':
      url = `/products/search?name=${value}&page=${page}&size=20`;
      break;
    default:
      throw new Error('Invalid finder type');
  }
  return await api.get<PageResponse<Product>>(url);
};

const ProductService = {
  getProductByUuid,
  getProducts,
};
export default ProductService;
