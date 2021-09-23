package com.ikub.reservationapp.controller;

import com.ikub.reservationapp.dto.AppointmentDto;
import com.ikub.reservationapp.entity.Appointment;;
import com.ikub.reservationapp.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.exception.GeneralException;
import com.ikub.reservationapp.exception.PatientNotFoundException;
import com.ikub.reservationapp.service.AppointmentService;;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/available")
    public ResponseEntity<List<AppointmentDto>> getAllAvailableAppointments() {
        log.info("Retrieving available appointments...");
        return new ResponseEntity<>(appointmentService.findAvailableAppointments(), HttpStatus.OK);
    }

    @PatchMapping("/reserve/{id}")
    public ResponseEntity<AppointmentDto> reserveAppointment(@PathVariable("id") Long appointmentId,
                                                             @RequestBody AppointmentDto newAppointmentDto) throws AppointmentNotFoundException, PatientNotFoundException, GeneralException {
        log.info("Reserving an appointment...");
        return new ResponseEntity<>(appointmentService.reserveAppointment(appointmentId, newAppointmentDto), HttpStatus.OK);
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<AppointmentDto> cancelAppointment(@PathVariable("id") Long id) {
        log.info("Canceling an appointment...");
        return new ResponseEntity<>(appointmentService.cancelAppointment(id), HttpStatus.OK);
    }

    @PatchMapping("/change/{id}")
    public ResponseEntity<AppointmentDto> changeDoctor(@PathVariable("id") Long id,
                                                       @RequestBody AppointmentDto newAppointmentDto) {
        log.info("Changing doctor for an appointment...");
        return new ResponseEntity<>(appointmentService.changeDoctor(id, newAppointmentDto), HttpStatus.OK);
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsByPatientId(@PathVariable("id") Long id) {
        log.info("Retrieving appointments by patient id...");
        return new ResponseEntity<>(appointmentService.findByPatient(id), HttpStatus.OK);
    }

    @GetMapping("/doctor/{id}")
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsByDoctorId(@PathVariable("id") Long id) {
        log.info("Retrieving appointments by doctor id...");
        return new ResponseEntity<>(appointmentService.findByDoctor(id), HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByStatus(@PathVariable Appointment.Status status) {
        log.info("Retrieving appointments by status...");
        return new ResponseEntity<>(appointmentService.findByStatus(status), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDto>> getAllAppointments() {
        log.info("Retrieving all appointments...");
        return new ResponseEntity<>(appointmentService.findAllAppointments(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AppointmentDto> saveAppointment(@RequestBody AppointmentDto appointmentDto) {
        log.info("Saving appointment...");
        return new ResponseEntity<>(appointmentService.save(appointmentDto), HttpStatus.CREATED);
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<AppointmentDto> updateAppointmentStatus(@PathVariable("id") Long id,
                                                                  @RequestParam("status") Appointment.Status status) {
        log.info("Updating appointment status...");
        return new ResponseEntity<>(appointmentService.changeStatus(id, status), HttpStatus.OK);
    }

    @GetMapping("/patientstatus/{id}")
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsByStatusAndPatient(@PathVariable("id") Long patientId,
                                                                                     @RequestParam("status") Appointment.Status status) {
        log.info("Retrieving all appointments by status and patient...");
        return new ResponseEntity<>(appointmentService.findByStatusAndPatient(status, patientId), HttpStatus.OK);
    }

    @GetMapping("/doctorstatus/{id}")
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsByStatusAndDoctor(@PathVariable("id") Long doctorId,
                                                                                     @RequestParam("status") Appointment.Status status) {
        log.info("Retrieving all appointments by status and patient...");
        return new ResponseEntity<>(appointmentService.findByStatusAndDoctor(status, doctorId), HttpStatus.OK);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<AppointmentDto> updateAppointmentToDone(@PathVariable("id") Long id) {
        log.info("Updating appointment status to DONE...");
        return new ResponseEntity<>(appointmentService.updateToDone(id), HttpStatus.OK);
    }
}