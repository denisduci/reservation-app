package com.ikub.reservationapp.appointments.controller;

import com.ikub.reservationapp.appointments.dto.*;
import com.ikub.reservationapp.appointments.dto.reports.DoctorReportDto;
import com.ikub.reservationapp.appointments.dto.reports.WeeklyMonthlyReportDto;
import com.ikub.reservationapp.appointments.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.appointments.service.reports.ReportService;
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
    @PreAuthorize("hasAnyRole('ROLE_PATIENT','ROLE_SECRETARY','ROLE_DOCTOR')")
    @GetMapping("/available")
    public ResponseEntity<AppointmentDateHourDto> getAllAvailableHours() {
        log.info("Retrieving all available hours...");
        return new ResponseEntity<>(appointmentService.getAllAvailableHours(), HttpStatus.OK);
    }

    /**
     *
     * @param appointmentDto appointment to create
     * @return AppointmentDto the created appointment
     */
    @PreAuthorize("hasAnyRole('ROLE_PATIENT','ROLE_SECRETARY')")
    @PostMapping
    public ResponseEntity<AppointmentResponseDto> createAppointment(@Valid @RequestBody AppointmentDto appointmentDto) throws ReservationAppException {
        log.info("Creating an appointment...");
        return new ResponseEntity<>(appointmentService.createAppointment(appointmentDto), HttpStatus.OK);
    }

    /**
     *
     * @param appointmentId appointment to cancel
     * @return AppointmentDto the canceled appointment
     */
    @PreAuthorize("hasAnyRole('ROLE_PATIENT','ROLE_SECRETARY', 'ROLE_DOCTOR')")
    @PutMapping("/cancel/{id}")
    public ResponseEntity<AppointmentDto> cancelAppointment(@PathVariable("id") Long appointmentId) throws ReservationAppException {
        log.info("Canceling an appointment...");
        return new ResponseEntity<>(appointmentService.cancelAppointment(appointmentId), HttpStatus.OK);
    }

    /**
     *
     * @param patientId patient to search
     * @return <List<AppointmentDto> canceled appointments
     * @throws AppointmentNotFoundException
     */
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/patient/canceled/{id}")
    public ResponseEntity<List<AppointmentDto>> getPatientCanceledAppointments(@PathVariable("id") Long patientId) throws AppointmentNotFoundException {
        log.info("Retrieving all canceled appointments for patient...");
        return new ResponseEntity<>(appointmentService.getPatientCanceledAppointments(patientId), HttpStatus.OK);
    }

    /**
     *
     * @param patientId to search appointment
     * @return List<AppointmentDto> active appointments
     * @throws AppointmentNotFoundException
     */
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/patient/active/{id}")
    public ResponseEntity<List<AppointmentDto>> getPatientActiveAppointments(@PathVariable("id") Long patientId) throws AppointmentNotFoundException {
        log.info("Retrieving all active appointments for patient...");
        return new ResponseEntity<>(appointmentService.getPatientActiveAppointments(patientId), HttpStatus.OK);
    }

    /**
     *
     * @param patientId to search appointment
     * @return List<AppointmentDto> finished appointments
     * @throws AppointmentNotFoundException
     */
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/patient/done/{id}")
    public ResponseEntity<List<AppointmentDto>> getPatientFinishedAppointments(@PathVariable("id") Long patientId) throws AppointmentNotFoundException {
        log.info("Retrieving all DONE appointments for patient...");
        return new ResponseEntity<>(appointmentService.getPatientFinishedAppointments(patientId), HttpStatus.OK);
    }

    /**
     *
     * @return List<AppointmentDto> all pending appointments
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @GetMapping("/status/pending")
    public ResponseEntity<List<AppointmentDto>> getAllPendingAppointments() {
        log.info("Retrieving pending appointments...");
        return new ResponseEntity<>(appointmentService.getAllPendingAppointments(), HttpStatus.OK);
    }

    /**
     *
     * @return List<AppointmentDto> all finished appointments
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @GetMapping("/status/done")
    public ResponseEntity<List<AppointmentDto>> getAllFinishedAppointments() {
        log.info("Retrieving finished appointments...");
        return new ResponseEntity<>(appointmentService.getAllFinishedAppointments(), HttpStatus.OK);
    }

    /**
     *
     * @return List<AppointmentDto> all canceled appointments
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @GetMapping("/status/cancel")
    public ResponseEntity<List<AppointmentDto>> getAllCanceledAppointments() {
        log.info("Retrieving finished appointments...");
        return new ResponseEntity<>(appointmentService.getAllCanceledAppointments(), HttpStatus.OK);
    }

    /**
     *
     * @return  Approve appointment
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @PutMapping("/approve/{id}")
    public ResponseEntity<AppointmentResponseDto> approveAppointment(@PathVariable("id") Long id) throws ReservationAppException {
        log.info("Approve appointment...");
        return new ResponseEntity<>(appointmentService.approveAppointment(id), HttpStatus.OK);
    }

    /**
     *
     * @return  Approve appointment
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @PutMapping("/done/{id}")
    public ResponseEntity<AppointmentResponseDto> setAppointmentToDone(@PathVariable("id") Long appointmentId) throws ReservationAppException {
        log.info("Approve appointment...");
        return new ResponseEntity<>(appointmentService.setAppointmentToDone(appointmentId), HttpStatus.OK);
    }

    /**
     *
     * @param patientId of the patient
     * @return List<AppointmentDto> list of all patient appointments in all statuses
     */
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/patient/all/{id}")
    public ResponseEntity<List<AppointmentDto>> getPatientAllAppointments(@PathVariable("id") Long patientId) {
        log.info("Retrieving appointments by patient id...");
        return new ResponseEntity<>(appointmentService.getPatientAllAppointments(patientId), HttpStatus.OK);
    }

    /**
     *
     * @param appointmentDto appointment to update
     * @return AppointmentDto updated appointment
     */
    @PreAuthorize("hasAnyRole('ROLE_DOCTOR','ROLE_PATIENT','ROLE_SECRETARY')")
    @PutMapping
    public ResponseEntity<AppointmentDto> updateAppointment(@RequestBody @Valid AppointmentDto appointmentDto) {
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
     * @param newAppointmentDto appointment with the new doctor
     * @return AppointmentDto updated appointment approved/rejected
     */
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @PutMapping("/patient/doctor-change")
    public ResponseEntity<AppointmentResponseDto> approveOrRejectDoctorChange(@RequestBody AppointmentDto newAppointmentDto) throws ReservationAppException {
        log.info("Approve/reject doctor for an appointment...");
        return new ResponseEntity<>(appointmentService.approveOrRejectDoctorChange(newAppointmentDto), HttpStatus.OK);
    }

    /**
     *
     * @param doctorId of the doctor to search
     * @return List<AppointmentDto> all appointments of the specific doctor
     */
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/doctor/{id}")
    public ResponseEntity<List<AppointmentDto>> getDoctorAllAppointments(@PathVariable("id") Long doctorId) {
        log.info("Retrieving appointments by doctor id...");
        return new ResponseEntity<>(appointmentService.getDoctorAllAppointments(doctorId), HttpStatus.OK);
    }

    /**
     *
     * @return List<AppointmentDto> all appointments in all statuses
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @GetMapping
    public ResponseEntity<List<AppointmentDto>> getAllAppointments() {
        log.info("Retrieving all appointments...");
        return new ResponseEntity<>(appointmentService.getAllAppointments(), HttpStatus.OK);
    }

    /**
     *
     * @param doctorId id of the doctor to search
     * @return List<AppointmentDto> list of all appointments in this status for the specific doctor
     */
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/doctor/active/{id}")
    public ResponseEntity<List<AppointmentDto>> getDoctorActiveAppointments(@PathVariable("id") Long doctorId) {
        log.info("Retrieving all appointments by status and doctor...");
        return new ResponseEntity<>(appointmentService.getDoctorActiveAppointments(doctorId), HttpStatus.OK);
    }

    /**
     *
     * @param doctorId id of the doctor to search
     * @return List<AppointmentDto> list of all appointments in this status for the specific doctor
     */
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/doctor/canceled/{id}")
    public ResponseEntity<List<AppointmentDto>> getDoctorCanceledAppointments(@PathVariable("id") Long doctorId) {
        log.info("Retrieving all appointments by status and doctor...");
        return new ResponseEntity<>(appointmentService.getDoctorCanceledAppointments(doctorId), HttpStatus.OK);
    }

    /**
     *
     * @param doctorId id of the doctor to search
     * @return List<AppointmentDto> list of all appointments in this status for the specific doctor
     */
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/doctor/done/{id}")
    public ResponseEntity<List<AppointmentDto>> getDoctorFinishedAppointments(@PathVariable("id") Long doctorId) {
        log.info("Retrieving all appointments by status and doctor...");
        return new ResponseEntity<>(appointmentService.getDoctorFinishedAppointments(doctorId), HttpStatus.OK);
    }


    /**
     *
     * @param appointmentDto appointment to update feedback
     * @return AppointmentDto with the updated feedback
     */
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PutMapping("/feedback")
    public ResponseEntity<AppointmentResponseDto> updateAppointmentFeedback(@RequestBody AppointmentDto appointmentDto) {
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