package com.ikub.reservationapp.service;

import com.ikub.reservationapp.dto.PatientDto;
import com.ikub.reservationapp.entity.Patient;
import com.ikub.reservationapp.exception.GeneralException;
import com.ikub.reservationapp.exception.PatientNotFoundException;
import com.ikub.reservationapp.mapper.MapStructMapper;
import com.ikub.reservationapp.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MapStructMapper mapStructMapper;

    @Override
    public PatientDto save(PatientDto patientDto) {
        Patient patient = patientRepository.save(mapStructMapper.patientDtoToPatient(patientDto));
        return mapStructMapper.patientToPatientDto(patient);
    }

    @Override
    public List<PatientDto> search(String firstName, String lastName) {

        if (!(firstName.trim().isEmpty() && lastName.trim().isEmpty())) {
            return patientRepository.findByFirstNameOrLastNameContaining(firstName, lastName)
                    .stream().map(patient -> mapStructMapper.patientToPatientDto(patient))
                    .collect(Collectors.toList());
        }
        throw new GeneralException("No value to search");
    }

    @Override
    public Patient findById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("No patient found with ID = " + id));
    }
}