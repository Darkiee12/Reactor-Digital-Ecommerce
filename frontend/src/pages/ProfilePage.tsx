import { RootState } from '@/store';
import { useState } from 'preact/hooks';
import { useSelector } from 'react-redux';
import ProfileSegment from '@/modules/user/components/ProfileSegment';
import AccountSegment from '@/modules/user/components/AccountSegment';
import TabComponent, { VerticalTabItem } from '@/components/custom/VerticalTab';
import { Settings, UserRoundPen } from 'lucide-react';

const profileTabs: VerticalTabItem[] = [
  {
    id: 'profile',
    label: (
      <>
        <UserRoundPen className="inline mr-2" /> Public profile
      </>
    ),
    Component: ProfileSegment,
  },
  {
    id: 'account',
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
  const [selection, setSelection] = useState('profile');

  return (
    <div className="w-full flex flex-wrap pt-5 pl-2 md:pl-20">
      <div className="w-full md:w-1/5">
        <TabComponent.VerticalTabsNav
          tabs={profileTabs}
          currentId={selection}
          onSelect={setSelection}
        />
      </div>
      <div className="w-full md:w-4/5">
        <TabComponent.VerticalTabsPanels tabs={profileTabs} currentId={selection} />
      </div>
    </div>
  );
};

export default ProfilePage;
