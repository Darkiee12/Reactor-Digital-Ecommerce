package com.ecommerce.nashtech.shared.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class PaginationConfiguration {
    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_SIZE = "10";
    public static final int MAX_SIZE = 100;  
}
