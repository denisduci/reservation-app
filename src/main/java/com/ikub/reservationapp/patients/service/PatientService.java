package com.ikub.reservationapp.patients.service;

import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.users.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PatientService {
    List<UserDto> search(String firstName, String lastName);

     boolean hasAppointment(AppointmentDto appointmentDto, UserDto patient);
}
