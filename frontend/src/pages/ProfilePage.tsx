import { RootState } from '@/store';
import { useState } from 'preact/hooks';
import { useSelector } from 'react-redux';
import ProfileSegment from '@/modules/user/components/ProfileSegment';
import AccountSegment from '@/modules/user/components/AccountSegment';
import TabComponent, { TabItem } from '@/components/custom/Tab';
import { Settings, UserRoundPen } from 'lucide-react';

const profileTabs: TabItem[] = [
  {
    label: (
      <>
        <UserRoundPen className="inline mr-2" /> Public profile
      </>
    ),
    Component: ProfileSegment,
  },
  {
    label: (
      <>
        <Settings className="inline mr-2" />
        Account
      </>
    ),
    Component: AccountSegment,
  },
];

const ProfilePage = () => {
  const user = useSelector((state: RootState) => state.user.currentUser)!;
  const [selection, setSelection] = useState(0);

  return (
    <div className="w-full flex pt-5 pl-20">
      <div className="w-full md:w-1/5">
        <TabComponent.TabsNav tabs={profileTabs} current={selection} onSelect={setSelection} />
      </div>
      <div className="w-full md:w-4/5">
        <TabComponent.TabsPanels tabs={profileTabs} current={selection} />
      </div>
    </div>
  );
};

export default ProfilePage;
