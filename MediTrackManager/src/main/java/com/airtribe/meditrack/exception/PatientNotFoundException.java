package com.airtribe.meditrack.exception;

public class PatientNotFoundException extends PersonNotFoundException {
    public PatientNotFoundException(String message) {
        super(message);
    }
    public PatientNotFoundException() {
        super();
    }
}
