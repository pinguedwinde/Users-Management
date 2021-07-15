package org.sliga.usersmanagement.exception.domain;

public class EmailExistException extends Exception{
    public EmailExistException(String message){
        super(message);
    }
}
