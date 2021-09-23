package com.ikub.reservationapp.service;

import com.ikub.reservationapp.dto.DoctorDto;
import com.ikub.reservationapp.entity.Doctor;
import com.ikub.reservationapp.exception.DoctorNotFoundException;

public interface DoctorService {

    Doctor findById(Long id) throws DoctorNotFoundException;
    DoctorDto save(DoctorDto doctorDto);
}
