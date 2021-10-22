package com.ikub.reservationapp.appointments.dto;

import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.users.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {

    private Long id;

    @FutureOrPresent(message = "The date selected is not valid. Please reserve a coming date!")
    @NotNull(message = "Appointment date must not be empty")
    private LocalDate appointmentDate;

    @NotNull(message = "Appointment start time must not be empty")
    private LocalDateTime startTime;

    @NotNull(message = "Appointment end time must not be empty")
    private LocalDateTime endTime;

    private String feedback;

    @NotBlank(message = "Description must not be empty")
    private String description;
    private String comments;
    private Status status;

    @NotNull(message = "Doctor must not be empty")
    private UserDto doctor;

    @NotNull(message = "Patient must not be empty")
    private UserDto patient;
}
