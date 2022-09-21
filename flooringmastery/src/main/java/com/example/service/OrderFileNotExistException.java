package com.example.service;

public class OrderFileNotExistException extends Exception {

    public OrderFileNotExistException(String message) {
        super(message);
    }
    public OrderFileNotExistException(String message, Throwable cause) {
        super(message, cause);
    }        
}
