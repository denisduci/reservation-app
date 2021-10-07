package com.ikub.reservationapp.appointments.dto.reports;

import com.ikub.reservationapp.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDBResponseDto {

    private Date date;
    private long numberOfAppointments;
    private Status status;
}
