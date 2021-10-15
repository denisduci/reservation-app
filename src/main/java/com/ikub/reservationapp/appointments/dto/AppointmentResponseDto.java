package com.ikub.reservationapp.appointments.dto;

import com.ikub.reservationapp.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDto {

    private Long id;
    private LocalDate appointmentDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String feedback;
    private String description;
    private String comments;
    private Status status;
    private String doctorName;
    private String patientName;
}
