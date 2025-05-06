import React, { ReactNode } from 'preact/compat';

type TabItem = { label: ReactNode; Component: React.FC };

interface TabsNavProps {
  tabs: TabItem[];
  current: number;
  onSelect: (i: number) => void;
}
const TabsNav: React.FC<TabsNavProps> = ({ tabs, current, onSelect }) => (
  <div className="w-full pl-4 flex flex-col gap-4 text-sm">
    {tabs.map((tab, i) => {
      const isSelected = i === current;
      return (
        <button
          key={i}
          onClick={() => onSelect(i)}
          className={`w-full flex justify-start relative text-white hover:bg-gray-700 hover:rounded-sm ${
            isSelected ? 'font-semibold' : ''
          }`}
        >
          <span className={`w-1 ${isSelected ? 'bg-blue-500' : 'bg-none'}`} />
          <span
            className={`w-full text-left py-1 pl-2 rounded-sm ${isSelected ? 'bg-gray-900' : ''}`}
          >
            {tab.label}
          </span>
        </button>
      );
    })}
  </div>
);

interface TabsPanelsProps {
  tabs: TabItem[];
  current: number;
}

const TabsPanels: React.FC<TabsPanelsProps> = ({ tabs, current }) => (
  <>
    {tabs.map((tab, i) => {
      const Panel = tab.Component;
      return (
        <div
          key={i}
          className={i === current ? 'block' : 'hidden'}
          aria-hidden={i === current ? undefined : true}
        >
          <Panel />
        </div>
      );
    })}
  </>
);

const TabComponent = {
  TabsNav,
  TabsPanels,
};
export default TabComponent;
export type { TabItem, TabsNavProps, TabsPanelsProps };
