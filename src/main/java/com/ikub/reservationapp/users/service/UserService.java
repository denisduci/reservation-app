package com.ikub.reservationapp.users.service;

import com.ikub.reservationapp.common.exception.PasswordNotValidException;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import com.ikub.reservationapp.common.model.AuthToken;
import com.ikub.reservationapp.users.dto.UserDto;
import com.ikub.reservationapp.users.dto.UserUpdateDto;
import com.ikub.reservationapp.users.entity.UserEntity;
import com.ikub.reservationapp.common.model.LoginUser;
import com.ikub.reservationapp.users.exception.UserNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService {
    AuthToken authenticate(LoginUser loginUser);
    AuthToken generateRefreshToken(HttpServletRequest request);
    UserDto save(UserDto user) throws PasswordNotValidException, ReservationAppException;
    List<UserDto> findAll();
    UserDto findById(Long id) throws UserNotFoundException;
    UserUpdateDto updateUser(UserUpdateDto userDto);
    List<UserDto> findUsersByRole(String roleName);
    UserDto findByIdAndRole(Long id, String roleName) throws ReservationAppException;
    UserDto findByUsername(String username) throws UserNotFoundException;

    String getUsernameFromContext();
}