package org.sliga.usersmanagement.exception;

public class EmailExistException extends Exception{
    public EmailExistException(String message){
        super(message);
    }
}
