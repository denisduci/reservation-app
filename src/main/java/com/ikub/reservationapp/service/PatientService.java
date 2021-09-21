package com.ikub.reservationapp.service;

import com.ikub.reservationapp.entity.Patient;
import com.ikub.reservationapp.exception.PatientNotFoundException;


public interface PatientService {

    Patient findById(Long id) throws PatientNotFoundException;

    Patient save(Patient patient);
}
