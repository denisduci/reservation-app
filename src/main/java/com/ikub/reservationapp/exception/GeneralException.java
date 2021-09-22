package com.ikub.reservationapp.exception;


public class GeneralException extends RuntimeException {

    private String message;

    public GeneralException(String message) {
        super(message);
        this.message = message;
    }

    public GeneralException() {
    }
}