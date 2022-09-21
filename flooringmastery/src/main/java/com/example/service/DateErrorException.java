package com.example.service;

public class DateErrorException extends Exception {
    public DateErrorException(String message) {
        super(message);
    }
    public DateErrorException(String message, Throwable cause) {
        super(message, cause);
    }        
}
