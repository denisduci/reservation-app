package com.ikub.reservationapp.appointments.utils;

import com.ikub.reservationapp.appointments.service.AppointmentService;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class DateUtil {

    public List<LocalDate> datesFromNowToSpecificDay(int limitOfDays) {
        long numOfDaysBetween = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.now().plusDays(limitOfDays));
        return IntStream.iterate(0, i -> i + 1)
                .limit(numOfDaysBetween)
                .mapToObj(i -> LocalDate.now().plusDays(i))
                .collect(Collectors.toList());
    }

    public List<LocalDateTime> createAllAvailableHours(LocalDate nextDate) {
        List<LocalDateTime> availableHours = new ArrayList<>();
        for (int startTime = AppointmentService.START_TIME; startTime < AppointmentService.END_TIME; startTime++) { //create all available dates of the specific day
            LocalDateTime availableTime = LocalDateTime.of(LocalDateTime.now().getYear(),
                    nextDate.getMonth(),
                    nextDate.getDayOfMonth(),
                    startTime, 0, 0);
            availableHours.add(availableTime);

        }
        return availableHours;
    }
}
