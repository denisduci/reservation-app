package com.ikub.reservationapp.common.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ReservationAppException extends RuntimeException {
    private String message;
}