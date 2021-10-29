package com.ikub.reservationapp.security.ldap.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLdap {
    @NotBlank(message = "First name must not be empty")
    private String firstName;

    @NotBlank(message = "Last name must not be empty")
    private String lastName;

    @NotBlank(message = "Username must not be empty")
    @Length(min = 3, message = "Username is too short")
    @Length(max = 40, message = "Username is too long")
    private String username;

    @NotBlank(message = "Password must not be empty")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$",
            message = "Password doesn't meet security!")
    private String password;

    @Email
    @NotEmpty(message = "Email must not be empty")
    private String email;

    @NotNull
    @NotBlank(message = "Phone number must not be empty")
    @Size(min = 10, max = 10, message = "Size of phone number must be 10")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Must be number with 10 digits")
    private String phone;

    private String roles;
}