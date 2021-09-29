package com.ikub.reservationapp.patients.service;

import com.ikub.reservationapp.patients.dto.PatientDto;
import com.ikub.reservationapp.patients.entity.PatientEntity;
import com.ikub.reservationapp.patients.exception.PatientNotFoundException;

import java.util.List;

public interface PatientService {

    PatientEntity findById(Long id) throws PatientNotFoundException;
    PatientDto save(PatientDto patientDto);
    PatientDto updatePatient(Long id, PatientDto patientDto);
    List<PatientDto> search(String firstName, String lastName);
}
