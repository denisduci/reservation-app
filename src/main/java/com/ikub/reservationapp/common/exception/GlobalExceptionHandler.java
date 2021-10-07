package com.ikub.reservationapp.common.exception;

import com.ikub.reservationapp.appointments.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.common.model.ExceptionMessage;
import com.ikub.reservationapp.users.exception.UserNotFoundException;
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

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity userNotFoundException(UserNotFoundException userNotFoundException) {
        return new ResponseEntity(userNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = AppointmentNotFoundException.class)
    public ResponseEntity appointmentNotFoundException(AppointmentNotFoundException appointmentNotFoundException) {
        return new ResponseEntity(appointmentNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ReservationAppException.class)
    public ResponseEntity generalException(ReservationAppException reservationAppException) {
        return new ResponseEntity(reservationAppException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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