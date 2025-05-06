import { useMemo } from 'preact/hooks';
import { useForm } from 'react-hook-form';
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
import PhoneInput from 'react-phone-number-input';
import 'react-phone-number-input/style.css';
import { useSelector } from 'react-redux';
import { RootState } from '@/store';
import { User } from '../model/User';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import useUpdateUser from '../hooks/useUpdateUser';

const ProfileSegment = () => {
  const user = useSelector((state: RootState) => state.user.currentUser)!;
  const { mutate: updateUser, isPending, isSuccess, isError, error } = useUpdateUser();
  const parsedAddress = useMemo(() => {
    const parts = user.address.split(',').map(p => p.trim());
    const len = parts.length;

    return {
      address: parts.slice(0, len - 3).join(', ') || parts[0],
      city: len >= 4 ? parts[len - 3] : '',
      state: len >= 3 ? parts[len - 2] : '',
      country: parts[len - 1],
    };
  }, [user.address]);

  const form = useForm<User & { city: string; state: string; country: string }>({
    defaultValues: {
      fullName: user.fullName,
      email: user.email,
      gender: user.gender,
      phoneNumber: user.phoneNumber,
      ...parsedAddress,
    },
  });

  const onSubmit = async (data: User & { city: string; state: string; country: string }) => {
    updateUser({
      finder: {
        type: 'uuid',
        value: user.uuid,
      },
      user: {
        ...data,
        city: data.city,
        state: data.state,
        country: data.country,
      },
    });
  };
  return (
    <div id="profile" className="w-full px-8">
      <p className="text-2xl border-b border-b-[#3D444D] mb-4 pb-1">Public profile</p>
      <div className="w-full flex flex-wrap">
        <div className="w-full md:w-2/3 pr-8">
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="flex flex-col gap-y-5">
              <FormField
                control={form.control}
                name="fullName"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Full name</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="shadcn"
                        {...field}
                        className="focus-visible:border-2 focus-visible:border-blue-600 "
                      />
                    </FormControl>
                    <FormDescription>This is your full name.</FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="gender"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Gender</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="shadcn"
                        {...field}
                        className="focus-visible:border-2 focus-visible:border-blue-600"
                      />
                    </FormControl>
                    <FormDescription>This is your full name.</FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="phoneNumber"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Phone Number</FormLabel>
                    <FormControl className="text-white">
                      <PhoneInput
                        international
                        defaultCountry="VN"
                        {...field}
                        className="w-full max-h-10 focus-visible:border-2 focus-visible:border-blue-600"
                      />
                    </FormControl>
                    <FormDescription>
                      Delivery service will contact you via this phone number.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="address"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Address</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="Enter address here"
                        {...field}
                        className="focus-visible:border-2 focus-visible:border-blue-600"
                      />
                    </FormControl>
                    <FormDescription>
                      This address will be used for delivery purpose.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="city"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>City</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="Enter city here"
                        {...field}
                        className="focus-visible:border-2 focus-visible:border-blue-600"
                      />
                    </FormControl>
                    <FormDescription>
                      This address will be used for delivery purpose.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="state"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>State/Province</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="Enter state here"
                        {...field}
                        className="focus-visible:border-2 focus-visible:border-blue-600"
                      />
                    </FormControl>
                    <FormDescription>
                      This address will be used for delivery purpose.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="country"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Country</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="Enter country here"
                        {...field}
                        className="focus-visible:border-2 focus-visible:border-blue-600"
                      />
                    </FormControl>
                    <FormDescription>
                      This address will be used for delivery purpose.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <Button type="submit" variant="ghost" className="mt-1 bg-[#238636] px-3 py-3">
                Update Profile
              </Button>
            </form>
          </Form>
        </div>
        <div className="w-full md:w-1/3 px-2">
          <p>Profile picture</p>
          <div className="w-full rounded-lg flex items-center justify-center py-3">
            <Avatar className="bg-white w-full h-full aspect-auto">
              <AvatarImage src="/profile.png" alt="@shadcn" />
              <AvatarFallback>CN</AvatarFallback>
            </Avatar>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfileSegment;
