package com.ecommerce.nashtech.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Settings {
    public static class Username {
        public static final int MIN_LENGTH = 5;
        public static final int MAX_LENGTH = 32;
    }

    public static class Password {
        public static final int MIN_LENGTH = 8;
        public static final int MAX_LENGTH = 32;
    }

    public static class Pagination {
        public static final String DEFAULT_PAGE = "0";
        public static final String DEFAULT_SIZE = "10";
        public static final int MAX_SIZE = 100;
    }

    public static class Roles{
        public static final String DEFAUL_ROLE = "USER";
        public static final String ADMIN_ROLE = "ADMIN";
        public static final String MODERATOR_ROLE = "MODERATOR";
    }

    public static class Environment{
        @Value("${spring.config.activate.on-profile}")
        private static String env;


        public static boolean isProduction(){
            return env.equals("prod");
        }

        public static boolean isDevelopment(){
            return env.equals("dev");
        }
    }

    
}
