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
import { RootState } from '@/store';
import { Moon, Sun } from 'lucide-react';
import { useTheme } from '@/components/ThemeProvider';
import { Button } from '@/components/ui/button';
import React from 'preact/compat';

const selectCurrentUser = (state: RootState) => state.user.currentUser;

const Logo = () => {
  const { theme } = useTheme();
  return (
    <div className="w-8 h-8 rounded-full dark:bg-white bg-black flex items-center justify-center shadow">
      <img
        src={theme === 'dark' ? '/logo.png' : '/logo_light.png'}
        alt="logo icon"
        className="object-contain"
      />
    </div>
  );
};

const ProfileButton: React.FC = () => {
  const { mutate: logout } = useLogout();
  const user = useSelector(selectCurrentUser);
  return (
    user && (
      <Avatar className="dark:bg-white bg-black rounded-full w-8 h-8">
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
  const { theme, setTheme } = useTheme();
  const location = useLocation();
  return (
    <div className="w-full dark:bg-black bg-[#d1d5da80]">
      <div className="flex flex-wrap items-center px-4 pt-4 pb-2">
        <div className="flex items-center">
          <Link to="/profile">
            <Logo />
          </Link>
          <div className="flex gap-x-4 items-center">
            <span className="ml-4 font-bold">Digital Reactive Store</span>
            {location.pathname && (
              <>
                <span className="dark:text-gray-400">/</span>
                <span>{location.pathname.split('/')[1]}</span>
              </>
            )}
          </div>
        </div>
        <div className="flex-grow" />
        <Button
          variant="ghost"
          className="mr-2 cursor-pointer"
          onClick={() => setTheme(theme === 'dark' ? 'light' : 'dark')}
        >
          {theme === 'dark' ? <Moon /> : <Sun className="text-black" />}
        </Button>
        <ProfileButton />
      </div>
    </div>
  );
};

const Footer: React.FC = () => {
  const { theme } = useTheme();
  return (
    <>
      <div className="w-full h-full flex justify-center items-center py-4 gap-x-4">
        <div className="w-8 h-8 rounded-full dark:bg-white bg-black shadow">
          <img
            src={theme === 'dark' ? '/logo.png' : '/logo_light.png'}
            alt="logo icon"
            className="object-contain"
          />
        </div>
        <p>© 2025 Digital Reactive Store, Inc.</p>
      </div>
      {/* <div className="bg-none w-1/9 px-8">
        <img src="./noMoit.png" alt="moit icon" className="object-contain" />
      </div> */}
    </>
  );
};

const Layout: React.FC = () => {
  return (
    <div className="min-h-screen flex flex-col w-full dark:bg-[#0D1117]">
      <Navbar />
      <div className="flex-1 w-full">
        <Outlet />
      </div>
      <Footer />
    </div>
  );
};

export default Layout;
