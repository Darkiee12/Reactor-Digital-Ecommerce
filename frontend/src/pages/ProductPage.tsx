import { useState } from 'preact/hooks';
import * as ProductComponent from '@/modules/product/subpage';
import TabComponent, { VerticalTabItem } from '@/components/custom/VerticalTab';
import { ALargeSmall, Cat, ChartBarStacked, IdCard } from 'lucide-react';

const productTabs: VerticalTabItem[] = [
  {
    id: 'finduuid',
    label: (
      <span>
        <IdCard className="inline mr-2" />
        By UUID
      </span>
    ),
    Component: ProductComponent.FindByUuidSegment,
  },
  {
    id: 'findbrand',
    label: (
      <span>
        <Cat className="inline mr-2" />
        By brand
      </span>
    ),
    Component: ProductComponent.FindAllProductsByBrandSegment,
  },
  {
    id: 'findcategory',
    label: (
      <span>
        <ChartBarStacked className="inline mr-2" />
        By category
      </span>
    ),
    Component: ProductComponent.FindAllProductsByCategorySegment,
  },
  {
    id: 'findname',
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
  const [selection, setSelection] = useState('finduuid');
  return (
    <div className="w-full flex pt-5 pl-2 md:pl-20">
      <div className="w-full md:w-1/5">
        <TabComponent.VerticalTabsNav
          tabs={productTabs}
          currentId={selection}
          onSelect={setSelection}
        />
      </div>
      <div className="w-full md:w-4/5 pl-2 md:pl-8">
        <TabComponent.VerticalTabsPanels tabs={productTabs} currentId={selection} />
      </div>
    </div>
  );
};

export default ProductPage;
