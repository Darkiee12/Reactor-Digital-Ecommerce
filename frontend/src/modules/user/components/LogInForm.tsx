import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import React, { useState } from 'preact/compat';
import { h } from 'preact';
import { useSelector } from 'react-redux';
import { RootState } from '@/store';
import UserService from '@/modules/user/service/UserApi';
import { useMutation, useQuery } from '@tanstack/react-query';
import LoginInput from '@/modules/user/model/LoginInput';
import DataResponse from '@/shared/response/model/DataResponse';
import AccessToken from '@/modules/user/model/AccessToken';
import ErrorResponse from '@/modules/error/model/Error';
import { AxiosError } from 'axios';
import { useLogin } from '../hooks/useLogin';
import loginSchema from '../schema/LoginSchema';
import { useCurrentUser } from '../hooks/useCurrentUser';
const LogInForm = ({ className, ...props }: React.ComponentPropsWithoutRef<'div'>) => {
  const [username, setUsername] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const error = useSelector((state: RootState) => state.error);
  const loginMutation = useLogin();
  const currentUser = useCurrentUser();

  const handleUsernameChange = (e: h.JSX.TargetedEvent<HTMLInputElement, Event>) => {
    setUsername(e.currentTarget.value);
  };

  const handlePasswordChange = (e: h.JSX.TargetedEvent<HTMLInputElement, Event>) => {
    setPassword(e.currentTarget.value);
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const result = loginSchema.safeParse({
      username: username,
      password: password,
    });
    if (result.success) {
      loginMutation.mutate({ username, password });
      currentUser;
    }
  };

  return (
    <div className={cn('flex flex-col gap-6', className)} {...props}>
      <Card>
        <CardHeader>
          <CardTitle>Login to your account</CardTitle>
          <CardDescription>
            Enter your username and password below to login to your account
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit}>
            <div className="flex flex-col gap-6">
              <div className="grid gap-3">
                <Label htmlFor="username">Username</Label>
                <Input
                  id="username"
                  type="text"
                  placeholder="your username here"
                  required
                  value={username}
                  onChange={handleUsernameChange}
                />
              </div>
              <div className="grid gap-3">
                <div className="flex items-center">
                  <Label htmlFor="password">Password</Label>
                  <a
                    href="#"
                    className="ml-auto inline-block text-sm underline-offset-4 hover:underline"
                  >
                    Forgot your password?
                  </a>
                </div>
                <Input
                  id="password"
                  type="password"
                  required
                  value={password}
                  onChange={handlePasswordChange}
                />
              </div>
              <div className="flex flex-col gap-3">
                <Button type="submit" className="w-full">
                  Login
                </Button>
                <Button variant="outline" className="w-full">
                  Login with Google
                </Button>
              </div>
            </div>
            <div className="mt-4 text-center text-sm">
              Don&apos;t have an account?{' '}
              <a href="#" className="underline underline-offset-4">
                Sign up
              </a>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};

export default LogInForm;
