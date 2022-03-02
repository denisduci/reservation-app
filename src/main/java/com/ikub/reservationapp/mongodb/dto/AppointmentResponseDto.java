package com.ikub.reservationapp.mongodb.dto;

import com.ikub.reservationapp.common.enums.Status;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AppointmentResponseDto {

    @Id
    private String id;

    @FutureOrPresent(message = "The date selected is not valid. Please reserve a coming date!")
    @NotNull(message = "Appointment date must not be empty")
    private String appointmentDate;

    @NotBlank(message = "Description must not be empty")
    private String description;

    private Status status;

    @NotNull(message = "Appointment start time must not be empty")
    private String startTime;

    @NotNull(message = "Appointment end time must not be empty")
    private String endTime;

    private String feedback;

    @NotNull(message = "Doctor must not be empty")
    private UserMongoResponseDto doctor;

    @NotNull(message = "Patient must not be empty")
    private UserMongoResponseDto patient;
}
