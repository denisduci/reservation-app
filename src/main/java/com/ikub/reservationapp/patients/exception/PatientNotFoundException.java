package com.ikub.reservationapp.patients.exception;

public class PatientNotFoundException extends RuntimeException {

    private String message;

    public PatientNotFoundException(String message) {
        super(message);
        this.message = message;
    }

    public PatientNotFoundException() {
    }
}