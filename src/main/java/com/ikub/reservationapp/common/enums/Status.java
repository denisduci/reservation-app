package com.ikub.reservationapp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {

    AVAILABLE("available"),
    PENDING("pending"),
    ACCEPTED("accepted"),
    CANCELED("canceled"),
    CHANGED("changed"),
    DONE("done");

    String status;
}
