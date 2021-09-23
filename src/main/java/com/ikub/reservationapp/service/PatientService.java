package com.ikub.reservationapp.service;

import com.ikub.reservationapp.dto.PatientDto;
import com.ikub.reservationapp.entity.Patient;
import com.ikub.reservationapp.exception.PatientNotFoundException;

public interface PatientService {

    Patient findById(Long id) throws PatientNotFoundException;
    PatientDto save(PatientDto patientDto);
}
