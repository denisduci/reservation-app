package com.ikub.reservationapp.mongodb.dto;

import com.ikub.reservationapp.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {

    @Id
    private String id;

    @FutureOrPresent(message = "The date selected is not valid. Please reserve a coming date!")
    @NotNull(message = "Appointment date must not be empty")
    private LocalDate appointmentDate;

    @NotBlank(message = "Description must not be empty")
    private String description;

    private Status status;

    @NotNull(message = "Appointment start time must not be empty")
    private LocalTime startTime;

    @NotNull(message = "Appointment end time must not be empty")
    private LocalTime endTime;

    private String feedback;

    @NotNull(message = "Doctor must not be empty")
    private String doctor;

    @NotNull(message = "Patient must not be empty")
    private String patient;
}