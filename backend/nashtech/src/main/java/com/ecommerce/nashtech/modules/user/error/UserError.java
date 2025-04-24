package com.ecommerce.nashtech.modules.user.error;

import com.ecommerce.nashtech.shared.error.BaseError;
import com.ecommerce.nashtech.shared.types.Option;


public sealed abstract class UserError extends BaseError permits 
    UserError.UserNotFoundError, 
    UserError.PhoneNumberValidationError, 
    UserError.AddressValidationError{

    protected UserError(String message, String code) {
        super(message, code);
    }

    public static final class UserNotFoundError extends UserError {
        private final static String code = "USER_100";
        private UserNotFoundError(Option<String> username) {
            super(
                switch (username) {
                    case Option.Some<String> u -> "User not found: " + u.get();
                    case Option.None<String> n -> "User not found";
                },
                code
            );
        }

        public static UserNotFoundError build(Option<String> username){
            return new UserNotFoundError(username);
        }
    }

    public static final class PhoneNumberValidationError extends UserError {
        private final static String code = "USER_101";
        private PhoneNumberValidationError(Option<String> phoneNumber) {
            super(
                switch (phoneNumber) {
                    case Option.Some<String> p -> "Phone number is invalid: " + p.get();
                    case Option.None<String> n -> "Phone number is invalid";
                },
                code
            );
        }
        

        public static PhoneNumberValidationError build(Option<String> phoneNumber){
            return new PhoneNumberValidationError(phoneNumber);
        }
    }

    public static final class AddressValidationError extends UserError {
        private final static String code = "USER_102";
        private AddressValidationError(String address) {
            super("Address is invalid: " + address, code);
        }

        public static AddressValidationError build(String address){
            return new AddressValidationError(address);
        }
    }
    
}

