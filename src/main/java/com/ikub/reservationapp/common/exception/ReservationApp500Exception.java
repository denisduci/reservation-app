package com.ikub.reservationapp.common.exception;


public class ReservationApp500Exception extends RuntimeException {
    private String message;

    public ReservationApp500Exception(String message) {
        super(message);
        this.message = message;

    }

    public ReservationApp500Exception() {

    }
}