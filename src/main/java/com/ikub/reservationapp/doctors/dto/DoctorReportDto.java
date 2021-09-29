package com.ikub.reservationapp.doctors.dto;

import lombok.Data;

@Data
public class DoctorReportDto {
    private String firstName;
    private long appointmentNumber;

    public DoctorReportDto(String firstName, long counter) {
        this.firstName = firstName;
        this.appointmentNumber = counter;
    }
}
