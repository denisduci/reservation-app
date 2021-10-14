package com.ikub.reservationapp.users.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RoleDto {
    @NotBlank(message = "Role name must not be empty")
    private String name;
    @NotBlank(message = "Role description must not be empty")
    private String description;
}
