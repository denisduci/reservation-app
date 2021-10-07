package com.ikub.reservationapp.appointments.service;

import com.ikub.reservationapp.appointments.dto.AppointmentDateHourDto;
import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.appointments.entity.AppointmentEntity;
import com.ikub.reservationapp.appointments.mapper.AppointmentMapper;
import com.ikub.reservationapp.appointments.utils.DateUtil;
import com.ikub.reservationapp.common.enums.Role;
import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.doctors.service.DoctorService;
import com.ikub.reservationapp.patients.service.PatientService;
import com.ikub.reservationapp.users.mapper.UserMapper;
import com.ikub.reservationapp.users.service.RoleService;
import com.ikub.reservationapp.users.service.UserService;
import org.apache.commons.lang3.StringUtils;
import com.ikub.reservationapp.appointments.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import com.ikub.reservationapp.appointments.repository.AppointmentRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;
    @Autowired
    private AppointmentMapper appointmentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DateUtil dateUtil;

    @Override
    public AppointmentDateHourDto findAvailableHours() {//DONE
        Map<LocalDate, List<LocalDateTime>> allAvailableAppointmentDatesAndHours = new HashMap<>();
        List<LocalDateTime> reservedHours = new ArrayList<>();
        val datesToIterate = dateUtil.datesFromNowToSpecificDay(AppointmentService.DAYS_TO_ITERATE);
        datesToIterate.forEach(nextDate -> {
            List<LocalDateTime> availableHours = dateUtil.createAllAvailableHours(nextDate); //Create All available hours
            List<AppointmentDto> appointmentDtos = findByAppointmentDate(nextDate);
            appointmentDtos.forEach(appointmentDto -> { //Create ALL Reserved Hours
                int startHour = appointmentDto.getStartTime().getHour();
                int endHour = appointmentDto.getEndTime().getHour();
                long numberOfDoctorsAvailable = doctorService.findAvailableDoctors(appointmentDto.getStartTime(),
                                                                                   appointmentDto.getEndTime())
                                                                                   .stream().count();
                if (numberOfDoctorsAvailable == 0) {
                    for (int hour = startHour; hour < endHour; hour++) {
                        reservedHours.add(LocalDateTime.of(appointmentDto.getStartTime().getYear(),
                                appointmentDto.getStartTime().getMonth(),
                                appointmentDto.getStartTime().getDayOfMonth(),
                                hour, 0, 0, 0));
                    }
                }
            });
            reservedHours.forEach(reservedHour -> //Remove Reserved Hours
                    availableHours.removeIf(availableHour -> availableHour.equals(reservedHour)));
            allAvailableAppointmentDatesAndHours.put(nextDate, availableHours);
        });
        AppointmentDateHourDto allAppointments = new AppointmentDateHourDto(allAvailableAppointmentDatesAndHours);
        return allAppointments;
    }

    @Override
    public AppointmentDateHourDto doctorAvailableTime(Long id) {//DONE
        Map<LocalDate, List<LocalDateTime>> allAvailableAppointmentDatesAndHours = new HashMap<>();
        List<LocalDateTime> reservedHours = new ArrayList<>();

        val existingDoctor = userService.findByIdAndRole(id, Role.DOCTOR.name());
        val datesToIterate = dateUtil.datesFromNowToSpecificDay(DAYS_TO_ITERATE);

        datesToIterate.forEach(nextDate -> {
            List<LocalDateTime> availableHours = dateUtil.createAllAvailableHours(nextDate); //Create All available hours
            List<AppointmentDto> appointmentDtos = findByAppointmentDate(nextDate);
            appointmentDtos.forEach(appointmentDto -> { //Create ALL Reserved Hours
                int startHour = appointmentDto.getStartTime().getHour();
                int endHour = appointmentDto.getEndTime().getHour();
                if (appointmentDto.getDoctor().getId().equals(existingDoctor.getId())) {
                    for (int hour = startHour; hour < endHour; hour++) {
                        reservedHours.add(LocalDateTime.of(appointmentDto.getStartTime().getYear(),
                                appointmentDto.getStartTime().getMonth(),
                                appointmentDto.getStartTime().getDayOfMonth(),
                                hour, 0, 0, 0));
                    }
                }
            });
            //Remove Reserved Hours
            reservedHours.forEach(reservedHour ->
                    availableHours.removeIf(availableHour -> availableHour.equals(reservedHour)));
            allAvailableAppointmentDatesAndHours.put(nextDate, availableHours);

        });
        AppointmentDateHourDto allAppointments = new AppointmentDateHourDto(allAvailableAppointmentDatesAndHours);
        return allAppointments;
    }

    @Transactional
    //@Scheduled(cron = "0 0 0 * * *")
    @Override
    public String updateDefaultFeedback() {
        LocalDate previousDate = LocalDate.now().minusDays(1);
        List<AppointmentDto> previousAppointments = findByAppointmentDate(previousDate);
        previousAppointments.forEach(appointmentDto -> {
            if (StringUtils.isEmpty(appointmentDto.getFeedback())) {
                appointmentDto.setFeedback("Default Feedback");
                appointmentRepository.save(appointmentMapper.appointmentDtoToAppointment(appointmentDto));
            }
        });
        return "Updated succesfully";
    }

    @Override
    public AppointmentDto createAppointment(AppointmentDto appointmentDto) {//DONE
      val doctor = userService.findByIdAndRole(appointmentDto.getDoctor().getId(), Role.DOCTOR.name());
        appointmentRepository
                .findByDoctorAvailability(userMapper.userDtoToUser(doctor), appointmentDto.getStartTime(), appointmentDto.getEndTime())
                .ifPresent(appointmentEntity -> {
            throw new ReservationAppException("Doctor is not available in this time!");
        });
        if (appointmentDto.getStartTime().getHour() < START_TIME || appointmentDto.getEndTime().getHour() >= END_TIME) {
            throw new ReservationAppException("Appointment time is out of business hours!");
        }
        if (appointmentDto.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new ReservationAppException("The date selected is not valid. Please reserve a coming date!");
        }
        val patient = userService.findByIdAndRole(appointmentDto.getPatient().getId(), Role.PATIENT.name());
        appointmentDto.setDoctor(doctor);
        appointmentDto.setPatient(patient);
        appointmentDto.setStatus(Status.PENDING);
        return appointmentMapper.appointmentToAppointmentDto(
                appointmentRepository.save(appointmentMapper.appointmentDtoToAppointment(appointmentDto)));
    }

    @Override
    public AppointmentDto cancelAppointment(AppointmentDto appointmentDto) {//DONE
        val appointment = findById(appointmentDto.getId());
        val current = LocalDateTime.now();
        val next = appointment.getStartTime();
        val duration = Duration.between(current, next);
        val seconds = duration.getSeconds();
        val hours = seconds / 3600;
        if (hours >= 24) {
            appointment.setStatus(Status.CANCELED);
            return appointmentMapper.appointmentToAppointmentDto(appointmentRepository.save(appointment));
        }
        throw new ReservationAppException("Too short time to cancel Appointment!");
    }

    @Override
    public List<AppointmentDto> findByStatusAndPatient(Status status, Long patientId) {
        val patient = userService.findByIdAndRole(patientId, Role.PATIENT.name());
        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusAndPatient(status, userMapper.userDtoToUser(patient)))) {
            throw new AppointmentNotFoundException("No appointment found!");
        }
        return appointmentRepository.findByStatusAndPatient(status, userMapper.userDtoToUser(patient))
                .stream().map(appointment -> appointmentMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> findByStatus(Status status) {
        val appointments = appointmentRepository.findByStatus(status)
                .stream().map(appointment -> appointmentMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(appointments)) {
            throw new AppointmentNotFoundException("No Appointments found!");
        }
        return appointments;
    }

    @Override
    public AppointmentDto updateAppointment(AppointmentDto appointmentDto) {
        val appointment = findById(appointmentDto.getId());
        if (appointmentDto.getStatus() == Status.DONE) {
            if (roleService.hasRole(Role.SECRETARY.getRole()) && !StringUtils.isEmpty(appointment.getFeedback())) {
                appointment.setStatus(appointmentDto.getStatus());
                return appointmentMapper.appointmentToAppointmentDto(appointmentRepository.save(appointment));
            }
            throw new ReservationAppException("Cannot update to DONE. No feedback from doctor or role not allowed!");
        }
        appointment.setStatus(appointmentDto.getStatus());
        return appointmentMapper.appointmentToAppointmentDto(appointmentRepository.save(appointment));
    }

    @Override
    public List<AppointmentDto> findByAppointmentDate(LocalDate appointmentDate) {
        return appointmentRepository.findByAppointmentDate(appointmentDate)
                .stream().map(appointmentEntity -> appointmentMapper.appointmentToAppointmentDto(appointmentEntity))
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentDto changeDoctor(AppointmentDto newAppointmentDto) {
        val appointment = findById(newAppointmentDto.getId());
        val doctor = userService.findByIdAndRole(newAppointmentDto.getDoctor().getId(), Role.DOCTOR.name());
        appointmentRepository.findByDoctorAvailability(userMapper.userDtoToUser(doctor),
                appointment.getStartTime(), appointment.getEndTime()).ifPresent(appointmentEntity -> {
            throw new ReservationAppException("Doctor is not available in this time!");
        });
        appointment.setDoctor(userMapper.userDtoToUser(doctor));
        appointment.setStatus(Status.UPDATED);
        return appointmentMapper.appointmentToAppointmentDto(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentDto updateAppointmentFeedback(AppointmentDto appointmentDto) {
        val appointment = findById(appointmentDto.getId());
        appointment.setFeedback(appointmentDto.getFeedback());
        return appointmentMapper.appointmentToAppointmentDto(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentEntity findById(Long id) throws AppointmentNotFoundException {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("No Appointment found with ID " + id));
    }

    @Override
    public List<AppointmentDto> findByPatient(Long patientId) {
        val patient = userService.findByIdAndRole(patientId,Role.PATIENT.name());
        return appointmentRepository.findByPatient(userMapper.userDtoToUser(patient))
                .stream().map(appointment -> appointmentMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> findByDoctor(Long doctorId) {
        val doctorDto = userService.findByIdAndRole(doctorId, Role.DOCTOR.name());
        return appointmentRepository.findByDoctor(userMapper.userDtoToUser(doctorDto))
                .stream().map(appointment -> appointmentMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> findAllAppointments() {
        return appointmentRepository.findAll()
                .stream().map(appointment -> appointmentMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> findByStatusAndDoctor(Status status, Long doctorId) {
        val doctorDto = userService.findByIdAndRole(doctorId, Role.DOCTOR.name());
        val doctor = userMapper.userDtoToUser(doctorDto);
        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusAndDoctor(status, doctor))) {
            throw new AppointmentNotFoundException("No appointment found!");
        }
        return appointmentRepository.findByStatusAndDoctor(status, doctor)
                .stream().map(appointment -> appointmentMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }
}