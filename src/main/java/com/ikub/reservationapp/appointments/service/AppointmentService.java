package com.ikub.reservationapp.appointments.service;

import com.ikub.reservationapp.appointments.dto.AppointmentDateHourDto;
import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.appointments.entity.AppointmentEntity;
import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.appointments.exception.AppointmentNotFoundException;
import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    public static final int START_TIME = 8;
    public static final int END_TIME = 17;
    public static final int DAYS_TO_ITERATE = 7;

    AppointmentDateHourDto findAvailableHours();
    AppointmentDto createAppointment(AppointmentDto appointmentDto);
    AppointmentDto cancelAppointment(AppointmentDto appointmentDto);
    List<AppointmentDto> findByStatusAndPatient(Status status, Long patientId);
    List<AppointmentDto> findByStatus(Status status);
    AppointmentDto updateAppointment(AppointmentDto appointmentDto);
    AppointmentDateHourDto doctorAvailableTime(Long id);

    List<AppointmentDto> findByAppointmentDate(LocalDate appointmentDate);
    AppointmentDto changeDoctor(Long id, AppointmentDto newAppointmentDto);
    AppointmentDto updateAppointmentFeedback(Long id, AppointmentDto appointmentDto);
    AppointmentEntity findById(Long id) throws AppointmentNotFoundException;
    List<AppointmentDto> findByPatient(Long patientId);
    List<AppointmentDto> findByDoctor(Long doctorId);
    List<AppointmentDto> findAllAppointments();
    List<AppointmentDto> findByStatusAndDoctor(Status status, Long doctorId);
}
