import { configureStore } from '@reduxjs/toolkit';
import { authSlice } from '@/modules/user/state/AuthSlice';
import { errorSlice } from '@/modules/error/state/ErrorSlice';
import { userSlice } from '@/modules/user/state/UserSlice';
import { useDispatch } from 'react-redux';

const store = configureStore({
  reducer: {
    auth: authSlice.reducer,
    error: errorSlice.reducer,
    user: userSlice.reducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
export const useAppDispatch = () => useDispatch<AppDispatch>();
export default store;
