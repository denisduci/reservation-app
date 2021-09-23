package com.ikub.reservationapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = PatientNotFoundException.class)
    public ResponseEntity patientNotFoundException(PatientNotFoundException patientNotFoundException) {
        return new ResponseEntity(patientNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = AppointmentNotFoundException.class)
    public ResponseEntity appointmentNotFoundException(AppointmentNotFoundException appointmentNotFoundException) {
        return new ResponseEntity(appointmentNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = DoctorNotFoundException.class)
    public ResponseEntity doctorNotFoundException(DoctorNotFoundException doctorNotFoundException) {
        return new ResponseEntity(doctorNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = GeneralException.class)
    public ResponseEntity generalException(GeneralException generalException) {
        return new ResponseEntity(generalException.getMessage(), HttpStatus.BAD_REQUEST);
    }
}