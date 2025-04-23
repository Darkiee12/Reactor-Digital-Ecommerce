package com.ecommerce.nashtech.shared.config;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
public class ProfileEnvironment {

    private final Environment environment;

    public ProfileEnvironment(Environment environment) {
        this.environment = environment;
    }

    public boolean isProduction() {
        return environment.acceptsProfiles(Profiles.of("prod"));
    }

    public boolean isDevelopment() {
        return environment.acceptsProfiles(Profiles.of("dev"));
    }

    public boolean isTest() {
        return environment.acceptsProfiles(Profiles.of("test"));
    }

    public String getActiveProfile() {
        String[] profiles = environment.getActiveProfiles();
        return profiles.length > 0 ? profiles[0] : "default";
    }
}
