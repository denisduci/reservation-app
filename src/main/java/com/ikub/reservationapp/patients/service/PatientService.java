package com.ikub.reservationapp.patients.service;

import com.ikub.reservationapp.users.dto.UserDto;

import java.util.List;

public interface PatientService {
    List<UserDto> search(String firstName, String lastName);
}
