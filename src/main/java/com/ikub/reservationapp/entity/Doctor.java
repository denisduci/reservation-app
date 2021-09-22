package com.ikub.reservationapp.entity;

import com.ikub.reservationapp.model.Person;
import lombok.Data;
import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Doctor extends Person {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "doctor")
    private List<Appointment> appointments;

    private String specialty;

}