package io.flik.app.exceptions;

public class FlikUrlNotFoundException extends RuntimeException {
    public FlikUrlNotFoundException(String message) {
        super(message);
    }
}
