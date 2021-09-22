package com.ikub.reservationapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ikub.reservationapp.model.BaseEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
public class Appointment extends BaseEntity {

//    @Column(name = "appointment_date")
//    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date date;

    private LocalDateTime dateTime;

    @NotEmpty
    private String description;

    private String comments;

    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    private Patient patient;

    public static enum Status {
        AVAILABLE, PENDING, ACCEPTED, CANCELED, CHANGED
    }

//    @PrePersist
//    void createdAt() {
//        this.date = new Date();
//    }
}