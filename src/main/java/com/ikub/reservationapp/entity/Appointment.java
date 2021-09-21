package com.ikub.reservationapp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ikub.reservationapp.model.BaseEntity;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
@Entity
@ToString
public class Appointment extends BaseEntity {

    @Column(name = "appointment_date")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @NotEmpty
    private String description;

    private String comments;

    private Status status;

    @JsonBackReference(value = "doctor")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @JsonBackReference(value = "patient")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    public static enum Status {
        AVAILABLE, PENDING, ACCEPTED, CANCELED, CHANGED
    }

//    @PrePersist
//    void createdAt() {
//        this.date = new Date();
//    }
}