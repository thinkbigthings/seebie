package com.seebie.server;

public class IncompatibleClientVersionException extends RuntimeException {
    public IncompatibleClientVersionException(String message) {
        super(message);
    }
}
