package com.konstantinov.onlinestore.exception;

public class CakeNotFoundException extends RuntimeException{
    public CakeNotFoundException(String message) {
        super(message);
    }
}
