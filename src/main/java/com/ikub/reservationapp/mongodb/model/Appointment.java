package com.ikub.reservationapp.mongodb.model;

import com.ikub.reservationapp.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias("Appointment")
public class Appointment {
    @Id
    private String id;
    private String description;
    private Status status;
    private String appointmentDate;
    private String startTime;
    private String endTime;
    private String feedback;
    private String doctor;
    private String patient;
}