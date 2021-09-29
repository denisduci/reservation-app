package com.ikub.reservationapp.patients.mapper;

import com.ikub.reservationapp.patients.dto.PatientDto;
import com.ikub.reservationapp.patients.entity.PatientEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientDto patientToPatientDto(PatientEntity patientEntity);
    PatientEntity patientDtoToPatient(PatientDto patientDto);

}
