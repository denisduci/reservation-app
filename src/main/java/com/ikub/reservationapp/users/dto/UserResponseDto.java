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
public class UserResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String roleName;

}