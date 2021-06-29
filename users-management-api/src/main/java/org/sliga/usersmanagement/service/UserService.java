package org.sliga.usersmanagement.service;

import org.sliga.usersmanagement.exception.EmailExistException;
import org.sliga.usersmanagement.exception.EmailNotFoundException;
import org.sliga.usersmanagement.exception.UserNotFoundException;
import org.sliga.usersmanagement.exception.UsernameExistException;
import org.sliga.usersmanagement.model.User;
import org.sliga.usersmanagement.model.UserForm;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    User register(UserForm registerUserForm) throws UserNotFoundException, EmailExistException, UsernameExistException;
    List<User> getAllUsers();
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User addNewUser(UserForm newUserForm) throws UserNotFoundException, EmailExistException, UsernameExistException;
    User updateUser(UserForm updateUserForm) throws UserNotFoundException, EmailExistException, UsernameExistException;
    User updateUserProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException;
    void deleteUser(Long id);
    void resetPassword(String email) throws EmailNotFoundException;
}
