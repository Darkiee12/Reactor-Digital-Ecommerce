import api from '@/shared/response/api/axios';
import PageResponse from '@/shared/response/model/PageResponse';
import Brand from '../model/brand';

const getAllBrands = async () => {
  return await api.get<PageResponse<Brand>>('/brands');
};

const getBrandById = async (id: string) => {
  return await api.get<Brand>(`/brands/${id}`);
};

const createBrand = async (brand: Brand) => {
  return await api.post<Brand>('/brands', brand);
};
