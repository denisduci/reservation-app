package com.ikub.reservationapp.appointments.service;

import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.appointments.entity.AppointmentEntity;;
import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.appointments.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import com.ikub.reservationapp.patients.exception.PatientNotFoundException;
import org.apache.tomcat.jni.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AppointmentService {

    //List<AppointmentDto> findAvailableAppointments();
    Map<Object, List<Integer>> findAvailableHours();
    List<AppointmentDto> findByAppointmentDate(LocalDate appointmentDate);
    AppointmentDto reserveAppointment(Long id, AppointmentDto newAppointmentDto) throws AppointmentNotFoundException, PatientNotFoundException, ReservationAppException;
    AppointmentDto cancelAppointment(Long id) throws AppointmentNotFoundException;
    AppointmentDto changeDoctor(Long id, AppointmentDto newAppointmentDto);
    AppointmentDto changeStatus(Long id, Status status);
    AppointmentDto updateToDone(Long id);
    AppointmentDto updateAppointmentFeedback(Long id, AppointmentDto appointmentDto);
    AppointmentEntity findById(Long id) throws AppointmentNotFoundException;
    List<AppointmentDto> findByPatient(Long patientId);
    List<AppointmentDto> findByDoctor(Long doctorId);
    List<AppointmentDto> findByStatus(Status status);
    List<AppointmentDto> findAllAppointments();
    List<AppointmentDto> findByStatusAndPatient(Status status, Long patientId);
    List<AppointmentDto> findByStatusAndDoctor(Status status, Long doctorId);
    AppointmentDto save(AppointmentDto appointment);
}
