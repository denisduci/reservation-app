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
     * @return List<AppointmentDateTimeDto> all available hours for booking. If doctor is logged in the return doctor available hours
     */
    @PreAuthorize("hasAnyRole('ROLE_PATIENT','ROLE_SECRETARY','ROLE_DOCTOR')")
    @GetMapping("/available")
    public ResponseEntity<List<AppointmentDateTimeDto>> getAllAvailableHours() {
        log.info("Retrieving all available hours...");
        return new ResponseEntity<>(appointmentService.getAllAvailableHours(), HttpStatus.OK);
    }

    /**
     * @param appointmentDto appointment to create
     * @return AppointmentResponseDto the created appointment
     */
    @PreAuthorize("hasAnyRole('ROLE_PATIENT','ROLE_SECRETARY')")
    @PostMapping
    public ResponseEntity<AppointmentResponseDto> createAppointment(@Valid @RequestBody AppointmentDto appointmentDto) throws ReservationAppException {
        log.info("Creating an appointment...");
        return new ResponseEntity<>(appointmentService.createAppointment(appointmentDto), HttpStatus.OK);
    }

    /**
     * @param appointmentDto appointment to cancel
     * @return AppointmentResponseDto the canceled appointment
     */
    @PreAuthorize("hasAnyRole('ROLE_PATIENT','ROLE_SECRETARY', 'ROLE_DOCTOR')")
    @PutMapping("/cancel")
    public ResponseEntity<AppointmentResponseDto> cancelAppointment(@RequestBody AppointmentDto appointmentDto) throws ReservationAppException {
        log.info("Canceling an appointment...");
        return new ResponseEntity<>(appointmentService.cancelAppointment(appointmentDto), HttpStatus.OK);
    }

    /**
     * @return <List<AppointmentResponseDto> canceled appointments
     * @throws AppointmentNotFoundException
     */
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/patient/canceled")
    public ResponseEntity<List<AppointmentResponseDto>> getPatientCanceledAppointments() throws AppointmentNotFoundException {
        log.info("Retrieving all canceled appointments for patient...");
        return new ResponseEntity<>(appointmentService.getPatientCanceledAppointments(), HttpStatus.OK);
    }

    /**
     * @return List<AppointmentResponseDto> active appointments
     * @throws AppointmentNotFoundException
     */
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/patient/active")
    public ResponseEntity<List<AppointmentResponseDto>> getPatientActiveAppointments() throws AppointmentNotFoundException {
        log.info("Retrieving all active appointments for patient...");
        return new ResponseEntity<>(appointmentService.getPatientActiveAppointments(), HttpStatus.OK);
    }

    /**
     * @return List<AppointmentResponseDto> finished appointments
     * @throws AppointmentNotFoundException
     */
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/patient/done")
    public ResponseEntity<List<AppointmentResponseDto>> getPatientFinishedAppointments() throws AppointmentNotFoundException {
        log.info("Retrieving all DONE appointments for patient...");
        return new ResponseEntity<>(appointmentService.getPatientFinishedAppointments(), HttpStatus.OK);
    }

    /**
     * @param searchRequestDto pageNumber, pageSize
     * @return List<AppointmentResponseDto> list of all patient appointments in all statuses
     */
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/patient/all")
    public ResponseEntity<List<AppointmentResponseDto>> getPatientAllAppointments(@RequestBody AppointmentSearchRequestDto searchRequestDto) {
        log.info("Retrieving appointments by patient id...");
        return new ResponseEntity<>(appointmentService.getPatientAllAppointments(searchRequestDto), HttpStatus.OK);
    }

    /**
     * @return List<AppointmentResponseDto> all pending appointments
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @GetMapping("/pending")
    public ResponseEntity<List<AppointmentResponseDto>> getAllPendingAppointments(@RequestBody AppointmentSearchRequestDto searchRequestDto) {
        log.info("Retrieving pending appointments...");
        return new ResponseEntity<>(appointmentService.getAllPendingAppointments(searchRequestDto), HttpStatus.OK);
    }

    /**
     * @return List<AppointmentResponseDto> all finished appointments
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @GetMapping("/done")
    public ResponseEntity<List<AppointmentResponseDto>> getAllFinishedAppointments(@RequestBody AppointmentSearchRequestDto searchRequestDto) {
        log.info("Retrieving all finished appointments...");
        return new ResponseEntity<>(appointmentService.getAllFinishedAppointments(searchRequestDto), HttpStatus.OK);
    }

    /**
     * @return List<AppointmentResponseDto> all canceled appointments
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @GetMapping("/canceled")
    public ResponseEntity<List<AppointmentResponseDto>> getAllCanceledAppointments(@RequestBody AppointmentSearchRequestDto searchRequestDto) {
        log.info("Retrieving canceled appointments...");
        return new ResponseEntity<>(appointmentService.getAllCanceledAppointments(searchRequestDto), HttpStatus.OK);
    }

    /**
     * @return List<AppointmentResponseDto> all approved appointments
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @GetMapping("/approved")
    public ResponseEntity<List<AppointmentResponseDto>> getAllApprovedAppointments(@RequestBody AppointmentSearchRequestDto searchRequestDto) {
        log.info("Retrieving approved appointments...");
        return new ResponseEntity<>(appointmentService.getAllApprovedAppointments(searchRequestDto), HttpStatus.OK);
    }

    /**
     * @return List<AppointmentResponseDto> all appointments by status and pagination
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @GetMapping("/status/pagination")
    public ResponseEntity<List<AppointmentResponseDto>> getAllAppointmentsWithStatusAndPagination(@RequestBody AppointmentSearchRequestDto searchRequestDto) {
        log.info("Retrieving finished appointments...");
        return new ResponseEntity<>(appointmentService.getAllAppointmentsWithStatusAndPagination(searchRequestDto), HttpStatus.OK);
    }

    /**
     * @return Approve appointment
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @PutMapping("/approve/{id}")
    public ResponseEntity<AppointmentResponseDto> approveAppointment(@PathVariable("id") Long appointmentId) throws ReservationAppException {
        log.info("Approve appointment...");
        return new ResponseEntity<>(appointmentService.approveAppointment(appointmentId), HttpStatus.OK);
    }

    /**
     *
     * @param currentAppointment based on which the suggestion will happen
     * @param suggestedAppointment suggested appointment
     * @return
     * @throws ReservationAppException
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @GetMapping("/suggest/{id}")
    public ResponseEntity<AppointmentResponseDto> suggestTime(@PathVariable("id") Long currentAppointment, @RequestBody AppointmentDto suggestedAppointment) throws ReservationAppException {
        log.info("Suggesting new appointment...");
        return new ResponseEntity<>(appointmentService.suggestTime(currentAppointment, suggestedAppointment), HttpStatus.OK);
    }

    /**
     * @return Appointment DONE
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @PutMapping("/done/{id}")
    public ResponseEntity<AppointmentResponseDto> setAppointmentToDone(@PathVariable("id") Long appointmentId) throws ReservationAppException {
        log.info("Approve appointment...");
        return new ResponseEntity<>(appointmentService.setAppointmentToDone(appointmentId), HttpStatus.OK);
    }

    /**
     * @param appointmentDto appointment to update
     * @return AppointmentResponseDto updated appointment
     */
    @PreAuthorize("hasAnyRole('ROLE_DOCTOR','ROLE_PATIENT','ROLE_SECRETARY')")
    @PutMapping
    public ResponseEntity<AppointmentResponseDto> updateAppointment(@RequestBody @Valid AppointmentDto appointmentDto) {
        log.info("Updating appointment...");
        return new ResponseEntity<>(appointmentService.updateAppointment(appointmentDto), HttpStatus.OK);
    }

    /**
     * @param newAppointmentDto appointment with the new doctor
     * @return AppointmentResponseDto updated appointment
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @PutMapping("/change")
    public ResponseEntity<AppointmentResponseDto> changeDoctor(@RequestBody AppointmentDto newAppointmentDto) throws ReservationAppException {
        log.info("Changing doctor for an appointment...");
        return new ResponseEntity<>(appointmentService.changeDoctor(newAppointmentDto), HttpStatus.OK);
    }

    /**
     * @param newAppointmentDto appointment with the new doctor
     * @return AppointmentResponseDto updated appointment approved/rejected
     */
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @PutMapping("/patient/doctor-change")
    public ResponseEntity<AppointmentResponseDto> approveOrRejectDoctorChange(@RequestBody AppointmentDto newAppointmentDto) throws ReservationAppException {
        log.info("Approve/reject doctor for an appointment...");
        return new ResponseEntity<>(appointmentService.approveOrRejectDoctorChange(newAppointmentDto), HttpStatus.OK);
    }

    /**
     * @return List<AppointmentResponseDto> all appointments of the specific doctor
     */
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/doctor/all")
    public ResponseEntity<List<AppointmentResponseDto>> getDoctorAllAppointments(@RequestBody AppointmentSearchRequestDto searchRequestDto) {
        log.info("Retrieving appointments by doctor id...");
        return new ResponseEntity<>(appointmentService.getDoctorAllAppointments(searchRequestDto), HttpStatus.OK);
    }

    /**
     * @return List<AppointmentResponseDto> list of all appointments in this status for the specific doctor
     */
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/doctor/active")
    public ResponseEntity<List<AppointmentResponseDto>> getDoctorActiveAppointments(@RequestBody AppointmentSearchRequestDto searchRequestDto) {
        log.info("Retrieving all appointments by status and doctor...");
        return new ResponseEntity<>(appointmentService.getDoctorActiveAppointments(searchRequestDto), HttpStatus.OK);
    }

    /**
     * @return List<AppointmentResponseDto> list of all appointments in this status for the specific doctor
     */
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/doctor/canceled")
    public ResponseEntity<List<AppointmentResponseDto>> getDoctorCanceledAppointments(@RequestBody AppointmentSearchRequestDto searchRequestDto) {
        log.info("Retrieving all appointments by status and doctor...");
        return new ResponseEntity<>(appointmentService.getDoctorCanceledAppointments(searchRequestDto), HttpStatus.OK);
    }

    /**
     * @return List<AppointmentResponseDto> list of all appointments in this status for the specific doctor
     */
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/doctor/done")
    public ResponseEntity<List<AppointmentResponseDto>> getDoctorFinishedAppointments(@RequestBody AppointmentSearchRequestDto searchRequestDto) {
        log.info("Retrieving all appointments by status and doctor...");
        return new ResponseEntity<>(appointmentService.getDoctorFinishedAppointments(searchRequestDto), HttpStatus.OK);
    }


    /**
     * @return List<AppointmentResponseDto> all appointments in all statuses
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDto>> getAllAppointments(@RequestBody AppointmentSearchRequestDto searchRequestDto) {
        log.info("Retrieving all appointments...");
        return new ResponseEntity<>(appointmentService.getAllAppointments(searchRequestDto), HttpStatus.OK);
    }

    /**
     * @param appointmentDto appointment to update feedback
     * @return AppointmentResponseDto with the updated feedback
     */
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PutMapping("/feedback")
    public ResponseEntity<AppointmentResponseDto> updateAppointmentFeedback(@RequestBody AppointmentDto appointmentDto) {
        log.info("Updating appointment feedback...");
        return new ResponseEntity<>(appointmentService.updateAppointmentFeedback(appointmentDto), HttpStatus.OK);
    }

    /**
     * @return String message for the cron job executed
     */
    @PutMapping("/default")
    public ResponseEntity<String> updateDefaultAppointmentFeedback() {
        log.info("Updating default appointment feedback...");
        return new ResponseEntity<>(appointmentService.updateDefaultFeedback(), HttpStatus.OK);
    }

    /**
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
     * @return List<DoctorReportDto> list with reports based on doctor's maximum
     */
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    @GetMapping("/report/doctor")
    public ResponseEntity<List<DoctorReportDto>> generateDoctorReport() {
        log.info("Generating report based on doctor...");
        return new ResponseEntity<>(reportService.findDoctorsReport(), HttpStatus.OK);
    }

    /**
     *
     * @param searchRequestDto date, doctorName, patientName, pageNumber, pageSize
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_SECRETARY', 'ROLE_DOCTOR')")
    @GetMapping("/specification")
    public ResponseEntity<List<AppointmentResponseDto>> searchAppointmentWithSpecification(@RequestBody AppointmentSearchRequestDto searchRequestDto) {
        log.info("Retrieving appointments with specification...");
        return new ResponseEntity<>(appointmentService.searchAppointmentWithSpecification(searchRequestDto), HttpStatus.OK);
    }
}