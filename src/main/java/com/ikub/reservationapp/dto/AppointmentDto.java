package com.ikub.reservationapp.dto;

import com.ikub.reservationapp.entity.Appointment;;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AppointmentDto {

    private Long id;
//    private Date date;
    private LocalDateTime dateTime;
    private String description;
    private String comments;
    private Appointment.Status status;
    private DoctorDto doctor;
    private PatientDto patient;
}
