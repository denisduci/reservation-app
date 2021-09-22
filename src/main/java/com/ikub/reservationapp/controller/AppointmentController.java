package com.ikub.reservationapp.controller;

import com.ikub.reservationapp.dto.AppointmentDto;
import com.ikub.reservationapp.entity.Appointment;
import com.ikub.reservationapp.entity.Doctor;
import com.ikub.reservationapp.entity.Patient;
import com.ikub.reservationapp.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.exception.GeneralException;
import com.ikub.reservationapp.exception.PatientNotFoundException;
import com.ikub.reservationapp.mapper.MapStructMapper;
import com.ikub.reservationapp.service.AppointmentService;
import com.ikub.reservationapp.service.DoctorService;
import com.ikub.reservationapp.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private MapStructMapper mapStructMapper;

    @GetMapping("/available")
    public ResponseEntity<List<AppointmentDto>> getAllAvailableAppointments() {
        log.info("Retrieving available appointments...");
        List<AppointmentDto> appointmentDtos = appointmentService.findAvailableAppointments()
                .stream().map(appointment -> mapStructMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
        return new ResponseEntity<>(appointmentDtos, HttpStatus.OK);
    }

    @PatchMapping("/reserve/{id}")
    public ResponseEntity<AppointmentDto> reserveAppointment(@PathVariable("id") Long appointmentId,
                                                             @RequestBody AppointmentDto newAppointmentDto) throws AppointmentNotFoundException, PatientNotFoundException, GeneralException {
        log.info("Reserving an appointment...");
        Appointment appointment = appointmentService.findById(appointmentId);
        //Patient patient = patientService.findById(patientId);
        return new ResponseEntity<>(
                mapStructMapper.appointmentToAppointmentDto(
                        appointmentService.reserveAppointment(
                                mapStructMapper.appointmentDtoToAppointment(newAppointmentDto))), HttpStatus.OK);
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<AppointmentDto> cancelAppointment(@PathVariable("id") Long id) {
        log.info("Canceling an appointment...");
        Appointment appointment = appointmentService.findById(id);
        return new ResponseEntity<>(
                mapStructMapper.appointmentToAppointmentDto(
                        appointmentService.cancelAppointment(appointment)), HttpStatus.OK);
    }

    @PatchMapping("/change/{id}")
    public ResponseEntity<Appointment> changeDoctor(@PathVariable("id") Long id,
                                                    @RequestBody Appointment newAppointment) {
        log.info("Changing doctor for an appointment...");
        Appointment appointment = appointmentService.findById(id);
        appointment.setDoctor(newAppointment.getDoctor());
        appointment.setStatus(Appointment.Status.CHANGED);
        return new ResponseEntity<>(appointmentService.save(appointment), HttpStatus.OK);
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<AppointmentDto> updateAppointmentStatus(@PathVariable("id") Long id,
                                                                  @RequestBody AppointmentDto newAppointmentDto) {
        log.info("Updating appointment status...");
        Appointment appointmentEntity = appointmentService.findById(id);
        appointmentEntity.setStatus(newAppointmentDto.getStatus());
        return new ResponseEntity<>(mapStructMapper.appointmentToAppointmentDto(appointmentEntity), HttpStatus.OK);
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsByPatientId(@PathVariable("id") Long id) {
        log.info("Retrieving appointments by patient id...");
        Patient patient = patientService.findById(id);
        List<AppointmentDto> appointmentDtos = appointmentService.findByPatient(patient)
                .stream().map(appointment ->mapStructMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
        return new ResponseEntity<>(appointmentDtos, HttpStatus.OK);
    }

    @GetMapping("/doctor/{id}")
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsByDoctorId(@PathVariable("id") Long id) {
        log.info("Retrieving appointments by doctor id...");
        Doctor doctor = doctorService.findById(id);
        List<AppointmentDto> appointmentDtos = appointmentService.findByDoctor(doctor)
                .stream().map(appointment -> mapStructMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
        return new ResponseEntity<>(appointmentDtos, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByStatus(@PathVariable Appointment.Status status) {
        log.info("Retrieving appointments by status...");
        List<AppointmentDto> appointmentDtos = appointmentService.findByStatus(status)
                .stream().map(appointment -> mapStructMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
        return new ResponseEntity<>(appointmentDtos, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDto>> getAllAppointments() {
        log.info("Retrieving all appointments...");
        List<AppointmentDto> appointmentDtos = appointmentService.findAllAppointments()
                .stream().map(appointment -> mapStructMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
        return new ResponseEntity<>(appointmentDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AppointmentDto> saveAppointment(@RequestBody AppointmentDto appointmentDTO){
        log.info("Saving appointment...");
        Appointment appointment = appointmentService.save(mapStructMapper.appointmentDtoToAppointment(appointmentDTO));
        return  new ResponseEntity<>(mapStructMapper.appointmentToAppointmentDto(appointment), HttpStatus.CREATED);

    }
}