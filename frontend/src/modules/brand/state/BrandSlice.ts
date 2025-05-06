import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import Brand from '@/modules/brand/model/Brand';

interface BrandState {
  brands: Set<Brand>;
}
const initialState: BrandState = { brands: new Set<Brand>() };

export const brandSlice = createSlice({
  name: 'brand',
  initialState,
  reducers: {
    setBrands(state, action: PayloadAction<Brand[]>) {
      state.brands = new Set<Brand>(action.payload);
    },
    addBrand(state, action: PayloadAction<Brand>) {
      const brand = action.payload;
      if (!state.brands.has(brand)) {
        state.brands.add(brand);
      }
    },
    removeBrand(state, action: PayloadAction<string>) {
      const brandName = action.payload;
      const brandToRemove = Array.from(state.brands).find(brand => brand.name === brandName);
      if (brandToRemove) {
        state.brands.delete(brandToRemove);
      }
    },
  },
});
