package org.sliga.usersmanagement.service;

import org.sliga.usersmanagement.security.JwtTokenProvider;
import org.sliga.usersmanagement.security.UserPrincipal;
import org.sliga.usersmanagement.utils.SecurityConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import static org.sliga.usersmanagement.utils.SecurityConstants.*;

@Component
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    public void authenticate(String username, String password){
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    public HttpHeaders getJwtHeaders(UserPrincipal userPrincipal){
        HttpHeaders jwtHeaders = new HttpHeaders();
        String token = jwtTokenProvider.generateJwtToken(userPrincipal);
        jwtHeaders.add(TOKEN_JWT_HEADER, token);
        return jwtHeaders;
    }
}
