import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { Product } from '../model/Product';

interface ProductState {
  byId: Record<string, Product>;
  loading: boolean;
  error: string | null;
}

const initialState: ProductState = {
  byId: {},
  loading: false,
  error: null,
};

export const productSlice = createSlice({
  name: 'product',
  initialState,
  reducers: {
    fetchProductStart(state) {
      state.loading = true;
      state.error = null;
    },
    fetchProductSuccess(state, action: PayloadAction<Product>) {
      const product = action.payload;
      state.byId[product.uuid] = product;
      state.loading = false;
    },
    fetchMultipleProductsSuccess(state, action: PayloadAction<Product[]>) {
      action.payload.forEach(product => {
        state.byId[product.uuid] = product;
      });
      state.loading = false;
    },
    fetchProductFailure(state, action: PayloadAction<string>) {
      state.loading = false;
      state.error = action.payload;
    },
    updateProduct(state, action: PayloadAction<Product>) {
      const product = action.payload;
      if (state.byId[product.uuid]) {
        state.byId[product.uuid] = product;
      }
    },
    removeProduct(state, action: PayloadAction<string>) {
      delete state.byId[action.payload];
    },
  },
});

export const {
  fetchProductStart,
  fetchProductSuccess,
  fetchMultipleProductsSuccess,
  fetchProductFailure,
  updateProduct,
  removeProduct,
} = productSlice.actions;
