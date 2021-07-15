package org.sliga.usersmanagement.exception.domain;

public class UserNotFoundException extends Exception{
    public UserNotFoundException(String message){
        super(message);
    }
}
