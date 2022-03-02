package com.ikub.reservationapp.mongodb.controller;

import com.ikub.reservationapp.mongodb.dto.AppointmentDto;
import com.ikub.reservationapp.mongodb.dto.AppointmentResponseDto;
import com.ikub.reservationapp.mongodb.model.AppointmentSlotDto;
import com.ikub.reservationapp.mongodb.service.AppointmentMongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment-mongo")
@Slf4j
public class AppointmentMongoController {

    @Autowired
    private AppointmentMongoService appointmentMongoService;

    @PostMapping("/save")
    public ResponseEntity<AppointmentResponseDto> createAppointment(@RequestBody AppointmentDto appointmentModelDto) {
        log.info("Posting appointment in mongodb...");
        return new ResponseEntity<>(appointmentMongoService.createAppointment(appointmentModelDto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDto> getAppointmentById(@PathVariable String id) {
        log.info("Retrieving appointments by id in mongodb...");
        return new ResponseEntity<>(appointmentMongoService.getAppointmentById(id), HttpStatus.OK);
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsForPatient(@PathVariable String id) {
        log.info("Retrieving appointments for patient in mongodb...");
        return new ResponseEntity<>(appointmentMongoService.getPatientAppointments(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> updateAppointment(@PathVariable String id, @RequestBody AppointmentDto appointmentDto) {
        log.info("Updating appointment in mongodb...");
        return new ResponseEntity<>(appointmentMongoService.updateAppointment(id, appointmentDto), HttpStatus.OK);
    }

    @GetMapping("/available")
    public ResponseEntity<List<AppointmentSlotDto>> getAvailableHours() {
        log.info("Retrieving available hours for patient...");
        return new ResponseEntity<>(appointmentMongoService.getAvailableHours(), HttpStatus.OK);
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<Object> cancelAppointment(@PathVariable("id") String appointmentId) {
        log.info("Cancel appointment...");
        return new ResponseEntity<>(appointmentMongoService.cancelAppointment(appointmentId), HttpStatus.OK);
    }
}
