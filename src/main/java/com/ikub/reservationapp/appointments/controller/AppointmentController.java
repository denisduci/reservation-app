package com.ikub.reservationapp.appointments.controller;

import com.ikub.reservationapp.appointments.dto.AppointmentDateHourDto;
import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.appointments.service.AppointmentService;;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_SECRETARY')")
    @GetMapping("/available")
    public ResponseEntity<AppointmentDateHourDto> getAllAvailableHours() {
        log.info("Retrieving all available appointments...");
        return new ResponseEntity<>(appointmentService.findAvailableHours(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_SECRETARY')")
    @PostMapping
    public ResponseEntity<AppointmentDto> createAppointment(@RequestBody AppointmentDto appointmentDto) {
        log.info("Creating an appointment...");
        return new ResponseEntity<>(appointmentService.createAppointment(appointmentDto), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_SECRETARY', 'ROLE_DOCTOR')")
    @PutMapping("/cancel")
    public ResponseEntity<AppointmentDto> cancelAppointment(@RequestBody AppointmentDto appointmentDto) {
        log.info("Canceling an appointment...");
        return new ResponseEntity<>(appointmentService.cancelAppointment(appointmentDto), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/patientstatus/{id}")
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsByStatusAndPatient(@PathVariable("id") Long patientId,
                                                                                     @RequestParam("status") Status status) {
        log.info("Retrieving all appointments by status and patient...");
        return new ResponseEntity<>(appointmentService.findByStatusAndPatient(status, patientId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @GetMapping("/status")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByStatus(@RequestParam Status status) {
        log.info("Retrieving appointments by status...");
        return new ResponseEntity<>(appointmentService.findByStatus(status), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/patient/{id}")
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsByPatientId(@PathVariable("id") Long id) {
        log.info("Retrieving appointments by patient id...");
        return new ResponseEntity<>(appointmentService.findByPatient(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR','ROLE_USER','ROLE_SECRETARY')")
    @PutMapping
    public ResponseEntity<AppointmentDto> updateAppointment(@RequestBody AppointmentDto appointmentDto) {
        log.info("Updating appointment...");
        return new ResponseEntity<>(appointmentService.updateAppointment(appointmentDto), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/doctorHours/{id}")
    public ResponseEntity<AppointmentDateHourDto> getDoctorAvailableHours(@PathVariable Long id) {
        log.info("Retrieving doctor available appointments...");
        return new ResponseEntity<>(appointmentService.doctorAvailableTime(id), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @PutMapping("/change")
    public ResponseEntity<AppointmentDto> changeDoctor(@RequestBody AppointmentDto newAppointmentDto) {
        log.info("Changing doctor for an appointment...");
        return new ResponseEntity<>(appointmentService.changeDoctor(newAppointmentDto), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/doctor/{id}")
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsByDoctorId(@PathVariable("id") Long id) {
        log.info("Retrieving appointments by doctor id...");
        return new ResponseEntity<>(appointmentService.findByDoctor(id), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @GetMapping
    public ResponseEntity<List<AppointmentDto>> getAllAppointments() {
        log.info("Retrieving all appointments...");
        return new ResponseEntity<>(appointmentService.findAllAppointments(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/doctorstatus/{id}")
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsByStatusAndDoctor(@PathVariable("id") Long doctorId,
                                                                                     @RequestParam("status") Status status) {
        log.info("Retrieving all appointments by status and patient...");
        return new ResponseEntity<>(appointmentService.findByStatusAndDoctor(status, doctorId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PutMapping("/feedback")
    public ResponseEntity<AppointmentDto> updateAppointmentFeedback(@RequestBody AppointmentDto appointmentDto) {
        log.info("Updating appointment feedback...");
        return new ResponseEntity<>(appointmentService.updateAppointmentFeedback(appointmentDto), HttpStatus.OK);
    }

    @PutMapping("/default")
    public ResponseEntity<String> updateDefaultAppointmentFeedback() {
        log.info("Updating default appointment feedback...");
        return new ResponseEntity<>(appointmentService.updateDefaultFeedback(), HttpStatus.OK);
    }
}