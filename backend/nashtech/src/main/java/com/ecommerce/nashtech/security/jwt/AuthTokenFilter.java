package com.ecommerce.nashtech.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ecommerce.nashtech.security.jwt.JwtUtils.JwtParserException;
import com.ecommerce.nashtech.security.user.AccountDetailsService;
import com.ecommerce.nashtech.utils.rust.Result;

import java.io.IOException;
import java.util.function.Function;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AccountDetailsService accountDetailsService;

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        System.out.println("In AuthTokenFilter");

        String jwt = parseJwt(request);
        if (StringUtils.hasText(jwt) && jwtUtils.validateToken(jwt)) {
            var username = jwtUtils.getUsernameFromToken(jwt);
            switch (username) {
            case Result.Ok<String, JwtUtils.JwtParserException> okUName -> {
                var uname = okUName.get();
                var accountDetails = accountDetailsService.loadUserByUsername(uname);
                var auth = new UsernamePasswordAuthenticationToken(accountDetails, null,
                        accountDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            case Result.Err<String, JwtUtils.JwtParserException> err -> {
                Function<JwtUtils.JwtParserException, Void> errOutput = (e) -> {
                    try {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        response.getWriter().write(e.exception.getMessage());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    return null;
                };
                JwtParserException error = err.get();
                switch (error) {
                case JwtParserException.UtilsJwtException e -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write(
                            e.exception.getMessage() + ": Invalid or expired token, you may login and try again!");
                    filterChain.doFilter(request, response);
                    return;
                }
                case JwtParserException.UtilsUnsupportedJwtException e -> {
                    errOutput.apply(e);
                    return;
                }
                case JwtParserException.UtilsIllegalArgumentException e -> {
                    errOutput.apply(e);
                    return;
                }
                default -> {
                }
                }
            }
            }
        }

        else {
            System.out.println("No JWT or invalid token, allowing access to the controller");
            filterChain.doFilter(request, response); // Allow access to the controller if no JWT or invalid token
            return;
        }

        // After processing the JWT, continue the filter chain
        filterChain.doFilter(request, response);
    }

}