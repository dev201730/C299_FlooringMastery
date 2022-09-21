package com.example.service;

public class AreaBelowMinException extends Exception {

    public AreaBelowMinException(String message) {
        super(message);
    }
    public AreaBelowMinException(String message, Throwable cause) {
        super(message, cause);
    }
}