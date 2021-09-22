package com.ikub.reservationapp.mapper;

import com.ikub.reservationapp.dto.AppointmentDto;
import com.ikub.reservationapp.dto.DoctorDto;
import com.ikub.reservationapp.dto.PatientDto;
import com.ikub.reservationapp.entity.Appointment;
import com.ikub.reservationapp.entity.Doctor;
import com.ikub.reservationapp.entity.Patient;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring"
)
public interface MapStructMapper {

    AppointmentDto appointmentToAppointmentDto(Appointment appointment);

    Appointment appointmentDtoToAppointment(AppointmentDto appointmentDto);

    DoctorDto doctorToDoctorDto(Doctor doctor);

    Doctor doctorDtoToDoctor(DoctorDto doctorDto);

    PatientDto patientToPatientDto(Patient patient);

    Patient patientDtoToPatient(PatientDto patientDto);
}
