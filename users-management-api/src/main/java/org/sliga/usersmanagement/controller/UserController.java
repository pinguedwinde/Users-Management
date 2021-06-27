package org.sliga.usersmanagement.controller;

import com.auth0.jwt.exceptions.TokenExpiredException;
import org.sliga.usersmanagement.exception.EmailExistException;
import org.sliga.usersmanagement.exception.ExceptionHandling;
import org.sliga.usersmanagement.security.SecurityConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController extends ExceptionHandling {

    @GetMapping("/hello")
    public String helloUser() throws EmailExistException {
        throw new TokenExpiredException(SecurityConstants.TOKEN_EXPIRED_ERROR_MESSAGE);
        //return "Hello User";
    }
}
