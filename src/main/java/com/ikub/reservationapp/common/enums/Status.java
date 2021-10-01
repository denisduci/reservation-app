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
    DONE("done");

    String status;
}
