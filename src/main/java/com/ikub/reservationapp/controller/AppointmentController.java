package com.ikub.reservationapp.controller;

import com.ikub.reservationapp.entity.Appointment;
import com.ikub.reservationapp.entity.Doctor;
import com.ikub.reservationapp.entity.Patient;
import com.ikub.reservationapp.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.exception.PatientNotFoundException;
import com.ikub.reservationapp.service.AppointmentService;
import com.ikub.reservationapp.service.DoctorService;
import com.ikub.reservationapp.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/available")
    public ResponseEntity<Iterable<Appointment>> getAllAvailableAppointments() {
        log.info("Retrieving available appointments...");
        return new ResponseEntity<>(appointmentService.findAvailableAppointments(), HttpStatus.OK);
    }

    @PutMapping("/reserve/{appointmentId}/{userId}")
    public ResponseEntity<?> reserveAppointment(@PathVariable("appointmentId") Long appointmentId,
                                                @PathVariable("userId") Long patientId) throws AppointmentNotFoundException, PatientNotFoundException {
        log.info("Reserving an appointment...");
        Appointment appointment = appointmentService.findById(appointmentId);
        Patient patient = patientService.findById(patientId);
        return appointmentService.reserveAppointment(appointment, patient);
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<?> cancelAppointment(@PathVariable("appointmentId") Long id) {
        log.info("Canceling an appointment...");
        Appointment appointment = appointmentService.findById(id);
        return appointmentService.cancelAppointment(appointment);
    }

    @PatchMapping("/change/{id}")
    public ResponseEntity<?> changeDoctor(@PathVariable("id") Long id,
                                          @RequestBody Appointment newAppointment) {
        log.info("Changing doctor for an appointment...");
        Appointment appointment = appointmentService.findById(id);
        appointment.setDoctor(newAppointment.getDoctor());
        appointment.setStatus(Appointment.Status.CHANGED);
        return new ResponseEntity<>(appointmentService.save(appointment), HttpStatus.OK);
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<?> updateAppointmentStatus(@PathVariable("appointmentId") Long id,
                                                     @RequestBody Appointment newAppointment) {
        log.info("Updating appointment status...");
        Appointment appointment = appointmentService.findById(id);
        appointment.setStatus(newAppointment.getStatus());
        return new ResponseEntity<>(appointmentService.save(appointment), HttpStatus.OK);
    }

    //user retrieve all by user
    //accept/refuse -> status ACCEPTED/CANCELED


    @GetMapping("/patient/{id}")
    public ResponseEntity<?> getAllAppointmentsByPatientId(@PathVariable("id") Long id) {
        log.info("Retrieving appointments by patient id...");
        Patient patient = patientService.findById(id);
        return new ResponseEntity<>(patient.getAppointments(), HttpStatus.OK);
    }

    @GetMapping("/doctor/{id}")
    public ResponseEntity<?> getAppointmentsByDoctorId(@PathVariable("id") Long id) {
        log.info("Retrieving appointments by doctor id...");
        Doctor doctor = doctorService.findById(id);
        return new ResponseEntity<>(doctor.getAppointments(), HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Iterable<Appointment>> getAppointmentsByStatus(@PathVariable Appointment.Status status) {
        log.info("Retrieving appointments by status...");
        return new ResponseEntity<>(appointmentService.findByStatus(status), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<Appointment>> getAllAppointments() {
        log.info("Retrieving all appointments...");
        return new ResponseEntity<>(appointmentService.findAllAppointments(), HttpStatus.OK);
    }
}