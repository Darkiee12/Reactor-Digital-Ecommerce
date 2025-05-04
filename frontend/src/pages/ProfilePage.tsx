import { RootState } from '@/store';
import { useState } from 'preact/hooks';
import { useSelector } from 'react-redux';
import { Button } from '@/components/ui/button';
import ProfileSegment from '@/modules/user/components/ProfileSegment';
import AccountSegment from '@/modules/user/components/AccountSegment';
const ProfilePage = () => {
  const user = useSelector((state: RootState) => state.user.currentUser)!;
  const [selection, setSelection] = useState(0);

  return (
    <div className="w-full flex pt-5">
      <div className="w-full md:w-1/4">
        <ProfileNav selection={selection} setSelection={setSelection} />
      </div>
      <div className="w-full md:w-3/4">
        {selection === 0 && <ProfileSegment />}
        {selection === 1 && <AccountSegment />}
      </div>
    </div>
  );
};

interface ProfileNavProps {
  selection: number;
  setSelection: (selection: number) => void;
}

const ProfileNav = ({ selection, setSelection }: ProfileNavProps) => {
  return (
    <div className="w-full pl-4 flex flex-col gap-2">
      <Button
        variant="ghost"
        onClick={() => setSelection(0)}
        className={`w-full ${selection === 0 ? 'text-black bg-white' : 'bg-transparent'}`}
      >
        Public Profile
      </Button>
      <Button
        variant="ghost"
        onClick={() => setSelection(1)}
        className={`w-full ${selection === 1 ? 'text-black bg-white' : 'bg-transparent'}`}
      >
        Account
      </Button>
    </div>
  );
};

export default ProfilePage;
