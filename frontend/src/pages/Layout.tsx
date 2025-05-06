import { Outlet, useLocation } from 'react-router-dom';

import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { useSelector } from 'react-redux';
import useLogout from '@/modules/user/hooks/useLogout';
import { Link } from 'react-router-dom';
import store, { RootState } from '@/store';

const selectCurrentUser = (state: RootState) => state.user.currentUser;

const Logo = () => {
  return (
    <div className="w-8 h-8 rounded-full bg-white flex items-center justify-center shadow">
      <img src="/logo.png" alt="logo icon" className="object-contain" />
    </div>
  );
};

const ProfileButton: React.FC = () => {
  const { mutate: logout } = useLogout();
  const user = useSelector(selectCurrentUser);
  return (
    user && (
      <Avatar className="bg-white rounded-full w-8 h-8">
        <DropdownMenu>
          <DropdownMenuTrigger className="flex items-center gap-1">
            <AvatarImage src="/profile.png" alt="@shadcn" />
            <AvatarFallback>CN</AvatarFallback>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="start" className="bg-[#151B23] text-white">
            <DropdownMenuItem className="">
              <Link className="w-full text-left" to="/profile">
                Profile
              </Link>
            </DropdownMenuItem>
            <DropdownMenuItem className="">
              <button onClick={() => logout()} className="w-full text-left">
                Logout
              </button>
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </Avatar>
    )
  );
};

const Navbar: React.FC = () => {
  return (
    <div className="w-full bg-black text-white">
      <div className="flex items-center px-4 pt-4 pb-2">
        <div className="flex items-center">
          <Logo />
          <p className="ml-4 font-bold">Admin Dashboard</p>
        </div>
        <div className="flex-grow" />
        <ProfileButton />
      </div>
    </div>
  );
};

const Layout: React.FC = () => {
  return (
    <div className="w-full min-h-screen bg-[#0D1117]">
      <Navbar />
      <Outlet />
    </div>
  );
};

export default Layout;
