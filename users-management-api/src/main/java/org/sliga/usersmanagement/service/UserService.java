package org.sliga.usersmanagement.service;

import org.sliga.usersmanagement.exception.EmailExistException;
import org.sliga.usersmanagement.exception.UserNotFoundException;
import org.sliga.usersmanagement.exception.UsernameExistException;
import org.sliga.usersmanagement.model.RegistrationForm;
import org.sliga.usersmanagement.model.User;

import java.util.List;

public interface UserService {
    User register(RegistrationForm registrationForm) throws UserNotFoundException, EmailExistException, UsernameExistException;
    List<User> getAllUsers();
    User findUserByUsername(String username);
    User findUserByEmail(String email);
}
