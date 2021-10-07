package com.ikub.reservationapp.appointments.dto.reports;

import com.ikub.reservationapp.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorReportDto {

    private LocalDateTime date;
    private String firstName;
    private String lastName;
    private long numberOfAppointments;
    private Status status;
}
