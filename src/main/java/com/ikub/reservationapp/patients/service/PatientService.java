package com.ikub.reservationapp.patients.service;

import com.ikub.reservationapp.users.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PatientService {
    List<UserDto> search(String firstName, String lastName);
    boolean hasAppointment(UserDto doctor, LocalDateTime start, LocalDateTime end);
}
