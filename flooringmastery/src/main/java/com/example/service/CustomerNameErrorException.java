package com.example.service;


public class CustomerNameErrorException extends Exception {

    public CustomerNameErrorException(String message) {
        super(message);
    }
    public CustomerNameErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}