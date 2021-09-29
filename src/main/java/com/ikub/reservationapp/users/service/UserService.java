package com.ikub.reservationapp.users.service;

import com.ikub.reservationapp.users.dto.UserDto;
import com.ikub.reservationapp.users.entity.UserEntity;
import com.ikub.reservationapp.common.model.LoginUser;

import java.util.List;

public interface UserService {
    String authenticate(LoginUser loginUser);
    UserDto save(UserDto user);
    List<UserDto> findAll();
    UserEntity findOne(String username);
}