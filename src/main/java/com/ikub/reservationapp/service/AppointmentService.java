package com.ikub.reservationapp.service;

import com.ikub.reservationapp.dto.AppointmentDto;
import com.ikub.reservationapp.entity.Appointment;;
import com.ikub.reservationapp.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.exception.GeneralException;
import com.ikub.reservationapp.exception.PatientNotFoundException;
import java.util.List;

public interface AppointmentService {

    List<AppointmentDto> findAvailableAppointments();
    AppointmentDto reserveAppointment(Long id, AppointmentDto newAppointmentDto) throws AppointmentNotFoundException, PatientNotFoundException, GeneralException;
    AppointmentDto cancelAppointment(Long id) throws AppointmentNotFoundException;
    AppointmentDto changeDoctor(Long id, AppointmentDto newAppointmentDto);
    AppointmentDto changeStatus(Long id, Appointment.Status status);
    AppointmentDto updateToDone(Long id);
    AppointmentDto updateAppointmentFeedback(Long id, AppointmentDto appointmentDto);
    Appointment findById(Long id) throws AppointmentNotFoundException;
    List<AppointmentDto> findByPatient(Long patientId);
    List<AppointmentDto> findByDoctor(Long doctorId);
    List<AppointmentDto> findByStatus(Appointment.Status status);
    List<AppointmentDto> findAllAppointments();
    List<AppointmentDto> findByStatusAndPatient(Appointment.Status status, Long patientId);
    List<AppointmentDto> findByStatusAndDoctor(Appointment.Status status, Long doctorId);
    AppointmentDto save(AppointmentDto appointment);
}
