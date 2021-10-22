package com.ikub.reservationapp.appointments.validators;

import com.ikub.reservationapp.appointments.constants.AppointmentConstants;
import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.appointments.dto.AppointmentSearchRequestDto;
import com.ikub.reservationapp.appointments.entity.AppointmentEntity;
import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.common.exception.BadRequest;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
public final class AppointmentValidator {

    public static void validateAppointment(AppointmentDto appointmentDto) throws ReservationAppException {

        if (appointmentDto == null) {
            log.error("Appointment missing");
            throw new ReservationAppException(BadRequest.APPOINTMENT_MISSING.getMessage());
        }
        if (appointmentDto.getAppointmentDate() == null) {
            log.error("Appointment date missing");
            throw new ReservationAppException(BadRequest.APPOINTMENT_DATE_MISSING.getMessage());
        }
        if (appointmentDto.getStartTime() == null || appointmentDto.getEndTime() == null) {
            log.error("Appointment start time or end time missing");
            throw new ReservationAppException(BadRequest.APPOINTMENT_TIME_MISSING.getMessage());
        }
        if (appointmentDto.getDoctor() == null || appointmentDto.getDoctor().getId() == null) {
            log.error("Appointment doctor missing");
            throw new ReservationAppException(BadRequest.DOCTOR_MISSING.getMessage());
        }
        if (appointmentDto.getPatient() == null || appointmentDto.getPatient().getId() == null) {
            log.error("Appointment patient missing");
            throw new ReservationAppException(BadRequest.PATIENT_MISSING.getMessage());
        }
        if (appointmentDto.getStartTime().getDayOfMonth() != appointmentDto.getEndTime().getDayOfMonth()) {
            log.error("Appointment day does not match with end day: -> {}", appointmentDto.getPatient().getId());
            throw new ReservationAppException(BadRequest.APPOINTMENT_OUT_OF_HOURS.getMessage());
        }
        if (appointmentDto.getEndTime().isBefore(appointmentDto.getStartTime())) {
            log.error("End time not valid, start time is -> {}, end time is -> {}", appointmentDto.getStartTime(), appointmentDto.getEndTime());
            throw new ReservationAppException(BadRequest.END_TIME_NOT_VALID.getMessage());
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

    public static void validateAppointmentFeedback(AppointmentEntity existingAppointment, AppointmentDto appointmentDto) {
        if (existingAppointment.getFeedback() != null) {
            log.error("Appointment feedback already exists: -> {}", existingAppointment.getFeedback());
            throw new ReservationAppException(BadRequest.APPOINTMENT_FEEDBACK_EXISTS.getMessage());
        }
        if (appointmentDto.getFeedback().length() > 200) {
            log.error("Appointment feedback is too long length is -> {}", appointmentDto.getFeedback().length());
            throw new ReservationAppException(BadRequest.APPOINTMENT_FEEDBACK_LONG.getMessage());
        }
        if (existingAppointment.getStatus() != Status.APPROVED) {
            log.error("Invalid appointment status for updating the feedback: -> {}", existingAppointment.getStatus());
            throw new ReservationAppException(BadRequest.INVALID_STATUS.getMessage());
        }
    }

    public static void validateAppointmentSearchDto(AppointmentSearchRequestDto searchRequestDto) {
        Optional.ofNullable(searchRequestDto)
                .orElseThrow(() -> new ReservationAppException(BadRequest.APPOINTMENT_SEARCH_FIELDS_MISSING.getMessage()));
        if (searchRequestDto.getPageSize() == null)
            searchRequestDto.setPageSize(AppointmentConstants.DEFAULT_PAGE_SIZE);
        if (searchRequestDto.getPageNumber() == null)
            searchRequestDto.setPageNumber(AppointmentConstants.DEFAULT_PAGE_NUMBER);
    }
}
