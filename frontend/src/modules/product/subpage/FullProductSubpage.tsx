import HorizontalTab, { HorizontalTabItem } from '@/components/custom/HorizontalTab';
import { Info, MessageSquareDiff, Settings } from 'lucide-react';
import FullProductViewer from '../components/ViewFullProduct';
import { Product } from '../model/Product';
import React, { useEffect } from 'preact/compat';
import { AppDispatch, RootState } from '@/store';
import { useDispatch, useSelector } from 'react-redux';
import { selectProductById } from '../selectors';
import { useParams } from 'react-router-dom';
import { fetchProductSuccess } from '../state/productSlice';
import useProductByUuid from '../hooks/useProduct';

const tabs: (product: Product) => HorizontalTabItem[] = (product: Product) => [
  {
    id: 'info',
    icon: <Info />,
    label: 'Info',
    content: <FullProductViewer product={product} />,
  },
  {
    id: 'review',
    icon: <MessageSquareDiff />,
    label: 'Review',
    content: <></>,
  },
  {
    id: 'setting',
    icon: <Settings />,
    label: 'Setting',
    content: <></>,
  },
];

const FullproductSubpage: React.FC = () => {
  const { id: uuid } = useParams<{ id: string }>();
  const dispatch = useDispatch<AppDispatch>();
  const cachedProduct = useSelector((state: RootState) =>
    uuid ? selectProductById(state, uuid) : undefined
  );
  const {
    data: apiProduct,
    isLoading,
    error,
  } = useProductByUuid(uuid!, {
    queryKey: ['product', uuid],
    enabled: !!uuid && !cachedProduct,
  });

  useEffect(() => {
    if (apiProduct && !cachedProduct) {
      dispatch(fetchProductSuccess(apiProduct.item));
    }
  }, [apiProduct, cachedProduct, dispatch]);
  const product = cachedProduct ?? apiProduct?.item;

  if (isLoading && !product) return <p>Loading product…</p>;
  if (error && !product) return <p>Failed to load product.</p>;
  if (!product) return <p>Product not found.</p>;

  return (
    <div className="w-full">
      <HorizontalTab.Tabs tabs={tabs(product)} defaultTabId="info" />
    </div>
  );
};

export default FullproductSubpage;
