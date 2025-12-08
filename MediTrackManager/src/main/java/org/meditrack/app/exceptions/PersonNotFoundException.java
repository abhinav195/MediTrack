package org.meditrack.app.exceptions;

public class PersonNotFoundException extends Exception {
    public PersonNotFoundException() {
        super();
    }
    public PersonNotFoundException(String message) {
        super(message);
    }
}
