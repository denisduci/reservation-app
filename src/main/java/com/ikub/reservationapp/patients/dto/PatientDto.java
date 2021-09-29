package com.ikub.reservationapp.patients.dto;

import lombok.Data;
import java.util.List;

@Data
public class PatientDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String telephone;
    //private List<AppointmentDto> appointments;
}
