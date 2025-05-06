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
  const navItems = [
    { to: '/profile', Icon: ProfileIcon, label: 'Profile' },
    { to: '/users', Icon: UserManagementIcon, label: 'User' },
    { to: '/products', Icon: ProductManagementIcon, label: 'Product' },
    { to: '/brands', Icon: BrandManagementIcon, label: 'Brand' },
    { to: '/categories', Icon: CategoryManagementIcon, label: 'Category' },
  ];

  return (
    <main className="w-full text-white">
      <div className="w-full flex gap-6 px-8 border-b border-[#3D444D] bg-black mb-2">
        {navItems.map(({ to, Icon, label }) => {
          const isActive = location.pathname === to;
          return (
            <div
              key={to}
              className={`transition-all duration-200 ease-in-out ${
                isActive ? 'border-b-2 border-[#F78166] font-bold text-white' : 'text-gray-300'
              }`}
            >
              <Link
                to={to}
                className="rounded-xl flex gap-x-1 py-2 px-2 hover:bg-gray-800 hover:text-white"
              >
                <Icon /> {label}
              </Link>
            </div>
          );
        })}
      </div>
      <div className="pl-20">
        <ProfileHeader user={user} />
      </div>

      <Outlet />
    </main>
  );
};

export default AuthorizedLayout;
