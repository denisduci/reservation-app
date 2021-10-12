package com.ikub.reservationapp.users.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Transient;
import java.util.Set;

@Data
public class UserUpdateDto {

    private Long id;
//    @JsonIgnore
//    private String password;
//    @Transient
//    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phone;
    private Set<RoleDto> roles;
}
