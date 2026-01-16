package com.kerubo.TaskManagement.exception;

public class ConflictException extends RuntimeException{
    public ConflictException(String message){
        super(message);
    }
}
