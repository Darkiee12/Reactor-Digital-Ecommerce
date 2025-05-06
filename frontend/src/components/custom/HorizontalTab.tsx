import React, { JSX, ReactNode, useState } from 'preact/compat';

// Define interfaces for tab items
interface HorizontalTabItem {
  id: string;
  label: string;
  icon?: ReactNode;
  content: ReactNode;
}

interface HorizontalTabsProps {
  tabs: HorizontalTabItem[];
  defaultTabId?: string;
}

const Tabs = ({ tabs, defaultTabId }: HorizontalTabsProps) => {
  const [activeTab, setActiveTab] = useState(defaultTabId || tabs[0]?.id);

  return (
    <main className="w-full">
      <div className="w-full flex gap-6 px-8 border-b border-[#3D444D] mb-2 dark:bg-black bg-white">
        {tabs.map(({ id, icon, label }) => {
          const isActive = activeTab === id;
          return (
            <div
              key={id}
              className={`transition-all duration-200 ease-in-out cursor-pointer ${
                isActive ? 'border-b-2 border-[#F78166] font-bold' : 'dark:text-gray-300'
              }`}
              onClick={() => setActiveTab(id)}
            >
              <div className="rounded-xl flex gap-x-1 py-2 px-2 hover:dark:bg-gray-800 hover:bg-gray-100 hover:dark:text-white">
                {icon} {label}
              </div>
            </div>
          );
        })}
      </div>

      {/* Tab Content */}
      <div className="pl-20">
        {tabs.map(({ id, content }) => (
          <div key={id} style={{ display: activeTab === id ? 'block' : 'none' }}>
            {content}
          </div>
        ))}
      </div>
    </main>
  );
};

const HorizontalTab = {
  Tabs,
};
export type { HorizontalTabsProps, HorizontalTabItem };
export default HorizontalTab;
