package com.ikub.reservationapp.users.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Transient;
import java.util.Set;

@Data
public class UserUpdateDto {

    private Long id;
    private String username;
//    @JsonIgnore
//    private String password;
//    @Transient
//    private String confirmPassword;
    private String email;
    private String phone;
    private String name;
    private Set<RoleDto> roles;
}
