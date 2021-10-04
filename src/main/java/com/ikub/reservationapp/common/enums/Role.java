package com.ikub.reservationapp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    ADMIN("ADMIN"),
    USER("USER"),
    SECRETARY("SECRETARY"),
    DOCTOR("DOCTOR");

    String role;
}