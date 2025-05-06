import { Product } from '@/modules/product/model/Product';
import { useForm } from 'react-hook-form';

export function useProductForm(defaultValues: Product) {
  const methods = useForm<Product>({
    defaultValues,
    mode: 'onChange',
  });
  return methods;
}
