package org.sliga.usersmanagement.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sliga.usersmanagement.exception.EmailExistException;
import org.sliga.usersmanagement.exception.UserNotFoundException;
import org.sliga.usersmanagement.exception.UsernameExistException;
import org.sliga.usersmanagement.model.RegistrationForm;
import org.sliga.usersmanagement.model.User;
import org.sliga.usersmanagement.repository.UserRepository;
import org.sliga.usersmanagement.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;

import static org.sliga.usersmanagement.exception.ExceptionHandling.*;
import static org.sliga.usersmanagement.security.utils.Role.ROLE_USER;

@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserService implements UserServiceInterface, UserDetailsService {
    private static final Log logger = LogFactory.getLog(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findUserByUsername(username);
        if (Objects.isNull(user)){
            String errorMessage = "User not found by username : " + username;
            logger.error(errorMessage);
            throw new UsernameNotFoundException(errorMessage);
        }else {
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            this.userRepository.save(user);
            logger.info("Returning found User by username : " + username);
            return new UserPrincipal(user);
        }
    }

    @Override
    public User register(RegistrationForm registrationForm) throws UserNotFoundException, EmailExistException, UsernameExistException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, registrationForm.getUsername(), registrationForm.getEmail());
        User user = User.builder()
                .userId(generateUserId())
                .password(encodePassword(registrationForm.getPassword()))
                .firstName(registrationForm.getFirstName())
                .lastName(registrationForm.getLastName())
                .username(registrationForm.getUsername())
                .email(registrationForm.getEmail())
                .joinDate(new Date())
                .isEnabled(true)
                .isNonLocked(true)
                .role(ROLE_USER.name())
                .authorities(Arrays.asList(ROLE_USER.authorities))
                .profileImageUrl(getTemporaryUrl())
                .build();
        return this.userRepository.save(user);
    }


    @Override
    public List<User> getAllUsers() {
        return null;
    }

    @Override
    public User findUserByUsername(String username) {
        return null;
    }

    @Override
    public User findUserByEmail(String email) {
        return null;
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private String getTemporaryUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("user/profile/image/temp").toUriString();
    }

    private Optional<User> validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        if (StringUtils.isNotBlank(currentUsername)){
            User currentUser = findUserByUsername(currentUsername);
            if(Objects.isNull(currentUser)){
                throw new UserNotFoundException(USER_NOT_FOUND);
            }
            User userByNewUsername = findUserByUsername(newUsername);
            if(Objects.nonNull(userByNewUsername) && Objects.equals(currentUser.getId(), userByNewUsername.getId())){
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            User userByNewEmail = findUserByUsername(newEmail);
            if(Objects.nonNull(userByNewEmail) && Objects.equals(currentUser.getId(), userByNewEmail.getId())){
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return Optional.of(currentUser);
        }else{
            User userByNewUsername = findUserByUsername(newUsername);
            if(Objects.nonNull(userByNewUsername)){
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            User userByNewEmail = findUserByUsername(newEmail);
            if(Objects.nonNull(userByNewEmail)){
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return Optional.empty();
        }
    }
}
