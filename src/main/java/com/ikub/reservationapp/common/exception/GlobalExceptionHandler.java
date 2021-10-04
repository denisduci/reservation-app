package com.ikub.reservationapp.common.exception;

import com.ikub.reservationapp.appointments.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.common.model.ExceptionMessage;
import com.ikub.reservationapp.doctors.exception.DoctorNotFoundException;
import com.ikub.reservationapp.patients.exception.PatientNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

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

    @ExceptionHandler(value = ReservationAppException.class)
    public ResponseEntity generalException(ReservationAppException reservationAppException) {
        return new ResponseEntity(reservationAppException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = PasswordNotValidException.class)
    public ResponseEntity<ExceptionMessage> getExceptions(PasswordNotValidException passwordNotValidException) {
        List<String> stringParts = Arrays.asList(passwordNotValidException.getMessage().split(","));
        List<String> details = new ArrayList<>();

        stringParts.forEach(item -> {
            details.add(item);
        });
        ExceptionMessage exception = new ExceptionMessage();
        exception.setMessage("Validation Failed");
        exception.setDetails(details);

        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> details = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(objectError -> {
            details.add(objectError.getDefaultMessage());
        });
        ExceptionMessage error = new ExceptionMessage("Validation Failed", details);
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

}