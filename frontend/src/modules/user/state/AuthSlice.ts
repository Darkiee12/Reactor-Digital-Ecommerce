import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import AccessToken from '@/modules/user/model/AccessToken';

const initialState: AccessToken = {
  accessToken: '',
};

export const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setAccessToken(state, action: PayloadAction<AccessToken>) {
      state.accessToken = action.payload.accessToken;
    },
    clearAccessToken(state) {
      state.accessToken = '';
    },
  },
});

export const { setAccessToken, clearAccessToken } = authSlice.actions;
export default authSlice.reducer;
