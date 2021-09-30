package com.ikub.reservationapp.appointments.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDateHourDto {
    private Map<LocalDate, List<LocalDateTime>> appointments;
}
