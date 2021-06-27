package org.sliga.usersmanagement.exception;


import com.auth0.jwt.exceptions.TokenExpiredException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sliga.usersmanagement.controller.response.HttpResponse;
import org.sliga.usersmanagement.security.JwtAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import static org.sliga.usersmanagement.utils.AuthConstants.*;
import static org.sliga.usersmanagement.utils.SecurityConstants.ACCESS_DENIED_MESSAGE;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ExceptionHandling implements ErrorController {
    private static final Log logger = LogFactory.getLog(JwtAccessDeniedHandler.class);

    public static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this endpoint. Please send a '%s' request";
    public static final String INTERNAL_SERVER_MESSAGE = "An error occurred while processing the request.";
    public static final String ERROR_PROCESSING_FILE = "Error occurred while processing file";
    public static final String NO_HANDLER_FOR_THIS_PATH = "There is no mapping for this URL";
    @Value("${server.error.path}")
    public static final String ERROR_PATH = "/error";

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountDisabledExceptionHandler(){
        return createHttpResponse(BAD_REQUEST, ACCOUNT_DISABLED);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> accountLockedExceptionHandler(){
        return createHttpResponse(UNAUTHORIZED, ACCOUNT_LOCKED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedExceptionHandler(){
        return createHttpResponse(FORBIDDEN, ACCESS_DENIED_MESSAGE);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsExceptionHandler(){
        return createHttpResponse(BAD_REQUEST, BAD_CREDENTIALS);
    }

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<HttpResponse> usernameExistExceptionHandler(UsernameExistException exception){
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<HttpResponse> emailExistExceptionHandler(EmailExistException exception){
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> emailNotFoundExceptionHandler(EmailNotFoundException exception){
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> userNotFoundExceptionHandler(EmailNotFoundException exception){
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredExceptionHandler(TokenExpiredException exception){
        return createHttpResponse(UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> httpMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException exception){
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        String message = String.format(METHOD_IS_NOT_ALLOWED, supportedMethod);
        return createHttpResponse(METHOD_NOT_ALLOWED, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerErrorExceptionHandler(Exception exception){
        logger.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_MESSAGE);
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse> notFoundExceptionHandler(NoResultException exception){
        logger.error(exception.getMessage());
        return createHttpResponse(NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpResponse> ioExceptionHandler(IOException exception){
        logger.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message){
        HttpResponse httpResponse = new HttpResponse.Builder()
                .withStatusCode(httpStatus.value())
                .withHttpStatus(httpStatus)
                .withReason(httpStatus.getReasonPhrase().toUpperCase(Locale.ROOT))
                .withMessage(message)
                .build();
        return new ResponseEntity<>(httpResponse, httpStatus);
    }

    @RequestMapping(ERROR_PATH)
    public ResponseEntity<HttpResponse> noRestHandler(){
        return createHttpResponse(NOT_FOUND, NO_HANDLER_FOR_THIS_PATH);
    }

    /*@ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<HttpResponse> noHandlerFoundExceptionHandler(NoHandlerFoundException exception){
        logger.error(exception.getMessage());
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }*/
}
