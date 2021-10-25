package com.ikub.reservationapp.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BadRequest {

    APPOINTMENT_MISSING("Appointment missing!"),
    APPOINTMENT_ALREADY_EXISTS("You already have an appointment in this time!"),
    APPOINTMENT_CANCELED_OR_DONE("Appointment is already canceled or DONE!"),
    APPOINTMENT_DATE_MISSING("Appointment date is missing!"),
    APPOINTMENT_TIME_MISSING("Appointment time is missing!"),
    APPOINTMENT_FEEDBACK_EXISTS("Appointment feedback already exists!"),
    APPOINTMENT_FEEDBACK_LONG("Appointment feedback is too long!"),
    DOCTOR_MISSING("Doctor missing!"),
    COMMENT_MISSING("Comment is missing for Appointment!"),
    DOCTOR_NOT_AVAILABLE("Doctor is not available in this time!"),
    PATIENT_MISSING("Patient missing!"),
    FEEDBACK_MISSING("Cannot update to DONE. No feedback from doctor!"),
    APPOINTMENT_OUT_OF_HOURS("Appointment time is out of business hours!"),
    APPOINTMENT_DATE_NOT_VALID("The date selected is not valid. Please reserve a coming date!"),
    SHORT_TIME_TO_CANCEL("Too short time to cancel Appointment!"),
    APPOINTMENT_SEARCH_FIELDS_MISSING("Search fields are missing!"),
    PAGE_SIZE_NUMBER_MISSING("Page number and size are missing!"),
    USER_EXISTS("User with username already exists"),
    START_TIME_NOT_VALID("Start time must not be before the current time!"),
    END_TIME_NOT_VALID("End time must not be before start time!"),
    PASSWORD_SECURITY_FAIL("Password doesn't meet security!"),
    PASSWORD_MATCH_FAIL("Passwords do not match!"),
    INVALID_STATUS("Appointment is not in valid status for this operation"),
    INVALID_DATE_FORMAT("Invalid date format! Date should be in format 'year-month-day'"),
    UNAUTHORIZED_OWNER("You are not the owner of the appointment");

    private String message;

}
