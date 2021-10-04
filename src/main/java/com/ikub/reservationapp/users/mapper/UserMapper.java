package com.ikub.reservationapp.users.mapper;

import com.ikub.reservationapp.users.dto.UserDto;
import com.ikub.reservationapp.users.dto.UserUpdateDto;
import com.ikub.reservationapp.users.entity.UserEntity;
import org.mapstruct.Mapper;;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity userDtoToUser(UserDto userDto);
    UserDto userToUserDto(UserEntity user);

    UserEntity userUpdateDtoToUser(UserUpdateDto userUpdateDto);
    UserUpdateDto userToUserUpdateDto(UserEntity userEntity);
}