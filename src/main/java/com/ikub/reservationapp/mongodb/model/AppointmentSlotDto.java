package com.ikub.reservationapp.mongodb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentSlotDto {
    private LocalDate appointmentDate;
    private List<LocalTime> availableHours;
}

