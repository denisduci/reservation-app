package com.ikub.reservationapp.appointments.service;

import com.ikub.reservationapp.appointments.dto.AppointmentDateHourDto;
import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.appointments.dto.AppointmentResponseDto;
import com.ikub.reservationapp.appointments.entity.AppointmentEntity;
import com.ikub.reservationapp.appointments.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.common.exception.ReservationAppException;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    AppointmentDateHourDto getAllAvailableHours(); //DONE

    AppointmentResponseDto createAppointment(AppointmentDto appointmentDto) throws ReservationAppException; //DONE

    AppointmentDto cancelAppointment(Long appointmentId) throws ReservationAppException;//DONE

    List<AppointmentDto> getPatientCanceledAppointments(Long patient) throws AppointmentNotFoundException;//DONE;

    List<AppointmentDto> getDoctorCanceledAppointments(Long doctorId) throws AppointmentNotFoundException;//DONE;

    List<AppointmentDto> getPatientActiveAppointments(Long patientId) throws AppointmentNotFoundException;//DONE

    List<AppointmentDto> getDoctorActiveAppointments(Long doctorId) throws AppointmentNotFoundException;

    List<AppointmentDto> getPatientFinishedAppointments(Long patientId) throws AppointmentNotFoundException;//DONE

    List<AppointmentDto> getDoctorFinishedAppointments(Long doctorId) throws AppointmentNotFoundException;//DONE

    List<AppointmentDto> getAllPendingAppointments() throws AppointmentNotFoundException;//DONE

    List<AppointmentDto> getAllFinishedAppointments() throws AppointmentNotFoundException;//DONE

    List<AppointmentDto> getAllCanceledAppointments() throws AppointmentNotFoundException;

    AppointmentResponseDto approveAppointment(Long appointmentId);

    AppointmentResponseDto approveOrRejectDoctorChange(AppointmentDto appointmentDto);

    AppointmentResponseDto setAppointmentToDone(Long appointmentId);

    AppointmentDto updateAppointment(AppointmentDto appointmentDto);

    String updateDefaultFeedback();

    boolean isEligibleAppointmentToCancel(AppointmentEntity appointment);

    List<AppointmentDto> getAppointmentByDate(LocalDate appointmentDate);
    AppointmentDto changeDoctor(AppointmentDto newAppointmentDto) throws ReservationAppException;
    AppointmentResponseDto updateAppointmentFeedback(AppointmentDto appointmentDto);
    AppointmentEntity getAppointmentById(Long id) throws AppointmentNotFoundException;
    List<AppointmentDto> getPatientAllAppointments(Long patientId);
    List<AppointmentDto> getDoctorAllAppointments(Long doctorId);
    List<AppointmentDto> getAllAppointments();
}
