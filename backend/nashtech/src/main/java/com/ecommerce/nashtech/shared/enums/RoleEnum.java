package com.ecommerce.nashtech.shared.enums;

import com.ecommerce.nashtech.modules.account.model.Role;

import lombok.experimental.FieldDefaults;

public sealed interface RoleEnum
        permits RoleEnum.UserRole, RoleEnum.ModeratorRole, RoleEnum.AdminRole {

    @FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
    public static final class UserRole implements RoleEnum {
        static Role role = new Role(1L, "ROLE_USER");

        public static Role getRole() {
            return role;
        }

        public static long getId() {
            return role.getId();
        }

        public static String getName() {
            return role.getName();
        }
    }

    @FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
    public static final class ModeratorRole implements RoleEnum {
        static Role role = new Role(2L, "ROLE_MODERATOR");

        public static Role getRole() {
            return role;
        }

        public static long getId() {
            return role.getId();
        }

        public static String getName() {
            return role.getName();
        }
    }

    public static final class AdminRole implements RoleEnum {
        static Role role = new Role(3L, "ROLE_ADMIN");

        public static Role getRole() {
            return role;
        }

        public static long getId() {
            return role.getId();
        }

        public static String getName() {
            return role.getName();
        }
    }

    public static Role[] getAllRoles() {
        return new Role[] {
                UserRole.getRole(),
                ModeratorRole.getRole(),
                AdminRole.getRole()
        };
    }
}
