package com.ikub.reservationapp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {

    PENDING("pending"),
    APPROVED("approved"),
    CANCELED("canceled"),
    UPDATED("updated"),
    DONE("done"),
    CANCELED_BY_PATIENT("CANCELED_BY_PATIENT"),
    CANCELED_BY_DOCTOR("CANCELED_BY_DOCTOR"),
    CANCELED_BY_SECRETARY("CANCELED_BY_SECRETARY");

    String status;
}
