package com.ikub.reservationapp.appointments.service.reports;

import com.ikub.reservationapp.appointments.dto.reports.DoctorReportDto;
import com.ikub.reservationapp.appointments.dto.reports.WeeklyMonthlyReportDto;
import java.util.List;

public interface ReportService {

    List<WeeklyMonthlyReportDto> findReports(String type);
    List<DoctorReportDto> findDoctorsReport();
}
