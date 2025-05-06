import { useState } from 'preact/hooks';
import * as ProductComponent from '@/modules/product/components';
import TabComponent, { TabItem } from '@/components/custom/Tab';
import { ALargeSmall, Cat, ChartBarStacked, IdCard } from 'lucide-react';

const productTabs: TabItem[] = [
  {
    label: (
      <span>
        <IdCard className="inline mr-2" />
        By UUID
      </span>
    ),
    Component: ProductComponent.FindByUuidSegment,
  },
  {
    label: (
      <span>
        <Cat className="inline mr-2" />
        By brand
      </span>
    ),
    Component: ProductComponent.FindAllProductsByBrandSegment,
  },
  {
    label: (
      <span>
        <ChartBarStacked className="inline mr-2" />
        By category
      </span>
    ),
    Component: ProductComponent.FindAllProductsByCategorySegment,
  },
  {
    label: (
      <span>
        <ALargeSmall className="inline mr-2" />
        By name
      </span>
    ),
    Component: ProductComponent.FindAllProductsByName,
  },
];

const ProductPage = () => {
  const [selection, setSelection] = useState(0);
  return (
    <div className="w-full flex pt-5 pl-20">
      <div className="w-full md:w-1/5">
        <TabComponent.TabsNav tabs={productTabs} current={selection} onSelect={setSelection} />
      </div>
      <div className="w-full md:w-4/5 pl-8">
        <TabComponent.TabsPanels tabs={productTabs} current={selection} />
      </div>
    </div>
  );
};

export default ProductPage;
