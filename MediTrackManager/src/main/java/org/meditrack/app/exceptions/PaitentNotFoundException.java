package org.meditrack.app.exceptions;

public class PaitentNotFoundException extends Exception {
    public PaitentNotFoundException() {
        super();
    }
    public PaitentNotFoundException(String message) {
        super(message);
    }
}
