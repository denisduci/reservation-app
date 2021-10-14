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
    DOCTOR_MISSING("Doctor missing!"),
    DOCTOR_NOT_AVAILABLE("Doctor is not available in this time!"),
    PATIENT_MISSING("Patient missing!"),
    FEEDBACK_MISSING("Cannot update to DONE. No feedback from doctor or role not allowed!"),
    APPOINTMENT_OUT_OF_HOURS("Appointment time is out of business hours!"),
    APPOINTMENT_DATE_NOT_VALID("The date selected is not valid. Please reserve a coming date!"),
    SHORT_TIME_TO_CANCEL("Too short time to cancel Appointment!"),
    USER_EXISTS("User with username already exists"),
    START_TIME_NOT_VALID("Start time must be a valid time!"),
    PASSWORD_SECURITY_FAIL("Password doesn't meet security!"),
    PASSWORD_MATCH_FAIL("Passwords do not match!"),
    UNAUTHORIZED_OWNER("You are not the owner of the appointment");

    private String message;

}
