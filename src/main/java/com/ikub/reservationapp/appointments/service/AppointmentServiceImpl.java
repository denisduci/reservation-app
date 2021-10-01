package com.ikub.reservationapp.appointments.service;

import com.ikub.reservationapp.appointments.dto.AppointmentDateHourDto;
import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.appointments.entity.AppointmentEntity;
import com.ikub.reservationapp.appointments.mapper.AppointmentMapper;
import com.ikub.reservationapp.appointments.utils.DateUtil;
import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.doctors.dto.DoctorDto;
import com.ikub.reservationapp.doctors.mapper.DoctorMapper;
import com.ikub.reservationapp.doctors.service.DoctorService;
import com.ikub.reservationapp.patients.entity.PatientEntity;
import com.ikub.reservationapp.patients.mapper.PatientMapper;
import com.ikub.reservationapp.patients.service.PatientService;
import org.apache.commons.lang3.StringUtils;
import com.ikub.reservationapp.appointments.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.doctors.exception.DoctorNotFoundException;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import com.ikub.reservationapp.patients.exception.PatientNotFoundException;
import com.ikub.reservationapp.appointments.repository.AppointmentRepository;
import com.ikub.reservationapp.doctors.repository.DoctorRepository;
import com.ikub.reservationapp.patients.repository.PatientRepository;
import lombok.val;
import com.ikub.reservationapp.doctors.entity.DoctorEntity;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.NamedParameterJdbcOperationsDependsOnPostProcessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private DateUtil dateUtil;

    @Override
    public AppointmentDateHourDto findAvailableHours() {
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
    public AppointmentDateHourDto doctorAvailableTime(Long id) {
        Map<LocalDate, List<LocalDateTime>> allAvailableAppointmentDatesAndHours = new HashMap<>();
        List<LocalDateTime> reservedHours = new ArrayList<>();

        val existingDoctor = doctorService.findById(id);
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

    @Override
    public AppointmentDto createAppointment(AppointmentDto appointmentDto) {
        DoctorEntity doctor = doctorMapper.doctorDtoToDoctor(
                doctorService.findById(appointmentDto.getDoctor().getId()));
        appointmentRepository
                .findByDoctorAvailability(doctor, appointmentDto.getStartTime(), appointmentDto.getEndTime()).ifPresent(appointmentEntity -> {
            throw new ReservationAppException("Doctor is not available in this time!");
        });
        if (appointmentDto.getStartTime().getHour() < START_TIME || appointmentDto.getEndTime().getHour() >= END_TIME) {
            throw new ReservationAppException("Appointment time is out of business hours");
        }
        PatientEntity patient = patientService.findById(appointmentDto.getPatient().getId());
        appointmentDto.setDoctor(doctorMapper.doctorToDoctorDto(doctor));
        appointmentDto.setPatient(patientMapper.patientToPatientDto(patient));
        appointmentDto.setStatus(Status.PENDING);
        return appointmentMapper.appointmentToAppointmentDto(
                appointmentRepository.save(appointmentMapper.appointmentDtoToAppointment(appointmentDto)));
    }

    @Override
    public AppointmentDto cancelAppointment(AppointmentDto appointmentDto) {
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
        val patient = patientService.findById(patientId);
        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusAndPatient(status, patient))) {
            throw new AppointmentNotFoundException("No appointment found!");
        }
        return appointmentRepository.findByStatusAndPatient(status, patient)
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
            if (!StringUtils.isEmpty(appointment.getFeedback())) {
                appointment.setStatus(Status.DONE);
                return appointmentMapper.appointmentToAppointmentDto(appointmentRepository.save(appointment));
            }
            throw new ReservationAppException("Cannot update to DONE. No feedback from doctor!");
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
    public AppointmentDto changeDoctor(Long id, AppointmentDto newAppointmentDto) {
        val appointment = findById(id);
        val doctorDto = doctorService.findById(newAppointmentDto.getDoctor().getId());
        appointment.setDoctor(doctorMapper.doctorDtoToDoctor(doctorDto));
        appointment.setStatus(Status.UPDATED);
        return appointmentMapper.appointmentToAppointmentDto(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentDto updateAppointmentFeedback(Long id, AppointmentDto appointmentDto) {
        val appointment = findById(id);
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
        val patient = patientService.findById(patientId);
        return appointmentRepository.findByPatient(patient)
                .stream().map(appointment -> appointmentMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> findByDoctor(Long doctorId) {
        val doctorDto =doctorService.findById(doctorId);
        return appointmentRepository.findByDoctor(doctorMapper.doctorDtoToDoctor(doctorDto))
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
        val doctorDto =doctorService.findById(doctorId);
        val doctor = doctorMapper.doctorDtoToDoctor(doctorDto);
        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusAndDoctor(status, doctor))) {
            throw new AppointmentNotFoundException("No appointment found!");
        }
        return appointmentRepository.findByStatusAndDoctor(status, doctor)
                .stream().map(appointment -> appointmentMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }
}