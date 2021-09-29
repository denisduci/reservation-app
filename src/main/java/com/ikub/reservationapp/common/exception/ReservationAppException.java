package com.ikub.reservationapp.common.exception;


public class ReservationAppException extends RuntimeException {

    private String message;

    public ReservationAppException(String message) {
        super(message);
        this.message = message;
    }

    public ReservationAppException() {
    }
}