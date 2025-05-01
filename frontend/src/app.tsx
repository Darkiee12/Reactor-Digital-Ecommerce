import './app.css';
import { RootState } from '@/store';
import { useSelector } from 'react-redux';
import { useEffect, useState } from 'preact/hooks';
import { Routes, Route, useNavigate } from 'react-router-dom';
import Layout from '@/pages/Layout';
import LoginPage from '@/pages/LoginPage';
import ProfilePage from '@/pages/ProfilePage';
import UserPage from '@/pages/UserPage';
import ProductPage from '@/pages/ProductPage';
import BrandPage from './pages/BrandPage';
import CategoryPage from '@/pages/CategoryPage';
import useRefreshToken from './modules/user/hooks/useRefreshToken';
export default function App() {
  const token = useSelector((state: RootState) => state.auth.accessToken);
  const user = useSelector((state: RootState) => state.user.currentUser);
  const navigate = useNavigate();
  const [refreshLoading, setRefreshLoading] = useState(true);
  const refreshTokenMutation = useRefreshToken();
  useEffect(() => {
    const refresh = async () => {
      await refreshTokenMutation.mutateAsync();
      if (user) {
        navigate('/profile');
      }
      setRefreshLoading(false);
    };
    refresh();
  }, []);

  if (refreshLoading) {
    return <div>Refreshing session...</div>;
  }

  return (
    <Routes>
      <Route path="/" element={<Layout key={user} />}>
        {!token && <Route index element={<LoginPage />} />}
        {user && (
          <>
            <Route path="/profile" element={<ProfilePage />} />
            <Route path="/users" element={<UserPage />} />
            <Route path="/products" element={<ProductPage />} />
            <Route path="/brands" element={<BrandPage />} />
            <Route path="/categories" element={<CategoryPage />} />
          </>
        )}
      </Route>
    </Routes>
  );
}
