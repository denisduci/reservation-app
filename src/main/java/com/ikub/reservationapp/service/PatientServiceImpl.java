package com.ikub.reservationapp.service;

import com.ikub.reservationapp.entity.Patient;
import com.ikub.reservationapp.exception.PatientNotFoundException;
import com.ikub.reservationapp.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public Patient save(Patient patient) {
        return patientRepository.save(patient);
    }

    @Override
    public Patient findById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("No patient found with ID = " + id));
    }
}