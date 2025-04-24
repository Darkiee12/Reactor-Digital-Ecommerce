package com.ecommerce.nashtech.security.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.ecommerce.nashtech.security.jwt.AuthTokenWebFilter;
import com.ecommerce.nashtech.security.jwt.JwtAuthEntryPoint;
import com.ecommerce.nashtech.security.user.AccountDetailsService;
import static org.springframework.security.config.Customizer.withDefaults;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class SecurityConfig {

    AccountDetailsService userDetailsService;
    JwtAuthEntryPoint authEntryPoint;
    AuthTokenWebFilter authTokenWebFilter;

    private static final String[] PERMITTED_URLS = {
        "/api/v1/account/login", 
        "/api/v1/account/register",
        "/api/v1/users",
        "/swagger-ui.html",
        "/swagger-ui/**", 
        "/v3/api-docs/**",
        "/v3/api-docs", 
        "/v3/api-docs/swagger-config"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(PasswordEncoder passwordEncoder) {
        UserDetailsRepositoryReactiveAuthenticationManager authManager = new UserDetailsRepositoryReactiveAuthenticationManager(
                userDetailsService);
        authManager.setPasswordEncoder(passwordEncoder);
        return authManager;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
            ReactiveAuthenticationManager authManager) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPoint))
                .requestCache((requestCache) -> NoOpServerRequestCache.getInstance())
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authenticationManager(authManager)
                .httpBasic(withDefaults())
                .formLogin(form -> form.loginPage("/swagger-ui.html"))
                .authorizeExchange(
                        exchanges -> exchanges.pathMatchers(PERMITTED_URLS).permitAll().anyExchange().authenticated())
                .addFilterAt(authTokenWebFilter, SecurityWebFiltersOrder.AUTHENTICATION).build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
