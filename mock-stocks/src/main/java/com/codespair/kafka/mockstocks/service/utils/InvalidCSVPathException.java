package com.codespair.kafka.mockstocks.service.utils;

/**
 * Runtime Exception that should be thrown when an invalid csv file path is specified.
 */
public class InvalidCSVPathException extends RuntimeException {

    public InvalidCSVPathException() {
        super("The specified csv file was not found on the specified path provided");
    }

    public InvalidCSVPathException(String message) {
        super(message);
    }
}
