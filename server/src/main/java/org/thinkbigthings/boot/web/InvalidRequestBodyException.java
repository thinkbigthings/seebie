package org.thinkbigthings.boot.web;

public class InvalidRequestBodyException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;
    public InvalidRequestBodyException(String message) {
        super(message);
    }
}
