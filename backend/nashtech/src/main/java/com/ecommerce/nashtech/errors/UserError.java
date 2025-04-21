package com.ecommerce.nashtech.errors;

import com.ecommerce.nashtech.errors.UserError.AddressValidationError;
import com.ecommerce.nashtech.errors.UserError.PhoneNumberValidationError;
import com.ecommerce.nashtech.errors.UserError.UserNotFoundError;
import com.ecommerce.nashtech.utils.rust.Option;

import lombok.Getter;

@Getter
public sealed abstract class UserError extends AccountError permits UserNotFoundError, PhoneNumberValidationError, AddressValidationError{

    private final String message;
    private final String code;
    

    protected UserError(String message, String code) {
        super(message, code);
        this.message = message;
        this.code = code;
    }



    public static final class UserNotFoundError extends UserError {
        private final static String code = "USER_100";
        private UserNotFoundError(String username) {
            super("User not found: " + username, code);
        }

        public static UserNotFoundError build(String username){
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
