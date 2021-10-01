package com.ikub.reservationapp.appointments.controller;

import com.ikub.reservationapp.appointments.dto.AppointmentDateHourDto;
import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.appointments.service.AppointmentService;;
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
    public ResponseEntity<AppointmentDateHourDto> getAllAvailableHours() {
        log.info("Retrieving all available appointments...");
        return new ResponseEntity<>(appointmentService.findAvailableHours(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AppointmentDto> createAppointment(@RequestBody AppointmentDto appointmentDto) {
        log.info("Creating an appointment...");
        return new ResponseEntity<>(appointmentService.createAppointment(appointmentDto), HttpStatus.OK);
    }

    @PutMapping("/cancel")
    public ResponseEntity<AppointmentDto> cancelAppointment(@RequestBody AppointmentDto appointmentDto) {
        log.info("Canceling an appointment...");
        return new ResponseEntity<>(appointmentService.cancelAppointment(appointmentDto), HttpStatus.OK);
    }

    @GetMapping("/patientstatus/{id}")
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsByStatusAndPatient(@PathVariable("id") Long patientId,
                                                                                     @RequestParam("status") Status status) {
        log.info("Retrieving all appointments by status and patient...");
        return new ResponseEntity<>(appointmentService.findByStatusAndPatient(status, patientId), HttpStatus.OK);
    }

    @GetMapping("/status")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByStatus(@RequestParam Status status) {
        log.info("Retrieving appointments by status...");
        return new ResponseEntity<>(appointmentService.findByStatus(status), HttpStatus.OK);
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsByPatientId(@PathVariable("id") Long id) {
        log.info("Retrieving appointments by patient id...");
        return new ResponseEntity<>(appointmentService.findByPatient(id), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<AppointmentDto> updateAppointment(@RequestBody AppointmentDto appointmentDto) {
        log.info("Updating appointment...");
        return new ResponseEntity<>(appointmentService.updateAppointment(appointmentDto), HttpStatus.OK);
    }

    @GetMapping("/doctorHours/{id}")
    public ResponseEntity<AppointmentDateHourDto> getDoctorAvailableHours(@PathVariable Long id) {
        log.info("Retrieving doctor available appointments...");
        return new ResponseEntity<>(appointmentService.doctorAvailableTime(id), HttpStatus.OK);
    }

    @PatchMapping("/change/{id}")
    public ResponseEntity<AppointmentDto> changeDoctor(@PathVariable("id") Long id,
                                                       @RequestBody AppointmentDto newAppointmentDto) {
        log.info("Changing doctor for an appointment...");
        return new ResponseEntity<>(appointmentService.changeDoctor(id, newAppointmentDto), HttpStatus.OK);
    }

    @GetMapping("/doctor/{id}")
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsByDoctorId(@PathVariable("id") Long id) {
        log.info("Retrieving appointments by doctor id...");
        return new ResponseEntity<>(appointmentService.findByDoctor(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDto>> getAllAppointments() {
        log.info("Retrieving all appointments...");
        return new ResponseEntity<>(appointmentService.findAllAppointments(), HttpStatus.OK);
    }

    @GetMapping("/doctorstatus/{id}")
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsByStatusAndDoctor(@PathVariable("id") Long doctorId,
                                                                                     @RequestParam("status") Status status) {
        log.info("Retrieving all appointments by status and patient...");
        return new ResponseEntity<>(appointmentService.findByStatusAndDoctor(status, doctorId), HttpStatus.OK);
    }

    @PatchMapping("/feedback/{id}")
    public ResponseEntity<AppointmentDto> updateAppointmentFeedback(@PathVariable("id") Long id,
                                                                    @RequestBody AppointmentDto appointmentDto) {
        log.info("Updating appointment feedback...");
        return new ResponseEntity<>(appointmentService.updateAppointmentFeedback(id, appointmentDto), HttpStatus.OK);
    }
}