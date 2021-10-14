package com.ikub.reservationapp.doctors.service;

import com.ikub.reservationapp.users.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public interface DoctorService {

//    List<DoctorReportDto> findDoctors();
    boolean hasAvailableDoctors(LocalDateTime start, LocalDateTime end);
    boolean isDoctorAvailable(UserDto doctor,  LocalDateTime start, LocalDateTime end);
    UserDto saveDoctor(UserDto userDto);
}
