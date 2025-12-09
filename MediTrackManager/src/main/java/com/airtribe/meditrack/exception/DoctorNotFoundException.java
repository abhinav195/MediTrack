package com.airtribe.meditrack.exception;

public class DoctorNotFoundException extends PersonNotFoundException {
    public DoctorNotFoundException(String message) {
        super(message);
    }
    public DoctorNotFoundException() {
        super();
    }
}
