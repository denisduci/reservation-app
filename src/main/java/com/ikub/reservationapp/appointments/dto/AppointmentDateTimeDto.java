package com.ikub.reservationapp.appointments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDateTimeDto {
    private LocalDate appoointmentDate;
    private List<LocalDateTime> availableHours;
}
