package com.ikub.reservationapp.doctors.service;

import com.ikub.reservationapp.doctors.dto.DoctorDto;
import com.ikub.reservationapp.doctors.dto.DoctorReportDto;
import com.ikub.reservationapp.doctors.exception.DoctorNotFoundException;

import java.util.List;

public interface DoctorService {

    DoctorDto findById(Long id) throws DoctorNotFoundException;
    DoctorDto save(DoctorDto doctorDto);
    List<DoctorReportDto> findDoctors();
}
