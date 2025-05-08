import API from '@/shared/response/api/axios';
import PageResponse from '@/shared/response/model/PageResponse';
import Brand from '@/modules/brand/model/brand';

const api = API.getClient();

const getAllBrands = async (page: number = 0, size: number = 20) => {
  return await api.getPageData<Brand>(`/brands?page=${page}&size=${size}`);
};

const getBrandById = async (id: string) => {
  return await api.getData<Brand>(`/brands/${id}`);
};

const createBrand = async (brand: Brand) => {
  return await api.postData<Brand>('/brands', brand);
};

const search = async (searchTerm: string, page: number = 0) => {
  return await api.getPageData<Brand>(
    `/brands/search?searchTerm=${searchTerm}&page=${page}&size=10`
  );
};

const BrandService = {
  getAllBrands,
  getBrandById,
  createBrand,
  search,
};
export default BrandService;
