import { RootState } from '@/store';
import { useSelector } from 'react-redux';
import { Controller, useForm } from 'react-hook-form';
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import PhoneInput from 'react-phone-number-input';
import { Input } from '@/components/ui/input';
import { User } from '@/modules/user/model/User';
import 'react-phone-number-input/style.css';
import { Button } from '@/components/ui/button';

const ProfilePage = () => {
  const user = useSelector((state: RootState) => state.user.currentUser)!;
  const form = useForm<User>({
    defaultValues: {
      username: user.username,
      fullName: user.fullName,
      email: user.email,
      gender: user.gender,
      phoneNumber: user.phoneNumber,
      address: user.address,
    },
  });

  const onSubmit = (data: User) => {
    console.log('Form data:', data);
  };
  return (
    <div className="w-full flex">
      <div className="w-2/3">
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)}>
            <FormField
              control={form.control}
              name="username"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Username</FormLabel>
                  <FormControl>
                    <Input
                      placeholder="shadcn"
                      {...field}
                      className="focus-visible:border-2 focus-visible:border-blue-600 bg-[#151b23]"
                    />
                  </FormControl>
                  <FormDescription>This is your public display name.</FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />
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
                      className="focus-visible:border-2 focus-visible:border-blue-600 bg-[#151b23]"
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
                  <FormControl className="text-black">
                    <PhoneInput
                      international
                      defaultCountry="VN"
                      {...field}
                      className="w-full max-h-10 focus-visible:border-2 focus-visible:border-blue-600 bg-[#151b23]"
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
                      className="focus-visible:border-2 focus-visible:border-blue-600 bg-[#151b23]"
                    />
                  </FormControl>
                  <FormDescription>This address will be used for delivery purpose.</FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />
            <Button type="submit" className="bg-[#238636] px-3 py-3 hover:brightness-110">
              Update Profile
            </Button>
          </form>
        </Form>
      </div>
    </div>
  );
};

export default ProfilePage;
