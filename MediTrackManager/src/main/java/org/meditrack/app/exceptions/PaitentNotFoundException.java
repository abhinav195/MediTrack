package org.meditrack.app.exceptions;

public class PatientNotFoundException extends PersonNotFoundException {
    public PatientNotFoundException(String message) {
        super(message);
    }
    public PatientNotFoundException() {
        super();
    }

}
