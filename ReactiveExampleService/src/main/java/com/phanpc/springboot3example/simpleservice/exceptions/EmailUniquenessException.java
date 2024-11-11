package com.phanpc.springboot3example.simpleservice.exceptions;

public class EmailUniquenessException extends RuntimeException {
    public EmailUniquenessException(String message) {
        super(message);
    }
}
