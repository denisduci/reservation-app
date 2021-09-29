package com.ikub.reservationapp.patients.entity;

import com.ikub.reservationapp.common.model.Person;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;

@Data
@Entity
@Table(name = "patient")
public class PatientEntity extends Person {

    @NotEmpty
    private String address;

    @NotEmpty
    private String city;

    @NotEmpty
    @Digits(fraction = 0, integer = 10)
    private String telephone;

}