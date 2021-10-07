package com.ikub.reservationapp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    ADMIN("ADMIN"),
    PATIENT("PATIENT"),
    DOCTOR("DOCTOR"),
    SECRETARY("SECRETARY");

    String role;
}