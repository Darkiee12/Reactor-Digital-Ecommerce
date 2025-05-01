import { Outlet, useLocation } from 'react-router-dom';
import store, { RootState } from '@/store';
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
import {
  User as ProfileIcon,
  Users as UserManagementIcon,
  Gamepad2 as ProductManagementIcon,
  Cat as BrandManagementIcon,
  ChartBarStacked as CategoryManagementIcon,
} from 'lucide-react';

const selectCurrentUser = (state: RootState) => state.user.currentUser;

const Logo = () => {
  return (
    <div className="w-12 h-12 rounded-full bg-white flex items-center justify-center shadow">
      <img src="/logo.png" alt="logo icon" className="object-contain" />
    </div>
  );
};

const ProfileButton: React.FC = () => {
  const { mutate: logout } = useLogout();
  const user = useSelector(selectCurrentUser);
  return (
    user && (
      <Avatar className="bg-white rounded-full w-12 h-12">
        <DropdownMenu>
          <DropdownMenuTrigger className="flex items-center gap-1">
            <AvatarImage src="/profile.png" alt="@shadcn" />
            <AvatarFallback>CN</AvatarFallback>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="start" className="bg-[#151B23] text-white">
            <DropdownMenuItem className="">Profile</DropdownMenuItem>
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
  const location = useLocation();
  return (
    <div className="w-full bg-black text-white border-b border-[#3D444D]">
      <div className="flex items-center px-4 py-4">
        <div className="flex items-center">
          <Logo />
          <p className="px-5 text-2xl">{store.getState().user.currentUser?.username}</p>
        </div>
        <div className="flex-grow" />
        <ProfileButton />
      </div>
      <div className="w-full flex gap-4 px-4">
        <Link
          to="/profile"
          className={`flex gap-x-1 py-2 px-1 ${
            location.pathname === '/profile' && 'border-b border-[#F78166]'
          }`}
        >
          <ProfileIcon /> Profile
        </Link>
        <Link
          to="/users"
          className={`flex gap-x-1 py-2 px-1 ${
            location.pathname === '/users' && 'border-b border-[#F78166]'
          }`}
        >
          <UserManagementIcon /> User Management
        </Link>
        <Link
          to="/products"
          className={`flex gap-x-1 py-2 px-1 ${
            location.pathname === '/products' && 'border-b border-[#F78166]'
          }`}
        >
          <ProductManagementIcon /> Product Management
        </Link>
        <Link
          to="/brands"
          className={`flex gap-x-1 py-2 px-1 ${
            location.pathname === '/brands' && 'border-b border-[#F78166]'
          }`}
        >
          <BrandManagementIcon /> Brand Management
        </Link>
        <Link
          to="/categories"
          className={`flex gap-x-1 py-2 px-1 ${
            location.pathname === '/categories' && 'border-b border-[#F78166]'
          }`}
        >
          <CategoryManagementIcon /> Category Management
        </Link>
      </div>
    </div>
  );
};

const Layout: React.FC = () => {
  return (
    <div className="w-full h-screen bg-[#0D1117]">
      <Navbar />
      <main className="p-4 text-white">
        <Outlet />
      </main>
    </div>
  );
};

export default Layout;
