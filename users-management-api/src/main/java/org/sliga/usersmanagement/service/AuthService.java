package org.sliga.usersmanagement.service;

import org.sliga.usersmanagement.model.LoginForm;
import org.sliga.usersmanagement.model.User;
import org.sliga.usersmanagement.security.JwtTokenProvider;
import org.sliga.usersmanagement.security.UserPrincipal;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import static org.sliga.usersmanagement.utils.SecurityConstants.TOKEN_JWT_HEADER;

@Service
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    public final UserService userService;


    public AuthService(JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager, UserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
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

    private void authenticate(String username, String password){
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
