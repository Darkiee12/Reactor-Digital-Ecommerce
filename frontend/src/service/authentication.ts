import Account, { AccountType, CreateAccount, Credential } from '@/models/Account';
import Result from '@/utils/Result';
import axios from 'axios';

const client = axios.create({
    // baseURL: import.meta.env.VITE_API+"api/v1"
    baseURL: "http://localhost:8080/api/v1"
});
const config = {
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
    }
}


const login = async(data: Credential): Promise<Result<Account>> => {
    try{
        const response = await client.post<AccountType>("/account/login", data, config);
        if(response.status === 200){
            
            const account = Account.from(response.data);
            return account;
        } else{
            return Result.Err(response.statusText);
        }

    } catch (error){
        if (axios.isAxiosError(error)) {
            return Result.Err(error.message);
        } else {
            return Result.Err("Unknown error");
        }
    }
}

const register = async(data: CreateAccount): Promise<Result<string>> => {
    try{
        const response = await client.post<string>("/account/register", data);
        return Result.Ok(response.data);

    }
    catch(e){
        if (axios.isAxiosError(e)) {
            return Result.Err(e.message);
        } else {
            return Result.Err("Unknown error");
        }
    }
}


  


const AuthenticationService = {
    login,
    register
}

export default AuthenticationService