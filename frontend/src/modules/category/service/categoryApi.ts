import API from '@/shared/response/api/axios';
import { Category } from '../model/Category';
const api = API.getClient();
const getCategories = async (page: number = 0, size: number = 20) => {
  return await api.getPageData<Category>(`/categories?page=${page}&size=${size}`);
};

const CategoryService = {
  getCategories,
};

export default CategoryService;
