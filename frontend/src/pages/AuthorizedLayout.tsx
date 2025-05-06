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
const navItems = [
  {
    to: '/profile',
    Icon: <ProfileIcon className="" />,
    label: 'Profile',
  },
  { to: '/users', Icon: <UserManagementIcon />, label: 'User' },
  { to: '/products', Icon: <ProductManagementIcon />, label: 'Product' },
  { to: '/brands', Icon: <BrandManagementIcon />, label: 'Brand' },
  { to: '/categories', Icon: <CategoryManagementIcon />, label: 'Category' },
];

const AuthorizedLayout = () => {
  const location = useLocation();
  const user = useSelector((state: RootState) => state.user.currentUser)!;

  return (
    <main className="w-full">
      <div className="w-full flex flex-wrap gap-6 px-8 border-b border-[#3D444D] mb-2 dark:bg-black bg-white">
        {navItems.map(({ to, Icon, label }) => {
          const isActive = location.pathname === to;
          return (
            <div
              key={to}
              className={`transition-all duration-200 ease-in-out ${
                isActive ? 'border-b-2 border-[#F78166] font-bold' : 'dark:text-gray-300'
              }`}
            >
              <Link
                to={to}
                className="rounded-xl flex gap-x-1 py-2 px-2 hover:dark:bg-gray-800 hover:bg-gray-100 hover:dark:text-white"
              >
                {Icon} {label}
              </Link>
            </div>
          );
        })}
      </div>
      <div className="pl-2 md:pl-20">
        <ProfileHeader user={user} />
      </div>

      <Outlet />
    </main>
  );
};

export default AuthorizedLayout;
