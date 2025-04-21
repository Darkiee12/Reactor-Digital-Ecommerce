import { UUID } from "node:crypto";
import Result from "@/utils/Result";
export type AccountType = {
    uuid: UUID
    username: string
    email: string
    createdAt: number
    jwt?: string
}

export type Credential = Pick<AccountType, "username"> & { password: string }; 
export type CreateAccount = Pick<AccountType, "username" | "email" > & { password: string };

export default class Account{
    
    private readonly account: AccountType;
    private constructor(account: AccountType) {
        this.account = account;
    }

    static from(account: AccountType): Result<Account>{
        if(!account.email){
            return Result.Err("No email provided");
        }
        if(!account.username){
            return Result.Err("No username provided");
        }
        if(!account.uuid){
            return Result.Err("No uuid provided");
        }
        if(!account.createdAt){
            return Result.Err("No createdAt provided");
        }
        return Result.Ok(new Account(account));

        
    }

    get(): AccountType{
        return this.account;
    }
}