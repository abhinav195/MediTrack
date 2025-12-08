package org.meditrack.app.exceptions;

public class DoctorNotFoundException extends PersonNotFoundException {
    public DoctorNotFoundException(String message) {
        super(message);
    }
    public DoctorNotFoundException() {
        super();
    }

}
