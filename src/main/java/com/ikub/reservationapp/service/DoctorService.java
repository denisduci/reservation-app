package com.ikub.reservationapp.service;

import com.ikub.reservationapp.entity.Doctor;
import com.ikub.reservationapp.entity.Patient;
import com.ikub.reservationapp.exception.DoctorNotFoundException;

import java.util.Optional;

public interface DoctorService {

    Doctor findById(Long id) throws DoctorNotFoundException;

    Doctor save(Doctor doctor);
}
