import { Outlet, useLocation } from 'react-router-dom';
import { Link } from 'react-router-dom';
import {
  User as ProfileIcon,
  Users as UserManagementIcon,
  Gamepad2 as ProductManagementIcon,
  Cat as BrandManagementIcon,
  ChartBarStacked as CategoryManagementIcon,
} from 'lucide-react';
import ProfileHeader from '@/modules/user/components/ProfileHeader';
import { useSelector } from 'react-redux';
import { RootState } from '@/store';
const AuthorizedLayout = () => {
  const location = useLocation();
  const user = useSelector((state: RootState) => state.user.currentUser)!;
  return (
    <main className="w-full text-white">
      <div className="w-full flex gap-4 px-4 border-b border-[#3D444D] bg-black mb-2">
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
      <ProfileHeader user={user} />
      <Outlet />
    </main>
  );
};

export default AuthorizedLayout;
