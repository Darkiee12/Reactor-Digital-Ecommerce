import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import ErrorResponse from '@/modules/error/model/Error';

const initialState: ErrorResponse = {
  instance: '',
  timestamp: '',
  message: '',
  code: '',
};

export const errorSlice = createSlice({
  name: 'error',
  initialState,
  reducers: {
    setError: (state, action: PayloadAction<ErrorResponse>) => {
      state.message = action.payload.message;
      state.code = action.payload.code;
      state.instance = action.payload.instance;
      state.timestamp = action.payload.timestamp;
    },
    clearError: state => {
      (state.message = ''), (state.code = '');
    },
  },
});

export const { setError, clearError } = errorSlice.actions;

export default errorSlice.reducer;
