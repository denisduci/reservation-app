package com.ikub.reservationapp.doctors.service;

import com.ikub.reservationapp.doctors.dto.DoctorDto;
import com.ikub.reservationapp.doctors.dto.DoctorReportDto;
import com.ikub.reservationapp.doctors.exception.DoctorNotFoundException;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ListResourceBundle;

public interface DoctorService {

    DoctorDto findById(Long id) throws DoctorNotFoundException;
    DoctorDto save(DoctorDto doctorDto);
    List<DoctorReportDto> findDoctors();
    List<DoctorDto> findAvailableDoctors(LocalDateTime start, LocalDateTime end);
}
