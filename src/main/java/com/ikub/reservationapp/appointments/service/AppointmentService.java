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

    List<AppointmentResponseDto> getDoctorCanceledAppointments(AppointmentSearchRequestDto searchRequestDto) throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getPatientActiveAppointments() throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getDoctorActiveAppointments(AppointmentSearchRequestDto searchRequestDto) throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getPatientFinishedAppointments() throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getDoctorFinishedAppointments(AppointmentSearchRequestDto searchRequestDto) throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getAllPendingAppointments(AppointmentSearchRequestDto searchRequestDto) throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getAllFinishedAppointments(AppointmentSearchRequestDto searchRequestDto) throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getAllCanceledAppointments(AppointmentSearchRequestDto searchRequestDto) throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getAllApprovedAppointments(AppointmentSearchRequestDto searchRequestDto) throws AppointmentNotFoundException;

    List<AppointmentResponseDto> searchAppointmentWithSpecification(AppointmentSearchRequestDto date) throws AppointmentNotFoundException;

    AppointmentResponseDto approveAppointment(Long appointmentId);

    AppointmentResponseDto approveOrRejectDoctorChange(AppointmentDto appointmentDto);

    AppointmentResponseDto setAppointmentToDone(Long appointmentId);

    AppointmentResponseDto updateAppointment(AppointmentDto appointmentDto);

    AppointmentResponseDto suggestTime(Long id, AppointmentDto suggestedAppointment);

    String updateDefaultFeedback();

    boolean isEligibleAppointmentToCancel(AppointmentEntity appointment);

    List<AppointmentDto> getAppointmentByDateAndNotCanceled(LocalDate appointmentDate);

    AppointmentResponseDto changeDoctor(AppointmentDto newAppointmentDto) throws ReservationAppException;

    AppointmentResponseDto updateAppointmentFeedback(AppointmentDto appointmentDto);

    AppointmentEntity getAppointmentById(Long id) throws AppointmentNotFoundException;

    List<AppointmentResponseDto> getPatientAllAppointments(AppointmentSearchRequestDto searchRequestDto);

    List<AppointmentResponseDto> getDoctorAllAppointments(AppointmentSearchRequestDto searchRequestDto);

    List<AppointmentResponseDto> getAllAppointments(AppointmentSearchRequestDto searchRequestDto);

    List<AppointmentResponseDto> getAllAppointmentsWithStatusAndPagination(AppointmentSearchRequestDto searchRequestDto);
}
