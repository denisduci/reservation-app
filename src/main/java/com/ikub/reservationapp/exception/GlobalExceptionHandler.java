package com.ikub.reservationapp.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Value(value = "${exception.patient}")
    private String message1;

    @Value(value = "${data.exception.appointmentnotfound}")
    private String message2;

    @Value(value = "${data.exception.doctornotfound}")
    private String message3;

    @Value(value = "${data.exception.general}")
    private String message4;

    @ExceptionHandler(value = PatientNotFoundException.class)
    public ResponseEntity patientNotFoundException(PatientNotFoundException patientNotFoundException) {
        return new ResponseEntity(message1, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = AppointmentNotFoundException.class)
    public ResponseEntity appointmentNotFoundException(AppointmentNotFoundException appointmentNotFoundException) {
        return new ResponseEntity(message2, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = DoctorNotFoundException.class)
    public ResponseEntity doctorNotFoundException(DoctorNotFoundException doctorNotFoundException) {
        return new ResponseEntity(message3, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = GeneralException.class)
    public ResponseEntity generalException(GeneralException generalException) {
        return new ResponseEntity(message4, HttpStatus.BAD_REQUEST);
    }
}