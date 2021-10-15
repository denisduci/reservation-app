package com.ikub.reservationapp.appointments.validators;

import com.ikub.reservationapp.appointments.constants.AppointmentConstants;
import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.common.exception.BadRequest;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
public final class AppointmentValidator {

    public static void validateAppointment(AppointmentDto appointmentDto) throws ReservationAppException {

        if (appointmentDto == null) {
            log.error("Appointment missing: -> {}", appointmentDto);
            throw new ReservationAppException(BadRequest.APPOINTMENT_MISSING.getMessage());
        }
        if (appointmentDto.getAppointmentDate() == null) {
            log.error("Appointment date missing: -> {}", appointmentDto.getAppointmentDate());
            throw new ReservationAppException(BadRequest.APPOINTMENT_DATE_MISSING.getMessage());
        }
        if (appointmentDto.getStartTime() == null || appointmentDto.getEndTime() == null) {
            log.error("Appointment start time or end time missing: -> {} {}", appointmentDto.getStartTime(), appointmentDto.getEndTime());
            throw new ReservationAppException(BadRequest.APPOINTMENT_TIME_MISSING.getMessage());
        }
        if (appointmentDto.getDoctor() == null || appointmentDto.getDoctor().getId() == null) {
            log.error("Appointment doctor missing: -> {}", appointmentDto.getDoctor().getId());
            throw new ReservationAppException(BadRequest.DOCTOR_MISSING.getMessage());
        }
        if (appointmentDto.getPatient() == null || appointmentDto.getPatient().getId() == null) {
            log.error("Appointment patient missing: -> {}", appointmentDto.getPatient().getId());
            throw new ReservationAppException(BadRequest.PATIENT_MISSING.getMessage());
        }
        if (appointmentDto.getStartTime().getHour() < AppointmentConstants.START_TIME || appointmentDto.getStartTime().getHour() >= AppointmentConstants.END_TIME ||
                appointmentDto.getAppointmentDate().getDayOfWeek() == DayOfWeek.SATURDAY || appointmentDto.getAppointmentDate().getDayOfWeek() == DayOfWeek.SUNDAY) {
            log.error("Appointment is out business hours: start -> {} and end: -> {}", appointmentDto.getStartTime(), appointmentDto.getEndTime());
            throw new ReservationAppException(BadRequest.APPOINTMENT_OUT_OF_HOURS.getMessage());
        }
        if (appointmentDto.getAppointmentDate().isBefore(LocalDate.now())) {
            log.error("Date selected is wrong: -> {}", appointmentDto.getAppointmentDate());
            throw new ReservationAppException(BadRequest.APPOINTMENT_DATE_NOT_VALID.getMessage());
        }
        if (appointmentDto.getStartTime().isBefore(LocalDateTime.now())) {
            log.error("Start time is not valid: -> {}", appointmentDto.getStartTime());
            throw new ReservationAppException(BadRequest.START_TIME_NOT_VALID.getMessage());
        }
    }

//    public AppointmentEntity validateAndSetFields(AppointmentEntity currentAppointment, AppointmentDto newAppointment) {
//
//        if (newAppointment.getStatus() != null)
//            currentAppointment.setStatus(newAppointment.getStatus());
//        if (newAppointment.getFeedback() != null)
//            currentAppointment.setFeedback(newAppointment.getFeedback());
//        if (newAppointment.getAppointmentDate() != null)
//            currentAppointment.setAppointmentDate(newAppointment.getAppointmentDate());
//        if (newAppointment.getStartTime() != null)
//            currentAppointment.setStartTime(newAppointment.getStartTime());
//        if (newAppointment.getEndTime() != null)
//            currentAppointment.setEndTime(newAppointment.getEndTime());
//        if (newAppointment.getDoctor() != null)
//            currentAppointment.setDoctor(userMapper.toEntity(newAppointment.getDoctor()));
//        if (newAppointment.getPatient() != null)
//            currentAppointment.setPatient(userMapper.toEntity(newAppointment.getPatient()));
//        if (newAppointment.getDescription() != null)
//            currentAppointment.setDescription(newAppointment.getDescription());
//        if (newAppointment.getComments() != null)
//            currentAppointment.setComments(newAppointment.getComments());
//
//        return currentAppointment;
//    }
}
