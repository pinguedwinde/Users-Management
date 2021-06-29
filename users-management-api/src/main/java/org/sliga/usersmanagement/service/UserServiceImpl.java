package org.sliga.usersmanagement.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sliga.usersmanagement.exception.EmailExistException;
import org.sliga.usersmanagement.exception.EmailNotFoundException;
import org.sliga.usersmanagement.exception.UserNotFoundException;
import org.sliga.usersmanagement.exception.UsernameExistException;
import org.sliga.usersmanagement.model.User;
import org.sliga.usersmanagement.model.UserForm;
import org.sliga.usersmanagement.repository.UserRepository;
import org.sliga.usersmanagement.security.UserPrincipal;
import org.sliga.usersmanagement.utils.Role;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static org.sliga.usersmanagement.utils.AuthConstants.*;
import static org.sliga.usersmanagement.utils.FileConstants.*;
import static org.sliga.usersmanagement.utils.Role.ROLE_USER;

@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {
    private static final Log logger = LogFactory.getLog(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    public final LoginAttemptsService loginAttemptsService;
    public final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, LoginAttemptsService loginAttemptsService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptsService = loginAttemptsService;
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findUserByUsername(username);
        if (Objects.isNull(user)){
            String errorMessage = NO_USER_FOUND_BY_USERNAME + username;
            logger.error(errorMessage);
            throw new UsernameNotFoundException(errorMessage);
        }else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            this.userRepository.save(user);
            logger.info(FOUND_USER_BY_USERNAME + username);
            return new UserPrincipal(user);
        }
    }

    @Override
    public User register(UserForm registerUserForm) throws UserNotFoundException, EmailExistException, UsernameExistException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, registerUserForm.getUsername(), registerUserForm.getEmail());
        registerUserForm.setEnabled(true);
        registerUserForm.setNonLocked(true);
        registerUserForm.setRole(ROLE_USER.name());
        User user = buildUserFromUserForm(registerUserForm);
        User savedUser = this.userRepository.save(user);
        this.emailService.sendWelcomeEmail(savedUser.getFirstName(), savedUser.getUsername(), savedUser.getEmail());
        return savedUser;
    }

    @Override
    public User addNewUser(UserForm newUserForm) throws UserNotFoundException, EmailExistException, UsernameExistException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, newUserForm.getUsername(), newUserForm.getEmail());
        User user = buildUserFromUserForm(newUserForm);
        User savedUser = this.userRepository.save(user);
        //saveProfileImageUrl(user, profileImage);
        this.emailService.sendWelcomeEmail(savedUser.getFirstName(), savedUser.getUsername(), savedUser.getEmail());
        return savedUser;
    }

    @Override
    public User updateUser(UserForm updateUserForm) throws UserNotFoundException, EmailExistException, UsernameExistException {
        User currentUser = validateNewUsernameAndEmail(updateUserForm.getCurrentUsername(), updateUserForm.getUsername(), updateUserForm.getEmail())
                .orElse(new User());
        currentUser.setFirstName(updateUserForm.getFirstName());
        currentUser.setLastName(updateUserForm.getLastName());
        currentUser.setUsername(updateUserForm.getUsername());
        currentUser.setEmail(updateUserForm.getEmail());
        currentUser.setNonLocked(updateUserForm.isNonLocked());
        currentUser.setEnabled(updateUserForm.isEnabled());
        currentUser.setRole(updateUserForm.getRole());
        currentUser.setAuthorities(Arrays.asList(Role.getRoleByString(updateUserForm.getRole()).authorities));
        User updatedUser = this.userRepository.save(currentUser);
        this.emailService.sendUpdateUserEmail(updatedUser.getFirstName(), updatedUser.getUsername(), updatedUser.getEmail());
        return updatedUser;
    }

    @Override
    public User updateUserProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException {
        User user = validateNewUsernameAndEmail(username,null, null).orElseThrow();
        saveProfileImageUrl(user, profileImage);
        return null;
    }

    @Override
    public void deleteUser(Long id) {
        this.userRepository.deleteById(id);
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException {
        User user =  this.userRepository.findUserByEmail(email);
        if(Objects.isNull(user)){
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL);
        }
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepository.save(user);
        emailService.sendNewPasswordEmail(user.getFirstName(), user.getUsername(), user.getPassword(), user.getEmail());
    }

    @Override
    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return this.userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return this.userRepository.findUserByEmail(email);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    private void saveProfileImageUrl(User user, MultipartFile profileImage) throws IOException {
        if (profileImage != null){
            Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
            if (!Files.exists(userFolder)){
                Files.createDirectories(userFolder);
                logger.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + "." + JPEG_EXTENSION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUsername() + "." + JPEG_EXTENSION), StandardCopyOption.REPLACE_EXISTING);
            user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));
            userRepository.save(user);
            logger.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }

    private String setProfileImageUrl(String username){
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username
        + "/." + JPEG_EXTENSION).toUriString();
    }

    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    private void validateLoginAttempt(User user){
        if(user.isNonLocked()){
            if(loginAttemptsService.hasExceededMaxAttempts(user.getUsername())){
                user.setNonLocked(false);
            }
        }else{
            loginAttemptsService.evictUserFromLoginAttemptsCache(user.getUsername());
        }
    }

    private Optional<User> validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);

        if (StringUtils.isNotBlank(currentUsername)){
            User currentUser = findUserByUsername(currentUsername);
            if(Objects.isNull(currentUser)){
                throw new UserNotFoundException(USER_NOT_FOUND);
            }
            if(Objects.nonNull(userByNewUsername) && Objects.equals(currentUser.getId(), userByNewUsername.getId())){
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if(Objects.nonNull(userByNewEmail) && Objects.equals(currentUser.getId(), userByNewEmail.getId())){
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return Optional.of(currentUser);
        }else{
            if(Objects.nonNull(userByNewUsername)){
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if(Objects.nonNull(userByNewEmail)){
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return Optional.empty();
        }
    }

    private User buildUserFromUserForm(UserForm userForm){
        return User.builder()
                .userId(generateUserId())
                .firstName(userForm.getFirstName())
                .lastName(userForm.getLastName())
                .username(userForm.getUsername())
                .password(encodePassword(userForm.getPassword()))
                .email(userForm.getEmail())
                .joinDate(new Date())
                .isEnabled(userForm.isEnabled())
                .isNonLocked(userForm.isNonLocked())
                .role(userForm.getRole())
                .profileImageUrl(getTemporaryProfileImageUrl(userForm.getUsername()))
                .authorities(Arrays.asList(Role.getRoleByString(userForm.getRole()).authorities))
                .build();
    }
}
