package com.ikub.reservationapp.appointments.utils;

import com.ikub.reservationapp.appointments.constants.AppointmentConstants;
import com.ikub.reservationapp.common.enums.Status;
import lombok.extern.slf4j.Slf4j;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public final class DateUtil {

    public static List<LocalDate> datesFromNowToSpecificDay(int limitOfDays) {
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

    public static List<LocalDateTime> createAllAvailableHours(LocalDate nextDate) {
        List<LocalDateTime> availableHours = new ArrayList<>();
        for (int startTime = AppointmentConstants.START_TIME; startTime < AppointmentConstants.END_TIME; startTime++) {
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

//    public static List<Status> getCanceledStatuses() {
//        return Arrays.stream(Status.values()).filter(status ->
//                status == Status.CANCELED_BY_DOCTOR || status == Status.CANCELED_BY_PATIENT || status == Status.CANCELED_BY_SECRETARY)
//                .collect(Collectors.toList());
//    }
}
