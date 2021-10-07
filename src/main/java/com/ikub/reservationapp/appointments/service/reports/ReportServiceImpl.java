package com.ikub.reservationapp.appointments.service.reports;

import com.ikub.reservationapp.appointments.dto.reports.DoctorReportDto;
import com.ikub.reservationapp.appointments.dto.reports.WeeklyMonthlyReportDto;
import com.ikub.reservationapp.appointments.mapper.ReportMapper;
import com.ikub.reservationapp.appointments.repository.AppointmentRepository;
import com.ikub.reservationapp.common.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ReportMapper reportMapper;

    @Override
    public List<WeeklyMonthlyReportDto> findReports(String type) {
        List<WeeklyMonthlyReportDto> responseList = new ArrayList<>();
        switch (type.toLowerCase()) {
            case "weekly":
                responseList = appointmentRepository.findWeeklyReports().stream().map(
                        reportDBResponseDto -> reportMapper.dbResponseDtoToWeeklyMonthlyReportDto(reportDBResponseDto)
                ).collect(Collectors.toList());
                break;
            case "monthly":
                responseList = appointmentRepository.findMonthlyReports().stream().map(
                        reportDBResponseDto -> reportMapper.dbResponseDtoToWeeklyMonthlyReportDto(reportDBResponseDto)
                ).collect(Collectors.toList());
                break;
        }
        return responseList;
    }

    @Override
    public List<DoctorReportDto> findDoctorsReport() {
        return appointmentRepository.findDoctorsReports().stream().map(
                object -> new DoctorReportDto(
                        ((Date) object[0]).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                        (String) object[1],
                        (String) object[2],
                        (long) ((BigInteger) object[3]).longValue(),
                        Status.values()[(int) object[4]]))
                .collect(Collectors.toList());
    }
}
