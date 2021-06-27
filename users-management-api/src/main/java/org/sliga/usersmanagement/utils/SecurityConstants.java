package org.sliga.usersmanagement.utils;

import java.time.Duration;

public class SecurityConstants {
    public static final long TOKEN_EXPIRATION_TIME = Duration.ofDays(5).toMillis();
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_JWT_HEADER = "Jwt-Token";
    public static final String TOKEN_EXPIRED_ERROR_MESSAGE = "The Jwt-Token is expired";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be not verified";
    public static final String TOKEN_AUDIENCE = "Users management portal";
    public static final String TOKEN_ISSUER = "Users Handling Corp, LLC";
    public static final String AUTHORITIES = "authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permissions to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS = {"**"};
    /*public static final String[] PUBLIC_URLS = {
            "/user/login", "/user/register", "/user/reset-password/**", "/user/image/**"
    };*/
}
