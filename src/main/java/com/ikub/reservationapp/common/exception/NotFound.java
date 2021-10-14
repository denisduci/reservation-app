package com.ikub.reservationapp.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotFound {

    APPOINTMENT("No Appointment found!"),
    USER("User not found!"),
    USER_WITH_ROLE("User not found with this id and role"),
    USERNAME("User not found with this username");

    private String message;

}
