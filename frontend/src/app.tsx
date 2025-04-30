import './app.css';
import LogInForm from '@/modules/user/components/LogInForm';
import { RootState } from './store';
import { useCurrentUser } from './modules/user/hooks/useCurrentUser';
import { useSelector } from 'react-redux';

export default function App() {
  const token = useSelector((state: RootState) => state.auth.accessToken);
  const { data: user, isLoading } = useCurrentUser({ enabled: !!token, queryKey: ['currentUser'] });
  if (!token) return <LogInForm />;
  if (isLoading) return <div>Loading user...</div>;
  return <div>Welcome {user?.item.fullName}</div>;
}
