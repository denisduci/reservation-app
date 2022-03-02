package com.ikub.reservationapp.mongodb.dto;

import com.ikub.reservationapp.mongodb.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMongoDto {

    private String id;

    @NotBlank(message = "First name must not be empty")
    private String firstName;

    @NotBlank(message = "Last name must not be empty")
    private String lastName;

    @NotBlank(message = "Username must not be empty")
    @Length(min = 3, message = "Username is too short")
    @Length(max = 40, message = "Username is too long")
    private String username;

    @Email
    @NotEmpty(message = "Email must not be empty")
    private String email;

    @NotBlank(message = "Password must not be empty")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$",
            message = "Password doesn't meet security!")
    private String password;

    @NotBlank(message = "Confirm Password must not be empty")
    private String confirmPassword;

    @NotNull
    @NotBlank(message = "Phone number must not be empty")
    @Size(min = 10, max = 10, message = "Size of phone number must be 10")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Must be number with 10 digits")
    private String phone;

    private Set<Role> roles;
}
