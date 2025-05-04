import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';

interface ProfileHeaderProps {
  user: { username: string; fullName: string };
}

const ProfileHeader = ({ user }: ProfileHeaderProps) => {
  return (
    <div className="w-full flex pt-3 px-4">
      <Avatar className="bg-white rounded-full w-12 h-12 mr-4">
        <AvatarImage src="/profile.png" alt="@shadcn" />
        <AvatarFallback>CN</AvatarFallback>
      </Avatar>
      <div className="text-[#9198A1]">
        <p>
          <span className="font-bold text-white">{user.username}</span>&nbsp;({user.fullName})
        </p>
        <p>Your personal account</p>
      </div>
    </div>
  );
};

export default ProfileHeader;
