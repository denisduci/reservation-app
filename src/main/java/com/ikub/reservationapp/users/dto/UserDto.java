package com.ikub.reservationapp.users.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.*;

@Data
public class UserDto {

    @NotBlank(message = "Username must not be empty")
    @Length(min = 3, message = "Username is too short")
    @Length(max = 40, message = "Username is too long")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "Password must not be empty")
    private String password;

    @NotBlank(message = "Confirm Password must not be empty")
    private String confirmPassword;

    @Email
    @NotEmpty(message = "Email must not be empty")
    private String email;

    @NotNull
    @NotBlank(message = "Phone number must not be empty")
    @Size(min = 10, max = 10)
    @Pattern(regexp = "(^$|[0-9]{10})")
    private String phone;

    @NotBlank(message = "Name must not be empty")
    private String name;
}