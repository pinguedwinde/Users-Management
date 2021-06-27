package org.sliga.usersmanagement.controller;

import org.sliga.usersmanagement.exception.EmailExistException;
import org.sliga.usersmanagement.exception.ExceptionHandling;
import org.sliga.usersmanagement.exception.UserNotFoundException;
import org.sliga.usersmanagement.exception.UsernameExistException;
import org.sliga.usersmanagement.model.LoginForm;
import org.sliga.usersmanagement.model.RegistrationForm;
import org.sliga.usersmanagement.model.User;
import org.sliga.usersmanagement.security.UserPrincipal;
import org.sliga.usersmanagement.service.AuthService;
import org.sliga.usersmanagement.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping( path = {"/","/user"})
public class UserController extends ExceptionHandling {

    public final UserService userService;
    public final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping("/hello")
    public String helloUser() {
        return "Hello User";
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers(){
        return new ResponseEntity<>(this.userService.getAllUsers(), OK);
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody RegistrationForm registrationForm) throws UserNotFoundException, EmailExistException, UsernameExistException {
        User registeredUser = this.userService.register(registrationForm);
        return new ResponseEntity<>(registeredUser, CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody LoginForm loginForm){
        authService.authenticate(loginForm.getUsername(), loginForm.getPassword());
        User loggedInUser = userService.findUserByUsername(loginForm.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loggedInUser);
        HttpHeaders jwtHeaders = authService.getJwtHeaders(userPrincipal);
        return new ResponseEntity<>(loggedInUser, jwtHeaders, OK);

    }
}
