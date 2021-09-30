package com.ikub.reservationapp.appointments.service;

import com.ikub.reservationapp.appointments.dto.AppointmentDateHourDto;
import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.appointments.entity.AppointmentEntity;
import com.ikub.reservationapp.appointments.mapper.AppointmentMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
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

    public static final int START_TIME = 8;
    public static final int END_TIME = 17;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

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

//    @Override
//    public List<AppointmentDto> findAvailableAppointments() {
//        val datetime = LocalDateTime.now();
//        val newDate = datetime.plusDays(7);
//        val appointments = appointmentRepository.findByStatusAndAppointmentDateBetween(
//                Status.AVAILABLE, LocalDateTime.now(), newDate)
//                .stream().map(appointment -> appointmentMapper.appointmentToAppointmentDto(appointment))
//                .collect(Collectors.toList());
//        if (CollectionUtils.isEmpty(appointments)) {
//            throw new AppointmentNotFoundException("No appointments found!");
//        }
//        return appointments;
//    }

    @Override
    public AppointmentDto createAppointment(AppointmentDto appointmentDto) {
        //check if doctor exists and doctor availability
        //check if patient exists
        //check time range
        DoctorEntity doctor = doctorMapper.doctorDtoToDoctor(
                doctorService.findById(appointmentDto.getDoctor().getId()));
        appointmentRepository
                .findByDoctorAvailability(doctor, appointmentDto.getStartTime(), appointmentDto.getEndTime()).ifPresent(appointmentEntity -> {
            throw new ReservationAppException("Doctor is not available in this time!");
        });
        if (appointmentDto.getStartTime().getHour() < START_TIME || appointmentDto.getEndTime().getHour() >= END_TIME) {
            throw new ReservationAppException("Appointment time is out of business hours");
        }
        PatientEntity patient =  patientService.findById(appointmentDto.getPatient().getId());
        appointmentDto.setDoctor(doctorMapper.doctorToDoctorDto(doctor));
        appointmentDto.setPatient(patientMapper.patientToPatientDto(patient));
        appointmentDto.setStatus(Status.PENDING);
        return appointmentMapper.appointmentToAppointmentDto(
                appointmentRepository.save(appointmentMapper.appointmentDtoToAppointment(appointmentDto)));
    }

    @Override
    public AppointmentDateHourDto findAvailableHours() {
        Map<LocalDate, List<LocalDateTime>> allAvailableAppointmentDatesAndHours = new HashMap<>();
        long numOfDaysBetween = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.now().plusDays(7));
        //find all dates to present to user
        List<LocalDate> datesToIterate = IntStream.iterate(0, i -> i + 1)
                .limit(numOfDaysBetween)
                .mapToObj(i -> LocalDate.now().plusDays(i))
                .collect(Collectors.toList());

        datesToIterate.forEach(nextDate -> {
            List<LocalDateTime> availableHours = new ArrayList<>();
            for (int startTime = START_TIME; startTime < END_TIME; startTime++) { //create all available dates of the specific day
                LocalDateTime availableTime = LocalDateTime.of(LocalDateTime.now().getYear(),
                        nextDate.getMonth(),
                        nextDate.getDayOfMonth(),
                        startTime, 0, 0);
                availableHours.add(availableTime);
            }
            List<LocalDateTime> reservedHours = new ArrayList<>();
            List<AppointmentDto> appointmentDtos = findByAppointmentDate(nextDate);
            //Create ALL Reserved Hours
            appointmentDtos.forEach(appointmentDto -> {
                int startHour = appointmentDto.getStartTime().getHour();
                int endHour = appointmentDto.getEndTime().getHour();
                long counter = doctorService.findAvailableDoctors(appointmentDto.getStartTime(), appointmentDto.getEndTime()).stream().count();
                if (counter == 0) {
                    for (int hour = startHour; hour < endHour; hour++) {
                        reservedHours.add(LocalDateTime.of(appointmentDto.getStartTime().getYear(),
                                appointmentDto.getStartTime().getMonth(),
                                appointmentDto.getStartTime().getDayOfMonth(),
                                hour, 0, 0, 0));
                    }
                }
            });
            //Remove Reserved Hours
            reservedHours.forEach(reservedHour -> //for each reserved hour| loop all doctors | findByDoctorAndTime
                    availableHours.removeIf(availableHour -> availableHour.equals(reservedHour)));
            allAvailableAppointmentDatesAndHours.put(nextDate, availableHours);
        });
        AppointmentDateHourDto allAppointments = new AppointmentDateHourDto(allAvailableAppointmentDatesAndHours);
        return allAppointments;
    }

    @Override
    public List<AppointmentDto> findByAppointmentDate(LocalDate appointmentDate) {
        return appointmentRepository.findByAppointmentDate(appointmentDate)
                .stream().map(appointmentEntity -> appointmentMapper.appointmentToAppointmentDto(appointmentEntity))
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentDto reserveAppointment(Long id, AppointmentDto newAppointmentDto) throws AppointmentNotFoundException, PatientNotFoundException, ReservationAppException {
        val appointment = findById(id);
        val patient = patientRepository.findById(newAppointmentDto.getPatient().getId())
                .orElseThrow(() -> new PatientNotFoundException("Patient not found"));
        if (appointment.getStatus() == Status.AVAILABLE) {
            appointment.setStatus(Status.PENDING);
            appointment.setPatient(patient);
            return save(appointmentMapper.appointmentToAppointmentDto(appointment));
        }
        throw new ReservationAppException("This appointment is already reserved!");
    }

    @Override
    public AppointmentDto changeDoctor(Long id, AppointmentDto newAppointmentDto) {
        val appointment = findById(id);
        val doctor = doctorRepository.findById(newAppointmentDto.getDoctor().getId())
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found!"));
        appointment.setDoctor(doctor);
        appointment.setStatus(Status.CHANGED);
        return save(appointmentMapper.appointmentToAppointmentDto(appointment));
    }

    @Override
    public AppointmentDto changeStatus(Long id, Status status) {
        val appointment = findById(id);
        appointment.setStatus(status);
        return save(appointmentMapper.appointmentToAppointmentDto(appointment));
    }

    @Override
    public AppointmentDto updateToDone(Long id) {
        val appointment = findById(id);
        if (!StringUtils.isEmpty(appointment.getFeedback())) {
            appointment.setStatus(Status.DONE);
            return save(appointmentMapper.appointmentToAppointmentDto(appointment));
        }
        throw new ReservationAppException("Cannot update to DONE. No feedback from doctor!");
    }

    @Override
    public AppointmentDto updateAppointmentFeedback(Long id, AppointmentDto appointmentDto) {
        val appointment = findById(id);
        appointment.setFeedback(appointmentDto.getFeedback());
        return save(appointmentMapper.appointmentToAppointmentDto(appointment));
    }

    @Override
    public AppointmentDto cancelAppointment(Long id) throws AppointmentNotFoundException {
        val appointment = findById(id);
        val current = LocalDateTime.now();
        val next = appointment.getAppointmentDate();
        val duration = Duration.between(current, next);
        val seconds = duration.getSeconds();
        val hours = seconds / 3600;
        if (hours >= 24) {
            appointment.setStatus(Status.CANCELED);
            return save(appointmentMapper.appointmentToAppointmentDto(appointment));
        }
        throw new ReservationAppException("Too short time to cancel Appointment!");
    }

    @Override
    public AppointmentEntity findById(Long id) throws AppointmentNotFoundException {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("No Appointment found with ID " + id));
    }

    @Override
    public List<AppointmentDto> findByPatient(Long patientId) {
        val patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(String.format("Patient not found with id %s", patientId)));
        return appointmentRepository.findByPatient(patient)
                .stream().map(appointment -> appointmentMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> findByDoctor(Long doctorId) {
        val doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found!"));
        return appointmentRepository.findByDoctor(doctor)
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
    public List<AppointmentDto> findAllAppointments() {
        return appointmentRepository.findAll()
                .stream().map(appointment -> appointmentMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }
//should be removed
    @Override
    public AppointmentDto save(AppointmentDto appointmentDto) {
        val appointment = appointmentRepository.save(
                appointmentMapper.appointmentDtoToAppointment(appointmentDto));
        return appointmentMapper.appointmentToAppointmentDto(appointment);
    }

    @Override
    public List<AppointmentDto> findByStatusAndPatient(Status status, Long patientId) {
        val patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found!"));
        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusAndPatient(status, patient))) {
            throw new AppointmentNotFoundException("No appointment found!");
        }
        return appointmentRepository.findByStatusAndPatient(status, patient)
                .stream().map(appointment -> appointmentMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> findByStatusAndDoctor(Status status, Long doctorId) {
        val doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found!"));
        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusAndDoctor(status, doctor))) {
            throw new AppointmentNotFoundException("No appointment found!");
        }
        return appointmentRepository.findByStatusAndDoctor(status, doctor)
                .stream().map(appointment -> appointmentMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }
}