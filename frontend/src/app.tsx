import './app.css';
import store, { RootState } from '@/store';
import { useSelector } from 'react-redux';
import { useEffect, useState } from 'preact/hooks';
import { Routes, Route, useNavigate, useLocation } from 'react-router-dom';
import Layout from '@/pages/Layout';
import LoginPage from '@/pages/LoginPage';
import ProfilePage from '@/pages/ProfilePage';
import UserPage from '@/pages/UserPage';
import ProductPage from '@/pages/ProductPage';
import BrandPage from './pages/BrandPage';
import CategoryPage from '@/pages/CategoryPage';
import useRefreshToken from './modules/user/hooks/useRefreshToken';
import AuthorizedLayout from './pages/AuthorizedLayout';

export default function App() {
  const token = useSelector((state: RootState) => state.auth.accessToken);
  const user = useSelector((state: RootState) => state.user.currentUser);
  const navigate = useNavigate();
  const location = useLocation();
  const refreshTokenMutation = useRefreshToken();

  const [refreshLoading, setRefreshLoading] = useState(true);

  useEffect(() => {
    const refresh = async () => {
      if (!user && !token) {
        try {
          console.log('Refreshing token...');
          await refreshTokenMutation.mutateAsync();
          console.log('Token refreshed.');
        } catch (err) {
          console.error('Token refresh failed:', err);
        }
      }
      setRefreshLoading(false);
    };

    refresh();
  }, []);

  useEffect(() => {
    if (!refreshLoading) {
      if (!user) {
        navigate('/', { replace: true });
      } else if (location.pathname === '/') {
        navigate('/profile', { replace: true });
      }
    }
  }, [refreshLoading, user]);

  if (refreshLoading) {
    return <div>Refreshing session...</div>;
  }

  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        {!token && <Route index element={<LoginPage />} />}
        {user && (
          <Route element={<AuthorizedLayout />}>
            <Route path="/profile" element={<ProfilePage />} />
            <Route path="/users" element={<UserPage />} />
            <Route path="/products" element={<ProductPage />} />
            <Route path="/brands" element={<BrandPage />} />
            <Route path="/categories" element={<CategoryPage />} />
          </Route>
        )}
      </Route>
    </Routes>
  );
}
