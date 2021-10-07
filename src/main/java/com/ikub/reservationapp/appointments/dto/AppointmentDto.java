package com.ikub.reservationapp.appointments.dto;

import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.users.dto.UserDto;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AppointmentDto {
    private Long id;
    private LocalDate appointmentDate;
//    private LocalDate lastModified;
//    private LocalDate createdOn;
//    private String createdBy;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String feedback;
    private String description;
    private String comments;
    private Status status;
    private UserDto doctor;
    private UserDto patient;
}
