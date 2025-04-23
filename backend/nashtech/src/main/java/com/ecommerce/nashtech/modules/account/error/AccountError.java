package com.ecommerce.nashtech.modules.account.error;

import com.ecommerce.nashtech.shared.error.BaseError;
import com.ecommerce.nashtech.shared.types.Option;

public sealed abstract class AccountError extends BaseError permits
    AccountError.DuplicateUsernameError,
    AccountError.DuplicateEmailError,
    AccountError.WrongCredentialsError,
    AccountError.AccountNotFoundError,
    AccountError.NotValidEmail,
    AccountError.NotValidUsername,
    AccountError.NotValidPassword, 
    AccountError.ExpiredTokenError
{
    protected AccountError(String message, String code) {
        super(message, code);
    }

    public static final class DuplicateUsernameError extends AccountError {
        protected DuplicateUsernameError() {
            super("Invalid username", "ACCOUNT_100");
        }

        public static DuplicateUsernameError build(){
            return new DuplicateUsernameError();
        }
    }

    public static final class DuplicateEmailError extends AccountError {
        private DuplicateEmailError() {
            super("Invalid email", "ACCOUNT_101");
        }

        public static DuplicateEmailError build(){
            return new DuplicateEmailError();
        }
    }

    public static final class WrongCredentialsError extends AccountError {
        private WrongCredentialsError() {
            super("Invalid credentials", "ACCOUNT_102");
        }

        public static WrongCredentialsError build(){
            return new WrongCredentialsError();
        }
    }


    public static final class AccountNotFoundError extends AccountError {
        private final static String code = "ACCOUNT_103";
        private AccountNotFoundError(Option<String> username) {
            super(switch(username){
                case Option.Some<String> u -> "Account not found: " + u.get();
                case Option.None<String> n -> "Account not found";
            }, code);
            
        };

        public static AccountNotFoundError build(Option<String> username){
            return new AccountNotFoundError(username);
        }
    }

    public static final class NotValidEmail extends AccountError {
        private NotValidEmail() {
            super("Email is not valid", "ACCOUNT_104");
        }

        public static NotValidEmail build(){
            return new NotValidEmail();
        }
    }

    public static final class NotValidUsername extends AccountError {
        private NotValidUsername() {
            super("Username is not valid", "ACCOUNT_105");
        }

        public static NotValidUsername build(){
            return new NotValidUsername();
        }
    }

    public static final class NotValidPassword extends AccountError {
        private NotValidPassword() {
            super("Password is not valid", "ACCOUNT_106");
        }

        public static NotValidPassword build(){
            return new NotValidPassword();
        }
    }

    public static final class ExpiredTokenError extends AccountError {
        private ExpiredTokenError() {
            super("Credential token is expired", "ACCOUNT_107");
        }

        public static ExpiredTokenError build(){
            return new ExpiredTokenError();
        }
    }

    
}
