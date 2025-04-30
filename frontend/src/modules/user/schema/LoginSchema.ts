import { object, string } from 'zod';

const loginSchema = object({
  username: string().min(3, 'Username is required'),
  password: string()
    .min(1, 'Password is required')
    .min(8, 'Password must be more than 8 characters')
    .max(32, 'Password must be less than 32 characters'),
});

export default loginSchema;
