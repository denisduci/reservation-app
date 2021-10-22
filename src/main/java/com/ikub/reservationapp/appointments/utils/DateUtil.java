package com.ikub.reservationapp.appointments.utils;

import com.ikub.reservationapp.appointments.constants.AppointmentConstants;
import com.ikub.reservationapp.common.exception.BadRequest;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
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
            if (nextDate.getYear() == LocalDate.now().getYear() && nextDate.getMonth() == LocalDate.now().getMonth()
                    && nextDate.getDayOfMonth() == LocalDateTime.now().getDayOfMonth()) {
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

    public static boolean isOverlapping(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.compareTo(end2) < 0 && end1.compareTo(start2) > 0;
    }

    public static LocalDate validateDateFormat(String date) throws ReservationAppException {
        if (date == null || StringUtils.isEmpty(date)) {
            log.error("Date is empty");
            throw new ReservationAppException(BadRequest.INVALID_DATE_FORMAT.getMessage());
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-mm-dd");
        LocalDate localDateFormat;
        try {
            TemporalAccessor temporalAccessor = formatter.ISO_DATE.parse(date);
            localDateFormat = LocalDate.of(
                    temporalAccessor.get(ChronoField.YEAR),
                    temporalAccessor.get(ChronoField.MONTH_OF_YEAR),
                    temporalAccessor.get(ChronoField.DAY_OF_MONTH));
        } catch (DateTimeParseException e) {
            throw new ReservationAppException(BadRequest.INVALID_DATE_FORMAT.getMessage());
        }
        return localDateFormat;
    }
}
