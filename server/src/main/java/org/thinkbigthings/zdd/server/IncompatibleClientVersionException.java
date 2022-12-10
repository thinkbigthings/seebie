package org.thinkbigthings.zdd.server;

public class IncompatibleClientVersionException extends RuntimeException {
    public IncompatibleClientVersionException(String message) {
        super(message);
    }
}
