package com.example.service;

public class ProductTypeNotFoundException extends Exception {

    public ProductTypeNotFoundException(String message) {
        super(message);
    }
    public ProductTypeNotFoundException(String message, Throwable cause) {
        super(message, cause);  
    }
}
