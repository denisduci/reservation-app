package com.ikub.reservationapp.appointments.utils;

import com.ikub.reservationapp.appointments.service.AppointmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
public class DateUtil {

    public List<LocalDate> datesFromNowToSpecificDay(int limitOfDays) {
        log.info("Inside datesFromNowToSpecificDay... ");
        List<LocalDate> dates = IntStream.iterate(0, i -> i + 1)
                .limit(limitOfDays + 2)
                .mapToObj(i -> LocalDate.now().plusDays(i))
                .collect(Collectors.toList());

        return dates.stream().filter(localDate ->
                DayOfWeek.of(localDate.get(ChronoField.DAY_OF_WEEK)) != DayOfWeek.SATURDAY &&
                        DayOfWeek.of(localDate.get(ChronoField.DAY_OF_WEEK)) != DayOfWeek.SUNDAY)
                .collect(Collectors.toList());
    }

    public List<LocalDateTime> createAllAvailableHours(LocalDate nextDate) {
        List<LocalDateTime> availableHours = new ArrayList<>();
        for (int startTime = AppointmentService.START_TIME; startTime < AppointmentService.END_TIME; startTime++) {
            if (nextDate.getMonth() == LocalDate.now().getMonth() && nextDate.getDayOfMonth() == LocalDateTime.now().getDayOfMonth()
                    && nextDate.getYear() == LocalDate.now().getYear()) {
                if (startTime > LocalDateTime.now().getHour()) {
                    LocalDateTime availableTime = LocalDateTime.of(LocalDateTime.now().getYear(),
                            nextDate.getMonth(), nextDate.getDayOfMonth(),
                            startTime, 0, 0);
                    availableHours.add(availableTime);
                }
            } else {
                LocalDateTime availableTime = LocalDateTime.of(LocalDateTime.now().getYear(),
                        nextDate.getMonth(), nextDate.getDayOfMonth(),
                        startTime, 0, 0);
                availableHours.add(availableTime);
            }
        }
        log.info("All available hours for date: -> {} are: -> {}", nextDate, availableHours);
        return availableHours;
    }
}
