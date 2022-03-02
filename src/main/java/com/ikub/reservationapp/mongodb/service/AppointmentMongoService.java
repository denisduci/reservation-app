package com.ikub.reservationapp.mongodb.service;

import com.ikub.reservationapp.mongodb.dto.AppointmentDto;
import com.ikub.reservationapp.mongodb.dto.AppointmentResponseDto;
import com.ikub.reservationapp.mongodb.model.AppointmentSlotDto;

import java.util.List;

public interface AppointmentMongoService {
    AppointmentResponseDto createAppointment(AppointmentDto appointmentModel);
    AppointmentDto getAppointmentById(String id);
    List<AppointmentResponseDto> getPatientAppointments(String id);

    AppointmentResponseDto updateAppointment(String id, AppointmentDto appointmentDto);

    List<AppointmentSlotDto> getAvailableHours();

    Object cancelAppointment(String id);
}
