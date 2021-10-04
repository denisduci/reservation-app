package com.ikub.reservationapp.users.mapper;

import com.ikub.reservationapp.users.dto.RoleDto;
import com.ikub.reservationapp.users.entity.RoleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleEntity roleDtoToRole(RoleDto roleDto);
    RoleDto roleToRoleDto(RoleEntity roleEntity);
}