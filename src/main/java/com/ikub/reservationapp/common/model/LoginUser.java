package com.ikub.reservationapp.common.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class LoginUser {
    @NotBlank(message = "Username must not be empty")
    private String username;
    @NotBlank(message = "Password must not be empty")
    private String password;
}