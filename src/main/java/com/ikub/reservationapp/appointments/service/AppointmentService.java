package com.ikub.reservationapp.appointments.service;

import com.ikub.reservationapp.appointments.dto.AppointmentDateTimeDto;
import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.appointments.dto.AppointmentResponseDto;
import com.ikub.reservationapp.appointments.entity.AppointmentEntity;
import com.ikub.reservationapp.appointments.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.common.exception.ReservationAppException;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    List<AppointmentDateTimeDto> getAllAvailableHours(); //DONE

    AppointmentResponseDto createAppointment(AppointmentDto appointmentDto) throws ReservationAppException; //DONE

    AppointmentResponseDto cancelAppointment(Long appointmentId) throws ReservationAppException;//DONE

    List<AppointmentResponseDto> getPatientCanceledAppointments(Long patient) throws AppointmentNotFoundException;//DONE;

    List<AppointmentResponseDto> getDoctorCanceledAppointments(Long doctorId) throws AppointmentNotFoundException;//DONE;

    List<AppointmentResponseDto> getPatientActiveAppointments(Long patientId) throws AppointmentNotFoundException;//DONE

    List<AppointmentResponseDto> getDoctorActiveAppointments(Long doctorId) throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getPatientFinishedAppointments(Long patientId) throws AppointmentNotFoundException;//DONE

    List<AppointmentResponseDto> getDoctorFinishedAppointments(Long doctorId) throws AppointmentNotFoundException;//DONE

    List<AppointmentResponseDto> getAllPendingAppointments() throws AppointmentNotFoundException;//DONE

    List<AppointmentResponseDto> getAllFinishedAppointments() throws AppointmentNotFoundException;//DONE

    List<AppointmentResponseDto> getAllCanceledAppointments() throws AppointmentNotFoundException;

    AppointmentResponseDto approveAppointment(Long appointmentId);

    AppointmentResponseDto approveOrRejectDoctorChange(AppointmentDto appointmentDto);

    AppointmentResponseDto setAppointmentToDone(Long appointmentId);

    AppointmentResponseDto updateAppointment(AppointmentDto appointmentDto);

    String updateDefaultFeedback();

    boolean isEligibleAppointmentToCancel(AppointmentEntity appointment);

    List<AppointmentDto> getAppointmentByDate(LocalDate appointmentDate);
    AppointmentResponseDto changeDoctor(AppointmentDto newAppointmentDto) throws ReservationAppException;
    AppointmentResponseDto updateAppointmentFeedback(AppointmentDto appointmentDto);
    AppointmentEntity getAppointmentById(Long id) throws AppointmentNotFoundException;
    List<AppointmentResponseDto> getPatientAllAppointments(Long patientId);
    List<AppointmentResponseDto> getDoctorAllAppointments(Long doctorId);
    List<AppointmentResponseDto> getAllAppointments();
}
