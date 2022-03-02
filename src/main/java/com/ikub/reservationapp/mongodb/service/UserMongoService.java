package com.ikub.reservationapp.mongodb.service;

import com.ikub.reservationapp.mongodb.dto.PasswordDto;
import com.ikub.reservationapp.mongodb.dto.UserMongoDto;
import com.ikub.reservationapp.mongodb.dto.UserMongoResponseDto;
import com.ikub.reservationapp.mongodb.model.NamesOnly;
import com.ikub.reservationapp.mongodb.model.Role;
import com.ikub.reservationapp.mongodb.model.UserMongo;

import java.util.List;

public interface UserMongoService {
    UserMongoResponseDto saveUser(UserMongoDto userMongoDto);
    UserMongoResponseDto saveDoctor(UserMongoDto userMongoDto);
    Role saveRole(Role role);
    UserMongoResponseDto getUserByUsername(String username);
    List<NamesOnly> getByProjection();

    String resetPassword(String email);

    String validatePasswordResetToken(String token);

    String saveNewPassword(PasswordDto passwordDto);
}
