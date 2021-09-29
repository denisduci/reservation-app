package com.ikub.reservationapp.appointments.entity;

import com.ikub.reservationapp.doctors.entity.DoctorEntity;
import com.ikub.reservationapp.patients.entity.PatientEntity;
import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.common.model.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Entity
@Table(name = "appointment")
public class AppointmentEntity extends BaseEntity {

    private LocalDate appointmentDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String feedback;
    private String description;
    private String comments;
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    private DoctorEntity doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    private PatientEntity patient;

}