import api from '@/shared/response/api/axios';
import { Product } from '../model/Product';
import { Result } from 'ts-results';
import DataResponse from '@/shared/response/model/DataResponse';
import { AxiosError, AxiosResponse } from 'axios';
import { ErrorResponse } from 'react-router-dom';
import PageResponse from '@/shared/response/model/PageResponse';
import { ProductsFinder } from '../model/enums';

const getProductByUuid = async (uuid: string) => {
  return await Result.wrapAsync<AxiosResponse<DataResponse<Product>>, AxiosError<ErrorResponse>>(
    () => api.get<DataResponse<Product>>(`/products/uuid/${uuid}`)
  );
};

const getProducts = async (
  finder: ProductsFinder
): Promise<Result<AxiosResponse<PageResponse<Product>>, AxiosError<ErrorResponse>>> => {
  const { type, value } = finder;

  const url = type === 'category' ? `/products/category/${value}` : `/products/brand/${value}`;

  return await Result.wrapAsync<AxiosResponse<PageResponse<Product>>, AxiosError<ErrorResponse>>(
    () => api.get<PageResponse<Product>>(url)
  );
};

const ProductService = {
  getProductByUuid,
  getProducts,
};
export default ProductService;
