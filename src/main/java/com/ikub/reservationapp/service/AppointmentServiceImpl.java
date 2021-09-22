package com.ikub.reservationapp.service;

import com.ikub.reservationapp.entity.Appointment;
import com.ikub.reservationapp.entity.Doctor;
import com.ikub.reservationapp.entity.Patient;
import com.ikub.reservationapp.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.exception.GeneralException;
import com.ikub.reservationapp.exception.PatientNotFoundException;
import com.ikub.reservationapp.repository.AppointmentRepository;
import com.ikub.reservationapp.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public List<Appointment> findAvailableAppointments() {

        LocalDateTime datetime = LocalDateTime.now();
        LocalDateTime newDate = datetime.plusDays(7);

//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DATE, 7);
//        Date nextDate = cal.getTime();
//        return appointmentRepository.findAllByStatusAndDateBetween(
//                Appointment.Status.AVAILABLE, new Date(), nextDate);
        return appointmentRepository.findByStatusAndDateTimeBetween(
                Appointment.Status.AVAILABLE, LocalDateTime.now(), newDate);
    }

    @Override
    public List<Appointment> findAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        appointmentRepository.findAll().forEach(appointments::add);
        return appointments;
    }

    @Override
    public Appointment reserveAppointment(Appointment appointment) throws AppointmentNotFoundException, PatientNotFoundException, GeneralException {
        if (appointment.getStatus().name().equalsIgnoreCase("available")) {
            appointment.setStatus(Appointment.Status.PENDING);
            appointment.setPatient(appointment.getPatient());
            return save(appointment);
        }
        throw new GeneralException("Cannot reserve appointmend");
    }

    @Override
    public ResponseEntity<?> changeDoctor(Doctor newDoctor, Appointment appointment) {
        appointment.setStatus(Appointment.Status.CHANGED);
        appointment.setDoctor(newDoctor);
        return new ResponseEntity<>(save(appointment), HttpStatus.OK);
    }

    @Override
    public Appointment cancelAppointment(Appointment appointment) throws AppointmentNotFoundException {
        LocalDateTime current = LocalDateTime.now();
        LocalDateTime next = appointment.getDateTime();
        Duration duration = Duration.between(current, next);
        long seconds = duration.getSeconds();
        long hours = seconds / 3600;

        if (hours >= 24) {
            appointment.setStatus(Appointment.Status.CANCELED);
            appointment.setPatient(null);
            Optional<Doctor> optionalDoctor = Optional.ofNullable(appointment.getDoctor());
            if (optionalDoctor.isPresent()) {
                appointment.setDoctor(null);
            }
            return (save(appointment));
        }
        throw new AppointmentNotFoundException("Appointment not found");
    }

    @Override
    public List<Appointment> findByStatus(Appointment.Status status) {
        return appointmentRepository.findByStatus(status);
    }

    @Override
    public List<Appointment> findByPatient(Patient patient) {
        return appointmentRepository.findByPatient(patient);
    }

    @Override
    public List<Appointment> findByDoctor(Doctor doctor) {
        return appointmentRepository.findByDoctor(doctor);
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