package com.ikub.reservationapp.appointments.entity;

import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.common.model.BaseEntity;
import com.ikub.reservationapp.users.entity.UserEntity;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private UserEntity doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity patient;

}