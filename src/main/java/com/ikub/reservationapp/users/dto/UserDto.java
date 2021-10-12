package com.ikub.reservationapp.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ikub.reservationapp.common.enums.Role;
import com.ikub.reservationapp.users.entity.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.*;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(message = "First name must not be empty")
    private String firstName;

    @NotBlank(message = "Last name must not be empty")
    private String lastName;

    @NotBlank(message = "Username must not be empty")
    @Length(min = 3, message = "Username is too short")
    @Length(max = 40, message = "Username is too long")
    private String username;

    @NotBlank(message = "Password must not be empty")
    private String password;

    @NotBlank(message = "Confirm Password must not be empty")
//    /@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String confirmPassword;

    @Email
    @NotEmpty(message = "Email must not be empty")
    private String email;

    @NotNull
    @NotBlank(message = "Phone number must not be empty")
    @Size(min = 10, max = 10)
    @Pattern(regexp = "(^$|[0-9]{10})")
    private String phone;

    private Set<RoleDto> roles;

}