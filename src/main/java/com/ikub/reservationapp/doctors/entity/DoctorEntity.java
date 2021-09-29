package com.ikub.reservationapp.doctors.entity;

import com.ikub.reservationapp.common.model.Person;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Entity
@Data
//@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "doctor")
public class DoctorEntity extends Person {

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "doctor")
//    private List<Appointment> appointments;

    private String specialty;

    public DoctorEntity(String firstName){
        super(firstName);
    }

}