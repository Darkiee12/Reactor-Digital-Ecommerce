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

const search = async (searchTerm: string, page: number = 0) => {
  return await api.get<PageResponse<Brand>>(
    `/brands/search?searchTerm=${searchTerm}&${page}=0&size=10`
  );
};

const BrandService = {
  getAllBrands,
  getBrandById,
  createBrand,
  search,
};
export default BrandService;
