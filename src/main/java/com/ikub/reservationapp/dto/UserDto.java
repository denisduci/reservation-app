package com.ikub.reservationapp.dto;

import lombok.Data;

@Data
public class UserDto {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String name;
}