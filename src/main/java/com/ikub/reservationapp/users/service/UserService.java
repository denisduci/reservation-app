package com.ikub.reservationapp.users.service;

import com.ikub.reservationapp.common.model.AuthToken;
import com.ikub.reservationapp.users.dto.UserDto;
import com.ikub.reservationapp.users.dto.UserUpdateDto;
import com.ikub.reservationapp.users.entity.UserEntity;
import com.ikub.reservationapp.common.model.LoginUser;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService {
    AuthToken authenticate(LoginUser loginUser);
    AuthToken generateRefreshToken(HttpServletRequest request);
    UserDto save(UserDto user);
    List<UserDto> findAll();
    UserDto findById(Long id);
    UserUpdateDto updateUser(UserUpdateDto userDto);
    List<UserDto> findUsersByRole(String roleName);
    UserDto findByIdAndRole(Long id, String roleName);
    UserDto findByUsername(String username);
}