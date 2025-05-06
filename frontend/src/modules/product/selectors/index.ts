import { RootState } from '@/store';
import { Product } from '@/modules/product/model/Product';

export const selectProductById = (state: RootState, uuid: string): Product | undefined =>
  state.product.byId[uuid];

export const selectAllProducts = (state: RootState): Product[] => Object.values(state.product.byId);
