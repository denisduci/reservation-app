package com.ikub.reservationapp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {

    PENDING("pending"),
    APPROVED("approved"),
    //CANCELED("canceled"),
    //UPDATED("updated"),
    DONE("done"),
    CANCELED_BY_PATIENT("CANCELED_BY_PATIENT"),
    CANCELED_BY_DOCTOR("CANCELED_BY_DOCTOR"),
    CANCELED_BY_SECRETARY("CANCELED_BY_SECRETARY"),
    DOCTOR_CHANGE_REQUEST("DOCTOR_CHANGE_REQUEST"),
    DOCTOR_CHANGE_APPROVED("DOCTOR_CHANGE_APPROVED"),
    DOCTOR_CHANGE_REFUSED("DOCTOR_CHANGE_REFUSED");

    String status;
}
