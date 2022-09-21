package com.example.service;

public class NoOrderNumException extends Exception {
    public NoOrderNumException (String message) {
        super(message);
    }
    public NoOrderNumException (String message, Throwable cause) {
        super(message, cause);
    }    
}
