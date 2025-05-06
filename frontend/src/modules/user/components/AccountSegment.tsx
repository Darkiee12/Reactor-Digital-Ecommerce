import { useEffect, useState } from 'preact/hooks';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import {
  Form,
  FormField,
  FormControl,
  FormLabel,
  FormDescription,
  FormMessage,
  FormItem,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import useUniqueCheck from '@/modules/user/hooks/useUniqueCheck';
import { RootState } from '@/store';
import { useSelector } from 'react-redux';
import useUpdateUser from '../hooks/useUpdateUser';
import { zodResolver } from '@hookform/resolvers/zod';

const AccountSegment = () => {
  return (
    <div className="px-8 flex flex-col gap-y-5 w-full">
      <AccountIdentity />
      <AccountAmendmentSegment />
      <PasswordAmendmentSegment />
    </div>
  );
};

const AccountIdentity = () => {
  const user = useSelector((state: RootState) => state.user.currentUser)!;
  return (
    <div id="account" className="w-full">
      <p className="text-2xl border-b border-b-[#3D444D] pb-2">Your account identity</p>
      <p>UUID: {user.uuid}</p>
    </div>
  );
};

const AccountAmendmentSegment = () => {
  const user = useSelector((state: RootState) => state.user.currentUser)!;
  const { mutate: updateUser, isPending, isSuccess, isError, error } = useUpdateUser();
  const defaultValues = {
    username: user.username,
    email: user.email,
  };

  const form = useForm<{ username: string; email: string }>({ defaultValues });
  const [isFormChanged, setIsFormChanged] = useState(false);

  const formSchema = z.object({
    username: z.string().min(2).max(50),
    email: z.string().email(),
  });

  const { data: isUsernameUnique } = useUniqueCheck({
    type: 'username',
    value: form.watch('username'),
  });
  const { data: isEmailUnique } = useUniqueCheck({ type: 'email', value: form.watch('email') });

  useEffect(() => {
    const currentValues = form.getValues();
    const hasChanged =
      currentValues.username !== defaultValues.username ||
      currentValues.email !== defaultValues.email;
    setIsFormChanged(hasChanged);
  }, [form.watch('username'), form.watch('email')]);

  const onSubmit = (values: z.infer<typeof formSchema>) => {
    updateUser({
      finder: {
        type: 'uuid',
        value: user.uuid,
      },
      user: {
        username: values.username,
        email: values.email,
      },
    });
  };

  return (
    <div id="account" className="w-full">
      <p className="text-2xl border-b border-b-[#3D444D] pb-2">Your account information</p>
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)}>
          <FormField
            control={form.control}
            name="username"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Username</FormLabel>
                <FormControl>
                  <Input {...field} placeholder="shadcn" />
                </FormControl>
                <FormDescription className="text-sm text-red-500">
                  {!isUsernameUnique && 'Username is already taken'}
                </FormDescription>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="email"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Email</FormLabel>
                <FormControl>
                  <Input {...field} placeholder="shadcn@example.com" />
                </FormControl>
                <FormDescription className="text-sm text-red-500">
                  {!isEmailUnique && 'Email is already taken'}
                </FormDescription>
                <FormMessage />
              </FormItem>
            )}
          />
          <Button
            type="submit"
            variant="ghost"
            className="mt-1 bg-[#238636] px-3 py-3"
            disabled={!isFormChanged}
          >
            Update Account
          </Button>
        </form>
      </Form>
    </div>
  );
};

const passwordSchema = z
  .object({
    password: z.string().min(8, 'Must be at least 8 characters').max(32),
    newPassword: z.string().min(8, 'Must be at least 8 characters').max(32),
    newPasswordConfirm: z.string().min(8).max(32),
  })
  .refine(data => data.newPassword === data.newPasswordConfirm, {
    message: 'Passwords do not match',
    path: ['newPasswordConfirm'],
  });

type PasswordFormValues = z.infer<typeof passwordSchema>;

export const PasswordAmendmentSegment = () => {
  const user = useSelector((state: RootState) => state.user.currentUser)!;
  const { mutate: updateUser, isPending, isError, error } = useUpdateUser();

  const form = useForm<PasswordFormValues>({
    resolver: zodResolver(passwordSchema),
    defaultValues: {
      password: '',
      newPassword: '',
      newPasswordConfirm: '',
    },
  });

  const onSubmit = (values: PasswordFormValues) => {
    updateUser({
      finder: { type: 'uuid', value: user.uuid },
      user: { password: values.newPassword },
    });
  };

  return (
    <div className="w-full">
      <p className="text-2xl border-b border-b-[#3D444D] pb-2">Change Password</p>
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="">
          <FormField
            control={form.control}
            name="password"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Current Password</FormLabel>
                <FormControl>
                  <Input type="password" placeholder="Current Password" {...field} />
                </FormControl>
                <FormDescription className="text-sm text-red-500">
                  {form.formState.errors.password?.message ?? '\u00A0'}
                </FormDescription>
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="newPassword"
            render={({ field }) => (
              <FormItem>
                <FormLabel>New Password</FormLabel>
                <FormControl>
                  <Input type="password" placeholder="New Password" {...field} />
                </FormControl>
                <FormDescription className="text-sm text-red-500">
                  {form.formState.errors.newPassword?.message ?? '\u00A0'}
                </FormDescription>
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="newPasswordConfirm"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Confirm New Password</FormLabel>
                <FormControl>
                  <Input type="password" placeholder="Confirm New Password" {...field} />
                </FormControl>
                <FormDescription className="text-sm text-red-500">
                  {form.formState.errors.newPasswordConfirm?.message ?? '\u00A0'}
                </FormDescription>
              </FormItem>
            )}
          />

          {isError && (
            <p className="text-sm text-red-500">
              {(error as { message?: string })?.message || 'An error occurred'}
            </p>
          )}

          <Button
            type="submit"
            variant="ghost"
            className="mt-1 bg-[#238636] px-3 py-3"
            disabled={isPending}
          >
            {isPending ? 'Updating...' : 'Update Password'}
          </Button>
        </form>
      </Form>
    </div>
  );
};

export default AccountSegment;
