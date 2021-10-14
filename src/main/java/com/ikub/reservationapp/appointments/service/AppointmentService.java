package com.ikub.reservationapp.appointments.service;

import com.ikub.reservationapp.appointments.dto.AppointmentDateHourDto;
import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.appointments.entity.AppointmentEntity;
import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.appointments.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {

    int START_TIME = 8;
    int END_TIME = 17;
    int DAYS_TO_ITERATE = 7;

    AppointmentDateHourDto findAvailableHours(); //DONE
    AppointmentDto createAppointment(AppointmentDto appointmentDto) throws ReservationAppException; //DONE
    AppointmentDto cancelAppointment(AppointmentDto appointmentDto) throws ReservationAppException;
    List<AppointmentDto> findByStatusAndPatient(Status status, Long patientId) throws AppointmentNotFoundException;
    List<AppointmentDto> findByStatus(Status status);
    AppointmentDto updateAppointment(AppointmentDto appointmentDto);
    String updateDefaultFeedback();

    boolean canCancel(AppointmentEntity appointment);

    List<AppointmentDto> findByAppointmentDate(LocalDate appointmentDate);
    AppointmentDto changeDoctor(AppointmentDto newAppointmentDto) throws ReservationAppException;
    AppointmentDto updateAppointmentFeedback(AppointmentDto appointmentDto);
    AppointmentEntity findById(Long id) throws AppointmentNotFoundException;
    List<AppointmentDto> findByPatient(Long patientId);
    List<AppointmentDto> findByDoctor(Long doctorId);
    List<AppointmentDto> findAllAppointments();
    List<AppointmentDto> findByStatusAndDoctor(Status status, Long doctorId);
}
