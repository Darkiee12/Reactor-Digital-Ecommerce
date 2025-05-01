package com.ecommerce.nashtech.shared.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RoleConfiguration {
    public static final String DEFAULT_ROLE = "USER";
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String MODERATOR_ROLE = "MODERATOR";
}
