package com.ikub.reservationapp.doctors.dto;

import lombok.Data;

@Data
public class DoctorDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String specialty;

}
