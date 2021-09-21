package com.ikub.reservationapp.service;

import com.ikub.reservationapp.entity.Appointment;
import com.ikub.reservationapp.entity.Doctor;
import com.ikub.reservationapp.entity.Patient;
import com.ikub.reservationapp.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.exception.PatientNotFoundException;
import org.springframework.http.ResponseEntity;

public interface AppointmentService {

    Appointment save(Appointment appointment);

    Iterable<Appointment> findByStatus(Appointment.Status status);

    Iterable<Appointment> findAvailableAppointments();

    Iterable<Appointment> findAllAppointments();

    Appointment findById(Long id) throws AppointmentNotFoundException;

    ResponseEntity<?> cancelAppointment(Appointment appointment);

    ResponseEntity<?> reserveAppointment(Appointment appointment, Patient patient) throws AppointmentNotFoundException, PatientNotFoundException;

    ResponseEntity<?> changeDoctor(Doctor doctor, Appointment appointment);
}
