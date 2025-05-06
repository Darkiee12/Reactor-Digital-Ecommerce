import React, { ReactNode } from 'preact/compat';

type VerticalTabItem = { id: string; label: ReactNode; Component: React.FC };

interface VerticalTabsNavProps {
  tabs: VerticalTabItem[];
  currentId: string;
  onSelect: (id: string) => void;
}
interface VerticalTabsPanelsProps {
  tabs: VerticalTabItem[];
  currentId: string;
}

const VerticalTabsNav: React.FC<VerticalTabsNavProps> = ({ tabs, currentId, onSelect }) => (
  <div className="w-full pl-4 flex flex-col gap-4 text-sm">
    {tabs.map(tab => {
      const isSelected = tab.id === currentId;
      return (
        <button
          key={tab.id}
          onClick={() => onSelect(tab.id)}
          className={`w-full flex justify-start relative hover:dark:bg-gray-700 hover:bg-gray-100 hover:rounded-sm ${
            isSelected ? 'font-semibold' : ''
          }`}
          id={tab.id}
        >
          <span className={`w-1 ${isSelected ? 'bg-blue-500' : 'bg-none'}`} />
          <span
            className={`w-full text-left py-1 pl-2 rounded-sm ${
              isSelected ? 'dark:bg-gray-900' : ''
            }`}
          >
            {tab.label}
          </span>
        </button>
      );
    })}
  </div>
);

const VerticalTabsPanels: React.FC<VerticalTabsPanelsProps> = ({ tabs, currentId }) => (
  <>
    {tabs.map(tab => {
      const Panel = tab.Component;
      return (
        <div
          key={tab.id}
          className={tab.id === currentId ? 'block' : 'hidden'}
          aria-hidden={tab.id === currentId ? undefined : true}
        >
          <Panel />
        </div>
      );
    })}
  </>
);

const VerticalTabComponent = {
  VerticalTabsNav,
  VerticalTabsPanels,
};
export default VerticalTabComponent;
export type { VerticalTabItem, VerticalTabsNavProps, VerticalTabsPanelsProps };
