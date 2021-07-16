package org.sliga.usersmanagement.controller;

import org.sliga.usersmanagement.controller.response.HttpResponse;
import org.sliga.usersmanagement.exception.*;
import org.sliga.usersmanagement.exception.domain.*;
import org.sliga.usersmanagement.model.User;
import org.sliga.usersmanagement.model.UserForm;
import org.sliga.usersmanagement.security.UserPrincipal;
import org.sliga.usersmanagement.service.AuthService;
import org.sliga.usersmanagement.service.UserService;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

import static org.sliga.usersmanagement.utils.EmailConstants.EMAIL_SENT_NOTIFICATION;
import static org.sliga.usersmanagement.utils.FileConstants.TEMP_IMAGE_BASE_URL;
import static org.sliga.usersmanagement.utils.FileConstants.USER_FOLDER;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

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

    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable( "username") String username){
        return new ResponseEntity<>(this.userService.findUserByUsername(username), OK);
    }

    @GetMapping("/reset-password/{email}")
    public ResponseEntity<HttpResponse> resetUserPassword(@PathVariable("email") String email) throws EmailNotFoundException {
        this.userService.resetPassword(email);
        return new ResponseEntity<>(response(NO_CONTENT, EMAIL_SENT_NOTIFICATION + email), OK);
    }

    @GetMapping(path = "/image/{username}/{filename}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getProfileImage(@PathVariable("username") String username,
                                                  @PathVariable("filename") String filename ) throws IOException {
        byte[] profileImage = Files.readAllBytes(Paths.get(USER_FOLDER + username + "/" + filename));
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        return new ResponseEntity<>(profileImage, headers, OK);
    }

    @GetMapping(path = "/profile/image/{username}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getTemporaryProfileImage(@PathVariable("username") String username) throws IOException {
        URL imageUrl = new URL(TEMP_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(InputStream inputStream = imageUrl.openStream()){
            int bytesRead;
            byte[] buffer = new byte[512];
            while( (bytesRead = inputStream.read(buffer)) > 0){
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
        }
        byte[] profileImage = byteArrayOutputStream.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        return new ResponseEntity<>(profileImage, headers, OK);
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserForm registerUserForm) throws UserNotFoundException, EmailExistException, UsernameExistException {
        User registeredUser = this.userService.register(registerUserForm);
        return new ResponseEntity<>(registeredUser, CREATED);
    }

    @PostMapping(path = "/add", consumes = { MULTIPART_FORM_DATA_VALUE, APPLICATION_JSON_VALUE })
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("password") String password,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isEnabled") Boolean isEnabled,
                                           @RequestParam("isNonLocked") Boolean isNonLocked,
                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
            throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        UserForm newUserForm = UserForm.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password(password)
                .email(email)
                .role(role)
                .isEnabled(isEnabled)
                .isNonLocked(isNonLocked)
                .build();
        User addedUser = this.userService.addNewUser(newUserForm, profileImage);
        return new ResponseEntity<>(addedUser, CREATED);
    }

    @PutMapping(path = "/update", consumes = { MULTIPART_FORM_DATA_VALUE, APPLICATION_JSON_VALUE })
    public ResponseEntity<User> updateUser(@RequestParam("currentUsername") String currentUsername,
                                           @RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("password") String password,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isEnabled") Boolean isEnabled,
                                           @RequestParam("isNonLocked") Boolean isNonLocked,
                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
            throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        UserForm updatedUserForm = UserForm.builder()
                .firstName(firstName)
                .lastName(lastName)
                .currentUsername(currentUsername)
                .username(username)
                .password(password)
                .email(email)
                .role(role)
                .isEnabled(isEnabled)
                .isNonLocked(isNonLocked)
                .build();
        User updatedUser = this.userService.updateUser(updatedUserForm, profileImage);
        return new ResponseEntity<>(updatedUser, OK);
    }

    @PutMapping("/update/profile/image")
    public ResponseEntity<User> updateUserProfileImage(@RequestParam("username") String username,
                                           @RequestParam("profileImage") MultipartFile profileImage)
            throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        User updatedUser = this.userService.updateUserProfileImage(username, profileImage);
        return new ResponseEntity<>(updatedUser, OK);
    }

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody UserForm loginForm){
        User loggedInUser = authService.loginUser(loginForm);
        HttpHeaders jwtHeaders = authService.getJwtHeaders(new UserPrincipal(loggedInUser));
        return new ResponseEntity<>(loggedInUser, jwtHeaders, OK);
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException {
        this.userService.deleteUser(username);
        return new ResponseEntity<>(response(NO_CONTENT, "User deleted successfully"), OK);
    }

    private HttpResponse response(HttpStatus httpStatus, String message){
        return new HttpResponse.Builder()
                .withStatusCode(httpStatus.value())
                .withHttpStatus(httpStatus)
                .withReason(httpStatus.getReasonPhrase().toUpperCase(Locale.ROOT))
                .withMessage(message)
                .build();
    }
}
