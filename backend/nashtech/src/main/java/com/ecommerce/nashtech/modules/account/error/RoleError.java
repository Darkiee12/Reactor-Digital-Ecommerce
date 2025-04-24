package com.ecommerce.nashtech.modules.account.error;

import com.ecommerce.nashtech.modules.account.error.RoleError.DuplicateRoleError;
import com.ecommerce.nashtech.shared.error.BaseError;

public sealed abstract class RoleError extends BaseError permits 
    RoleError.DefaultRoleNotFoundError, DuplicateRoleError
{
    
    protected RoleError(String message, String code) {
        super(message, code);
    }

    public static final class DefaultRoleNotFoundError extends RoleError {
        private DefaultRoleNotFoundError() {
            super("Default role not found", "ROLE_100");
        }

        public static DefaultRoleNotFoundError build(){
            return new DefaultRoleNotFoundError();
        }
    }

    public static final class DuplicateRoleError extends RoleError {
        public DuplicateRoleError() {
            super("Role exists", "ROLE_101");
        }

        public static DuplicateRoleError build(){
            return new DuplicateRoleError();
        }
    }


}
