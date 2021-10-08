package com.ikub.reservationapp.doctors.service;

import com.ikub.reservationapp.users.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public interface DoctorService {

//    List<DoctorReportDto> findDoctors();
    List<UserDto> findAvailableDoctors(LocalDateTime start, LocalDateTime end);
    UserDto saveDoctor(UserDto userDto);
}
