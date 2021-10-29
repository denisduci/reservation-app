package com.ikub.reservationapp.security.ldap.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLdapResponseDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String roles;
}