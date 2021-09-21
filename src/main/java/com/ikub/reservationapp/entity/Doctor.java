package com.ikub.reservationapp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ikub.reservationapp.model.Person;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@ToString
public class Doctor extends Person {

    @JsonManagedReference(value="doctor")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "doctor")
    private List<Appointment> appointments;

    private String specialty;

}