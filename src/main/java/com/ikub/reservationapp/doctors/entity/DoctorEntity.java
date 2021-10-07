//package com.ikub.reservationapp.doctors.entity;
//
//import com.ikub.reservationapp.common.model.Person;
//import com.ikub.reservationapp.users.entity.UserEntity;
//import lombok.Data;
//import lombok.RequiredArgsConstructor;
//
//import javax.persistence.*;
//
////@Entity
//@Data
////@NoArgsConstructor
//@RequiredArgsConstructor
//@Table(name = "doctor")
//public class DoctorEntity extends Person {
//
////    @OneToMany(cascade = CascadeType.ALL, mappedBy = "doctor")
////    private List<Appointment> appointments;
//
//    private String specialty;
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="user_id")
//    private UserEntity user;
//
//    public DoctorEntity(String firstName){
//        super(firstName);
//    }
//
//}