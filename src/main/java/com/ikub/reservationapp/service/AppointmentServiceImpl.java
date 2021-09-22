package com.ikub.reservationapp.service;

import com.ikub.reservationapp.entity.Appointment;
import com.ikub.reservationapp.entity.Doctor;
import com.ikub.reservationapp.entity.Patient;
import com.ikub.reservationapp.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.exception.PatientNotFoundException;
import com.ikub.reservationapp.repository.AppointmentRepository;
import com.ikub.reservationapp.repository.PatientRepository;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public Iterable<Appointment> findAvailableAppointments() {

//        LocalDateTime datetime = LocalDateTime.now();
//        datetime.plusDays(7);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);
        Date nextDate = cal.getTime();
        return appointmentRepository.findAllByStatusAndDateBetween(
                Appointment.Status.AVAILABLE, new Date(), nextDate);
    }

    @Override
    public Iterable<Appointment> findAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    public ResponseEntity<?> reserveAppointment(Appointment appointment, Patient patient) throws AppointmentNotFoundException, PatientNotFoundException {
        if (appointment.getStatus().name().equalsIgnoreCase("available")) {
            appointment.setStatus(Appointment.Status.PENDING);
            appointment.setPatient(patient);
            return new ResponseEntity<>(save(appointment), HttpStatus.OK);
        }
        return new ResponseEntity<>("Appointment is RESERVED", HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> changeDoctor(Doctor newDoctor, Appointment appointment) {
        appointment.setStatus(Appointment.Status.CHANGED);
        appointment.setDoctor(newDoctor);
        return new ResponseEntity<>(save(appointment), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> cancelAppointment(Appointment appointment) {
//        LocalDateTime current = LocalDateTime.now();
//        LocalDateTime  next = appointment.getDateTime();
//        Duration duration = Duration.between(next,current);
//        long seconds = duration.getSeconds();
//        long hoursD = seconds / 3600;
        long secs = (appointment.getDate().getTime() - new Date().getTime()) / 1000;
        long hours = secs / 3600;

        if (hours >= 24) {
            appointment.setStatus(Appointment.Status.CANCELED);
            appointment.setPatient(null);
            return new ResponseEntity<>(save(appointment), HttpStatus.OK);
        }
        return new ResponseEntity<>("Time too short to cancel", HttpStatus.BAD_REQUEST);
    }

    @Override
    public Iterable<Appointment> findByStatus(Appointment.Status status) {
        return appointmentRepository.findByStatus(status);
    }

    @Override
    public Appointment findById(Long id) throws AppointmentNotFoundException {
        return appointmentRepository.findById(id)
                .orElseThrow(()-> new AppointmentNotFoundException("No Appointment found with ID" + id));
    }

    @Override
    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }
}