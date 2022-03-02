package com.ikub.reservationapp.mongodb.dto;

import com.ikub.reservationapp.mongodb.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMongoResponseDto {
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phone;
    private Set<Role> roles;
}
