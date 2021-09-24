package com.ikub.reservationapp.service;

import com.ikub.reservationapp.dto.UserDto;
import com.ikub.reservationapp.entity.User;
import com.ikub.reservationapp.model.LoginUser;

import java.util.List;

public interface UserService {
    String authenticate(LoginUser loginUser);
    UserDto save(UserDto user);
    List<UserDto> findAll();
    User findOne(String username);
}