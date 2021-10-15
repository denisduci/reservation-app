package com.ikub.reservationapp.appointments.service;

import com.ikub.reservationapp.appointments.dto.AppointmentDateHourDto;
import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.appointments.dto.AppointmentResponseDto;
import com.ikub.reservationapp.appointments.entity.AppointmentEntity;
import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.appointments.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.common.exception.ReservationAppException;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    List<AppointmentDto> getCanceledAppointmentsByPatient(Long patient) throws AppointmentNotFoundException;//DONE;

    List<AppointmentDto> getActiveAppointmentsByPatient(Long patientId) throws AppointmentNotFoundException;//DONE

    List<AppointmentDto> getFinishedAppointmentsByPatient(Long patientId) throws AppointmentNotFoundException;//DONE

    List<AppointmentDto> getAllPendingAppointments();//DONE

    List<AppointmentDto> getAllFinishedAppointments();//DONE

    List<AppointmentDto> getAllCanceledAppointments();

    AppointmentResponseDto approveAppointment(Long id);

    AppointmentResponseDto approveOrRejectDoctorChange(AppointmentDto appointmentDto);

    AppointmentResponseDto setAppointmentToDone(Long id);

    AppointmentDateHourDto findAvailableHours(); //DONE
    AppointmentDto createAppointment(AppointmentDto appointmentDto) throws ReservationAppException; //DONE
    AppointmentDto cancelAppointment(AppointmentDto appointmentDto) throws ReservationAppException;//DONE

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
