package com.example.service;

public class StateNotFoundException extends Exception {

    public StateNotFoundException(String message) {
        super(message);
    }

    public StateNotFoundException(String message, Throwable cause) {
        super(message, cause);    
    }
}
