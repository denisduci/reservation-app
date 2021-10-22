package com.ikub.reservationapp.users.mapper;

import com.ikub.reservationapp.users.dto.UserDto;
import com.ikub.reservationapp.users.dto.UserResponseDto;
import com.ikub.reservationapp.users.dto.UserUpdateDto;
import com.ikub.reservationapp.users.entity.RoleEntity;
import com.ikub.reservationapp.users.entity.UserEntity;
import lombok.val;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;;import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserEntity toEntity(UserDto userDto);
    UserDto toDto(UserEntity user);

    default UserResponseDto toResponseDto(UserEntity userEntity) {
        if (userEntity == null)
            return null;
        UserResponseDto userResponseDto = new UserResponseDto();
        if (userEntity.getRoles() != null) {
            val names = userEntity.getRoles().stream()
                    .map(RoleEntity::getName)
                    .collect(Collectors.toList());
            String allUserRoles = String.join(", ", names);
            userResponseDto.setRoleName(allUserRoles);
        }
        if (userEntity.getId() != null) {
            userResponseDto.setId(userEntity.getId());
        }
        if (userEntity.getFirstName() != null) {
            userResponseDto.setFirstName(userEntity.getFirstName());
        }
        if (userEntity.getLastName() != null) {
            userResponseDto.setLastName(userEntity.getLastName());
        }
        if (userEntity.getEmail() != null) {
            userResponseDto.setEmail(userEntity.getEmail());
        }
        if (userEntity.getPhone() != null) {
            userResponseDto.setPhone(userEntity.getPhone());
        }
        return userResponseDto;
    }

    UserUpdateDto userToUserUpdateDto(UserEntity userEntity);
}