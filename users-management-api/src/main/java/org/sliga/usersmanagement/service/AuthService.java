package org.sliga.usersmanagement.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.sliga.usersmanagement.model.LoginForm;
import org.sliga.usersmanagement.model.User;
import org.sliga.usersmanagement.security.JwtTokenProvider;
import org.sliga.usersmanagement.security.UserPrincipal;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.sliga.usersmanagement.utils.AuthConstants.MAXIMUM_NUMBER_OF_ATTEMPTS;
import static org.sliga.usersmanagement.utils.SecurityConstants.*;

@Service
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    public final UserService userService;

    public LoadingCache<String, Integer> loginAttemptsCache;


    public AuthService(JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager, UserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.loginAttemptsCache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(200)
                .build(
                        new CacheLoader<String, Integer>() {
                            @Override
                            public Integer load(String key) throws Exception {
                                return 0;
                            }
                        }
                );
    }

    public User loginUser(LoginForm loginForm){
        authenticate(loginForm.getUsername(), loginForm.getPassword());
        return userService.findUserByUsername(loginForm.getUsername());
    }

    public HttpHeaders getJwtHeaders(UserPrincipal userPrincipal){
        HttpHeaders jwtHeaders = new HttpHeaders();
        String token = jwtTokenProvider.generateJwtToken(userPrincipal);
        jwtHeaders.add(TOKEN_JWT_HEADER, token);
        return jwtHeaders;
    }

    public void evictUserFromLoginAttemptsCache(String username){
        loginAttemptsCache.invalidate(username);
    }

    public void addUserToLoginAttemptsCache(String username) throws ExecutionException{
        int attempts = 1 + loginAttemptsCache.get(username);
        loginAttemptsCache.put(username, attempts);
    }

    public boolean hasExceededMaxAttempts(String username) throws ExecutionException {
        return loginAttemptsCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
    }

    private void authenticate(String username, String password){
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
