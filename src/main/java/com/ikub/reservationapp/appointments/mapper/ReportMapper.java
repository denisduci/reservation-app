package com.ikub.reservationapp.appointments.mapper;

import com.ikub.reservationapp.appointments.dto.reports.ReportDBResponseDto;
import com.ikub.reservationapp.appointments.dto.reports.WeeklyMonthlyReportDto;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class ReportMapper {

    public WeeklyMonthlyReportDto dbResponseDtoToWeeklyMonthlyReportDto(ReportDBResponseDto dbResponseDto) {
        WeeklyMonthlyReportDto weeklyMonthlyReportDto = new WeeklyMonthlyReportDto();
        weeklyMonthlyReportDto.setDate(dbResponseDto.getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());
        weeklyMonthlyReportDto.setNumberOfAppointments(dbResponseDto.getNumberOfAppointments());
        weeklyMonthlyReportDto.setStatus(dbResponseDto.getStatus());
        return weeklyMonthlyReportDto;
    }
}
