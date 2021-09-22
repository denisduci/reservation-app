package com.ikub.reservationapp.service;

import com.ikub.reservationapp.entity.Appointment;
import com.ikub.reservationapp.entity.Doctor;
import com.ikub.reservationapp.entity.Patient;
import com.ikub.reservationapp.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.exception.GeneralException;
import com.ikub.reservationapp.exception.PatientNotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AppointmentService {

    Appointment save(Appointment appointment);

    List<Appointment> findByStatus(Appointment.Status status);

    List<Appointment> findByPatient(Patient patient);

    List<Appointment> findByDoctor(Doctor doctor);

    List<Appointment> findAvailableAppointments();

    List<Appointment> findAllAppointments();

    Appointment findById(Long id) throws AppointmentNotFoundException;

    Appointment cancelAppointment(Appointment appointment) throws AppointmentNotFoundException;

    Appointment reserveAppointment(Appointment appointment) throws AppointmentNotFoundException, PatientNotFoundException, GeneralException;

    ResponseEntity<?> changeDoctor(Doctor doctor, Appointment appointment);
}
