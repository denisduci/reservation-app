package com.ikub.reservationapp.mapper;

import com.ikub.reservationapp.dto.AppointmentDto;
import com.ikub.reservationapp.dto.DoctorDto;
import com.ikub.reservationapp.dto.PatientDto;
import com.ikub.reservationapp.entity.Appointment;
import com.ikub.reservationapp.entity.Doctor;
import com.ikub.reservationapp.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        componentModel = "spring"
)
public interface MapStructMapper {

//    @Override
//    @Mappings({
//            @Mapping(source = "id", target = "id"),
//            @Mapping(source = "username", target = "username"),
//            @Mapping(source = "firstname", target = "firstname"),
//            @Mapping(source = "lastname", target = "lastname"),
//            @Mapping(source = "email", target = "email"),
//            @Mapping(source = "role", target = "roleDto"),
//            @Mapping(source = "status", target = "statusDto")
//    })
    AppointmentDto appointmentToAppointmentDto(Appointment appointment);

    Appointment appointmentDtoToAppointment(AppointmentDto appointmentDto);

    DoctorDto doctorToDoctorDto(Doctor doctor);

    Doctor doctorDtoToDoctor(DoctorDto doctorDto);

    PatientDto patientToPatientDto(Patient patient);

    Patient patientDtoToPatient(PatientDto patientDto);
}
