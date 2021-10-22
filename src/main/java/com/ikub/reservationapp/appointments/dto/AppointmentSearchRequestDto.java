package com.ikub.reservationapp.appointments.dto;

import com.ikub.reservationapp.common.enums.Status;
import lombok.Data;

@Data
public class AppointmentSearchRequestDto {
    private Integer pageNumber;
    private Integer pageSize;
    private Status status;
    private String date;
    private String doctorName;
    private String patientName;
}
