import LogInForm from '@/modules/user/components/LogInForm';

const LoginPage = () => {
  return (
    <div className="w-full h-full justify-center items-center flex p-3">
      <div className="w-full max-w-md">
        <LogInForm />
      </div>
    </div>
  );
};

export default LoginPage;
