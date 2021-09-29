package com.ikub.reservationapp.doctors.exception;

public class DoctorNotFoundException extends RuntimeException {

    private String message;

    public DoctorNotFoundException(String message) {
        super(message);
        this.message = message;
    }

    public DoctorNotFoundException() {
    }
}