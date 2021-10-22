package com.ikub.reservationapp.appointments.service;

import com.ikub.reservationapp.appointments.dto.AppointmentDateTimeDto;
import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.appointments.dto.AppointmentResponseDto;
import com.ikub.reservationapp.appointments.dto.AppointmentSearchRequestDto;
import com.ikub.reservationapp.appointments.entity.AppointmentEntity;
import com.ikub.reservationapp.appointments.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.common.exception.ReservationAppException;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    List<AppointmentDateTimeDto> getAllAvailableHours();

    AppointmentResponseDto createAppointment(AppointmentDto appointmentDto) throws ReservationAppException;

    AppointmentResponseDto cancelAppointment(AppointmentDto appointmentDto) throws ReservationAppException;

    List<AppointmentResponseDto> getPatientCanceledAppointments() throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getDoctorCanceledAppointments() throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getPatientActiveAppointments() throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getDoctorActiveAppointments() throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getPatientFinishedAppointments() throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getPatientAppointmentsInSpecificDay(String localDate) throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getDoctorAppointmentsInSpecificDay (String localDate) throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getDoctorFinishedAppointments() throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getAllPendingAppointments() throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getAllFinishedAppointments(AppointmentSearchRequestDto searchRequestDto) throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getAllCanceledAppointments() throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getAllAppointmentsInSpecificDay(String date) throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getAllAppointmentsInSpecificDay(AppointmentSearchRequestDto date) throws AppointmentNotFoundException;

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
    List<AppointmentResponseDto> getPatientAllAppointments();

    List<AppointmentResponseDto> getDoctorAllAppointments();
    List<AppointmentResponseDto> getAllAppointments(Integer pageNumber, Integer size);

    List<AppointmentResponseDto> getAllAppointmentsWithStatusAndPagination(AppointmentSearchRequestDto searchRequestDto);
}
