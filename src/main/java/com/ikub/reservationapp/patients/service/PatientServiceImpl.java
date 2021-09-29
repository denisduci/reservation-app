package com.ikub.reservationapp.patients.service;

import com.ikub.reservationapp.patients.dto.PatientDto;
import com.ikub.reservationapp.patients.entity.PatientEntity;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import com.ikub.reservationapp.patients.exception.PatientNotFoundException;
import com.ikub.reservationapp.patients.mapper.PatientMapper;
import com.ikub.reservationapp.patients.repository.PatientRepository;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientMapper patientMapper;

    @Override
    public PatientDto save(PatientDto patientDto) {
        PatientEntity patientEntity = patientRepository.save(patientMapper.patientDtoToPatient(patientDto));
        return patientMapper.patientToPatientDto(patientEntity);
    }

    @Override
    public PatientDto updatePatient(Long id, PatientDto patientDto) {
        val patient = findById(id);
        patient.setAddress(patientDto.getAddress());
        patient.setCity(patientDto.getCity());
        patient.setTelephone(patientDto.getTelephone());
        patient.setFirstName(patientDto.getFirstName());
        patient.setLastName(patientDto.getLastName());
        return save(patientMapper.patientToPatientDto(patient));
    }

    @Override
    public List<PatientDto> search(String firstName, String lastName) {
        if (!(StringUtils.isEmpty(firstName) && StringUtils.isEmpty(lastName))) {
            return patientRepository.findByFirstNameOrLastNameContainingAllIgnoreCase(firstName, lastName)
                    .stream().map(patient -> patientMapper.patientToPatientDto(patient))
                    .collect(Collectors.toList());
        }
        throw new ReservationAppException("No value to search");
    }

    @Override
    public PatientEntity findById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("No patient found with ID = " + id));
    }
}