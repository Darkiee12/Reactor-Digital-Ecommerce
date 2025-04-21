import { LoginForm } from "@/components/login-form"
import Account from "@/models/Account"
import { useState } from "preact/hooks"
export default function Page() {
  const [account, setAccount] = useState<Account | null>(null);

  const noAccount = () => {
    return(
      <div className="flex min-h-svh w-full items-center justify-center p-6 md:p-10">
      <div className="w-full max-w-sm">
        <LoginForm setAccount={setAccount} />
      </div>
    </div>
    )
  }

  const hasAccount = (account: Account) => {
    return(
      <div className="w-full text-center">
        <p className="text-3xl font-bold">Welcome back, {account.get().username}</p>
        <p className="text-gray-500">Your UUID is {account.get().uuid}</p>
      </div>
    )
  }
  return (
    <>
      {account ? hasAccount(account) : noAccount()}
    </>
  )
}


