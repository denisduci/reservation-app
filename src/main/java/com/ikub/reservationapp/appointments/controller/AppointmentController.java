package com.ikub.reservationapp.appointments.controller;

import com.ikub.reservationapp.appointments.dto.*;
import com.ikub.reservationapp.appointments.dto.reports.DoctorReportDto;
import com.ikub.reservationapp.appointments.dto.reports.WeeklyMonthlyReportDto;
import com.ikub.reservationapp.appointments.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.appointments.service.reports.ReportService;
import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.appointments.service.AppointmentService;;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private ReportService reportService;

    /**
     *
     * @return AppointmentDateHourDto all available hours for booking. If doctor is logged in the return doctor available hours
     */
    @PreAuthorize("hasAnyRole('ROLE_PATIENT','ROLE_SECRETARY','ROLE_ADMIN','ROLE_DOCTOR')")
    @GetMapping("/available")
    public ResponseEntity<AppointmentDateHourDto> getAllAvailableHours() {
        log.info("Retrieving all available hours...");
        return new ResponseEntity<>(appointmentService.findAvailableHours(), HttpStatus.OK);
    }

    /**
     *
     * @param appointmentDto appointment to create
     * @return AppointmentDto the created appointment
     */
    @PreAuthorize("hasAnyRole('ROLE_PATIENT','ROLE_SECRETARY','ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<AppointmentDto> createAppointment(@Valid @RequestBody AppointmentDto appointmentDto) throws ReservationAppException {
        log.info("Creating an appointment...");
        return new ResponseEntity<>(appointmentService.createAppointment(appointmentDto), HttpStatus.OK);
    }

    /**
     *
     * @param appointmentDto appointment to cancel
     * @return AppointmentDto the canceled appointment
     */
    @PreAuthorize("hasAnyRole('ROLE_PATIENT','ROLE_SECRETARY', 'ROLE_DOCTOR')")
    @PutMapping("/cancel")
    public ResponseEntity<AppointmentDto> cancelAppointment(@RequestBody AppointmentDto appointmentDto) throws ReservationAppException {
        log.info("Canceling an appointment...");
        return new ResponseEntity<>(appointmentService.cancelAppointment(appointmentDto), HttpStatus.OK);
    }

    /**
     *
     * @param patientId to search appointment
     * @param status of the appointment to search
     * @return List<AppointmentDto> appointments of patient in this status
     * @throws AppointmentNotFoundException
     */
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/patientstatus/{id}")
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsByStatusAndPatient(@PathVariable("id") Long patientId,
                                                                                     @RequestParam("status") Status status) throws AppointmentNotFoundException {
        log.info("Retrieving all appointments by status and patient...");
        return new ResponseEntity<>(appointmentService.findByStatusAndPatient(status, patientId), HttpStatus.OK);
    }

    /**
     *
     * @param status of the appointment to search
     * @return List<AppointmentDto> all appointments in this status
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @GetMapping("/status")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByStatus(@RequestParam("status") Status status) {
        log.info("Retrieving appointments by status...");
        return new ResponseEntity<>(appointmentService.findByStatus(status), HttpStatus.OK);
    }

    /**
     *
     * @param id of the patient
     * @return List<AppointmentDto> list of all patient appointments in all statuses
     */
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/patient/{id}")
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsByPatientId(@PathVariable("id") Long id) {
        log.info("Retrieving appointments by patient id...");
        return new ResponseEntity<>(appointmentService.findByPatient(id), HttpStatus.OK);
    }

    /**
     *
     * @param appointmentDto appointment to update
     * @return AppointmentDto updated appointment
     */
    @PreAuthorize("hasAnyRole('ROLE_DOCTOR','ROLE_PATIENT','ROLE_SECRETARY')")
    @PutMapping
    public ResponseEntity<AppointmentDto> updateAppointment(@RequestBody AppointmentDto appointmentDto) {
        log.info("Updating appointment...");
        return new ResponseEntity<>(appointmentService.updateAppointment(appointmentDto), HttpStatus.OK);
    }

    /**
     *
     * @param newAppointmentDto appointment with the new doctor
     * @return AppointmentDto updated appointment
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @PutMapping("/change")
    public ResponseEntity<AppointmentDto> changeDoctor(@RequestBody AppointmentDto newAppointmentDto) throws ReservationAppException {
        log.info("Changing doctor for an appointment...");
        return new ResponseEntity<>(appointmentService.changeDoctor(newAppointmentDto), HttpStatus.OK);
    }

    /**
     *
     * @param id of the doctor to search
     * @return List<AppointmentDto> all appointments of the specific doctor
     */
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/doctor/{id}")
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsByDoctorId(@PathVariable("id") Long id) {
        log.info("Retrieving appointments by doctor id...");
        return new ResponseEntity<>(appointmentService.findByDoctor(id), HttpStatus.OK);
    }

    /**
     *
     * @return List<AppointmentDto> all appointments in all statuses
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @GetMapping
    public ResponseEntity<List<AppointmentDto>> getAllAppointments() {
        log.info("Retrieving all appointments...");
        return new ResponseEntity<>(appointmentService.findAllAppointments(), HttpStatus.OK);
    }

    /**
     *
     * @param doctorId id of the doctor to search
     * @param status status of the appointment
     * @return List<AppointmentDto> list of all appointments in this status for the specific doctor
     */
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/doctorstatus/{id}")
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsByStatusAndDoctor(@PathVariable("id") Long doctorId,
                                                                                     @RequestParam("status") Status status) {
        log.info("Retrieving all appointments by status and patient...");
        return new ResponseEntity<>(appointmentService.findByStatusAndDoctor(status, doctorId), HttpStatus.OK);
    }

    /**
     *
     * @param appointmentDto appointment to update feedback
     * @return AppointmentDto with the updated feedback
     */
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PutMapping("/feedback")
    public ResponseEntity<AppointmentDto> updateAppointmentFeedback(@RequestBody AppointmentDto appointmentDto) {
        log.info("Updating appointment feedback...");
        return new ResponseEntity<>(appointmentService.updateAppointmentFeedback(appointmentDto), HttpStatus.OK);
    }

    /**
     *
     * @return String message for the cron job executed
     */
    @PutMapping("/default")
    public ResponseEntity<String> updateDefaultAppointmentFeedback() {
        log.info("Updating default appointment feedback...");
        return new ResponseEntity<>(appointmentService.updateDefaultFeedback(), HttpStatus.OK);
    }

    /**
     *
     * @param type of the report weekly or monthly
     * @return List<WeeklyMonthlyReportDto> list with appointments based on @type weekly | monthly
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @GetMapping("/report")
    public ResponseEntity<List<WeeklyMonthlyReportDto>> generateReport(@RequestParam("type") String type) {
        log.info("Generating report based on week/month...");
        return new ResponseEntity<>(reportService.findReports(type), HttpStatus.OK);
    }

    /**
     *
     * @return List<DoctorReportDto> list with reports based on doctor's maximum
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @GetMapping("/report/doctor")
    public ResponseEntity<List<DoctorReportDto>> generateDoctorReport() {
        log.info("Generating report based on doctor...");
        return new ResponseEntity<>(reportService.findDoctorsReport(), HttpStatus.OK);
    }
}