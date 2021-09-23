package com.ikub.reservationapp.entity;

import com.ikub.reservationapp.model.BaseEntity;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@Entity
public class Appointment extends BaseEntity {

    private LocalDateTime dateTime;

    private String feedback;

    @NotEmpty
    private String description;

    private String comments;

    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    private Patient patient;

    public static enum Status {
        AVAILABLE, PENDING, ACCEPTED, CANCELED, CHANGED, DONE
    }

//    @PrePersist
//    void createdAt() {
//        this.date = new Date();
//    }
}