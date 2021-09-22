package com.ikub.reservationapp.entity;

import com.ikub.reservationapp.model.Person;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Entity
public class Patient extends Person {

    @NotEmpty
    private String address;

    @NotEmpty
    private String city;

    @NotEmpty
    @Digits(fraction = 0, integer = 10)
    private String telephone;

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "patient")
//    private List<Appointment> appointments;
}