import HorizontalTab, { HorizontalTabItem } from '@/components/custom/HorizontalTab';
import { Info, MessageSquareDiff, Settings } from 'lucide-react';
import FullProductViewer from '@/modules/product/subpage/ViewFullProduct';
import { Product } from '@/modules/product/model/Product';
import React, { useEffect } from 'preact/compat';
import { AppDispatch, RootState } from '@/store';
import { useDispatch, useSelector } from 'react-redux';
import { selectProductById } from '@/modules/product/selectors';
import { useParams } from 'react-router-dom';
import { fetchProductSuccess } from '@/modules/product/state/productSlice';
import useProductByUuid from '@/modules/product/hooks/useProduct';
import ProductReview from './ProductReview';

const tabs: (product: Product) => HorizontalTabItem[] = (product: Product) => [
  {
    id: 'info',
    icon: <Info className="inline w-4 h-4" />,
    label: 'Info',
    content: <FullProductViewer product={product} />,
  },
  {
    id: 'review',
    icon: <MessageSquareDiff className="inline w-4 h-4" />,
    label: 'Review',
    content: <ProductReview product={product} />,
  },
  {
    id: 'setting',
    icon: <Settings className="inline w-4 h-4" />,
    label: 'Setting',
    content: <></>,
  },
];

const FullProductSubpage: React.FC = () => {
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
      dispatch(fetchProductSuccess(apiProduct));
    }
  }, [apiProduct, cachedProduct, dispatch]);
  const product = cachedProduct ?? apiProduct;

  if (isLoading && !product) return <p>Loading product…</p>;
  if (error && !product) return <p>Failed to load product.</p>;
  if (!product) return <p>Product not found.</p>;

  return (
    <div className="w-full">
      <HorizontalTab.Tabs tabs={tabs(product)} defaultTabId="info" />
    </div>
  );
};

export default FullProductSubpage;
