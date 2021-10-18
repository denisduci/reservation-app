package com.ikub.reservationapp.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {
    @NotBlank(message = "Username must not be empty!")
    private String username;
    @NotBlank(message = "Password must not be empty!")
    private String password;
}