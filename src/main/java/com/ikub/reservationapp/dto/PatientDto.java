package com.ikub.reservationapp.dto;

import com.ikub.reservationapp.entity.Appointment;
import lombok.Data;
import java.util.List;

@Data
public class PatientDto {
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String telephone;
    private List<AppointmentDto> appointments;
}
