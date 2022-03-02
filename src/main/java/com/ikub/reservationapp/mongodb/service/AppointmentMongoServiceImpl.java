package com.ikub.reservationapp.mongodb.service;

import com.ikub.reservationapp.appointments.constants.AppointmentConstants;
import com.ikub.reservationapp.appointments.dto.AppointmentDateTimeDto;
import com.ikub.reservationapp.appointments.entity.AppointmentEntity;
import com.ikub.reservationapp.appointments.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.appointments.utils.DateUtil;
import com.ikub.reservationapp.common.enums.Role;
import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.common.exception.BadRequest;
import com.ikub.reservationapp.common.exception.NotFound;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import com.ikub.reservationapp.mongodb.dto.AppointmentDto;
import com.ikub.reservationapp.mongodb.dto.AppointmentResponseDto;
import com.ikub.reservationapp.mongodb.mappers.AppointmentMongoMapper;
import com.ikub.reservationapp.mongodb.model.Appointment;
import com.ikub.reservationapp.mongodb.model.AppointmentSlotDto;
import com.ikub.reservationapp.mongodb.model.UserMongo;
import com.ikub.reservationapp.mongodb.repository.AppointmentMongoRepository;
import com.ikub.reservationapp.mongodb.repository.RoleMongoRepository;
import com.ikub.reservationapp.mongodb.repository.UserMongoRepository;
import com.ikub.reservationapp.mongodb.utils.DateTransformerUtil;
import com.ikub.reservationapp.users.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AppointmentMongoServiceImpl implements AppointmentMongoService {

    @Autowired
    private UserMongoRepository userMongoRepository;

    @Autowired
    private AppointmentMongoMapper appointmentMongoMapper;

    @Autowired
    private AppointmentMongoRepository appointmentMongoRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RoleMongoRepository roleRepository;

    @Override
    public AppointmentResponseDto createAppointment(AppointmentDto appointmentRequestDto) {
        List<Status> eligibleStatuses = Arrays.stream(Status.values()).filter(status -> status.equals(Status.PENDING) ||
                        status.equals(Status.APPROVED) || status.equals(Status.DOCTOR_CHANGE_REQUEST) || status.equals(Status.DOCTOR_CHANGE_APPROVED))
                .collect(Collectors.toList());
        //all appointments in the requested day that are eligible.
        val userAppointmentsInSpecificDay = appointmentMongoRepository.findByAppointmentDateAndPatientAndStatusIn(
                DateTransformerUtil.dateToString(appointmentRequestDto.getAppointmentDate()), appointmentRequestDto.getPatient(), eligibleStatuses);

        //filter appointments that overlap
        val counterOfOverlappingHours = userAppointmentsInSpecificDay.stream().filter(appointment ->
                        DateTransformerUtil.isOverlapping(DateTransformerUtil.stringToTime(appointment.getStartTime()),
                                DateTransformerUtil.stringToTime(appointment.getEndTime()),
                                appointmentRequestDto.getStartTime(),
                                appointmentRequestDto.getEndTime()))
                .collect(Collectors.toList())
                .stream().count();
        if (counterOfOverlappingHours > 0) {
            log.error("Already reserved appointment");
            throw new ReservationAppException(BadRequest.APPOINTMENT_ALREADY_EXISTS.getMessage());
        }
        Appointment appointment = appointmentMongoMapper.toAppointmentModel(appointmentRequestDto);
        appointment.setStatus(Status.PENDING);
        return appointmentMongoMapper.toResponseDto(appointmentMongoRepository.save(appointment));
    }

    @Override
    public AppointmentDto getAppointmentById(String id) {
        return appointmentMongoMapper.toAppointmentDto(appointmentMongoRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage())));
    }

    @Override
    public List<AppointmentResponseDto> getPatientAppointments(String id) {
        val response = appointmentMongoRepository.findByPatient(id);
        return response.stream().map(appointmentMongoMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentResponseDto updateAppointment(String id, AppointmentDto appointmentDto) {
        val appointment = appointmentMongoRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage()));
        appointmentMongoMapper.updateAppointmentFromDto(appointmentDto, appointment);

        String tempAppointmentDate = "2021-11-13";
        String patientId = "618d29743f062107eac3adf3";

        return appointmentMongoMapper.toResponseDto(appointmentMongoRepository.save(appointment));

    }

    @Override
    public List<AppointmentSlotDto> getAvailableHours() {
        //String loggedInUsername = userService.getUsernameFromContext();
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Authenticated username is: -> {}", loggedInUsername);
        List<AppointmentSlotDto> allAvailableAppointmentDatesAndHoursResponse = new ArrayList<>();
        val datesToIterate = DateUtil.datesFromNowToSpecificDay(AppointmentConstants.DAYS_TO_ITERATE);
        log.info("Dates to iterate: -> {}", datesToIterate);

        List<Status> eligibleStatuses = Arrays.stream(Status.values()).filter(status -> status.equals(Status.CANCELED_BY_SECRETARY) ||
                        status.equals(Status.CANCELED_BY_DOCTOR) || status.equals(Status.CANCELED_BY_PATIENT))
                .collect(Collectors.toList());

        datesToIterate.forEach(nextDate -> {
            List<LocalTime> reservedHours = new ArrayList<>();
            List<LocalTime> availableHours = createAllAvailableHours(nextDate);//1-Create All available hours for nextDate
            List<Appointment> reservedAppointments = appointmentMongoRepository.findByAppointmentDateAndStatusNotIn(nextDate.toString(), eligibleStatuses);
            log.info("Reserved appointments in date: -> {} are: -> {}", nextDate, reservedAppointments);

            reservedAppointments.forEach(reservedAppointment -> { //2-Create ALL Reserved Hours
                int startHour = DateTransformerUtil.stringToTime(reservedAppointment.getStartTime()).getHour();
                int endHour = DateTransformerUtil.stringToTime(reservedAppointment.getEndTime()).getHour();
                if (!availableDoctors(reservedAppointment.getStartTime(), reservedAppointment.getEndTime(), reservedAppointment.getAppointmentDate()))
                    addToReservations(startHour, endHour, reservedHours);

            });
            log.info("Reserved hours: -> {}", reservedHours);
            removeReservedHoursFromAllAvailableHours(reservedHours, availableHours);
            log.info("Available hours for date -> {} are: -> {}", nextDate, availableHours);
            AppointmentSlotDto availableAppointmentDto = new AppointmentSlotDto(nextDate, availableHours);
            allAvailableAppointmentDatesAndHoursResponse.add(availableAppointmentDto);
        });
        return allAvailableAppointmentDatesAndHoursResponse;
    }

    public static List<LocalTime> createAllAvailableHours(LocalDate nextDate) {
        List<LocalTime> availableHours = new ArrayList<>();
        for (int startTime = AppointmentConstants.START_TIME; startTime < AppointmentConstants.END_TIME; startTime++) {
            if (nextDate.getYear() == LocalDate.now().getYear() && nextDate.getMonth() == LocalDate.now().getMonth()
                    && nextDate.getDayOfMonth() == LocalDateTime.now().getDayOfMonth()) {
                if (startTime > LocalDateTime.now().getHour()) {
                    LocalTime availableTime = LocalTime.of(
                            startTime, 0);
                    availableHours.add(availableTime);
                }
            } else {
                LocalTime availableTime = LocalTime.of(startTime, 0);
                availableHours.add(availableTime);
            }
        }
        log.info("All available hours for date: -> {} are: -> {}", nextDate, availableHours);
        return availableHours;
    }

    public boolean availableDoctors(String startTime, String endTime, String date) {
        List<String> reserveDoctors = new ArrayList<>();
        List<String> roleDoctorIds = new ArrayList<>();
        roleDoctorIds.add(roleRepository.findByName(Role.DOCTOR.getRole()).getId());

        Query query = new Query(
                Criteria.where("appointmentDate").is(date)
                        .andOperator(
                                Criteria.where("startTime").gte(startTime),
                                Criteria.where("endTime").lte(endTime)
                        )
        );

        val mongoTemplateResponse = mongoTemplate.find(query, Appointment.class);
        mongoTemplateResponse.stream().forEach(appointment -> reserveDoctors.add(appointment.getDoctor()));
        val availableDoctorsAsList = userMongoRepository.findByIdNotInAndRolesIn(reserveDoctors, roleDoctorIds);
        return availableDoctorsAsList.size() > 0;
    }

    public void addToReservations(int startHour, int endHour, List<LocalTime> reservedHours) {
        log.info("Inside addToReservations...");
        for (int hour = startHour; hour < endHour; hour++) {
            reservedHours.add(LocalTime.of(
                    hour, 0));
        }
    }

    public void removeReservedHoursFromAllAvailableHours(List<LocalTime> reservedHours, List<LocalTime> availableHours) {
        reservedHours.forEach(reservedHour ->
                availableHours.removeIf(availableHour -> availableHour.equals(reservedHour)));
    }

    @Override
    public Object cancelAppointment(String id) {
        val existingAppointment = appointmentMongoRepository.findById(id)
                .orElseThrow(()-> new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage()));
        existingAppointment.setStatus(Status.CANCELED_BY_SECRETARY);
        return  appointmentMongoRepository.save(existingAppointment);
    }
}
