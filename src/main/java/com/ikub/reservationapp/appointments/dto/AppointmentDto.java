package com.ikub.reservationapp.appointments.dto;

import com.ikub.reservationapp.doctors.dto.DoctorDto;
import com.ikub.reservationapp.patients.dto.PatientDto;
import com.ikub.reservationapp.common.enums.Status;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AppointmentDto {
    private Long id;
    private LocalDate appointmentDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String feedback;
    private String description;
    private String comments;
    private Status status;
    private DoctorDto doctor;
    private PatientDto patient;
}
