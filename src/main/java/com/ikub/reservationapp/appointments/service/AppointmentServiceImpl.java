package com.ikub.reservationapp.appointments.service;

import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.appointments.entity.AppointmentEntity;
import com.ikub.reservationapp.appointments.mapper.AppointmentMapper;
import com.ikub.reservationapp.common.enums.Status;
import org.apache.commons.lang3.StringUtils;
import com.ikub.reservationapp.appointments.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.doctors.exception.DoctorNotFoundException;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import com.ikub.reservationapp.patients.exception.PatientNotFoundException;
import com.ikub.reservationapp.appointments.repository.AppointmentRepository;
import com.ikub.reservationapp.doctors.repository.DoctorRepository;
import com.ikub.reservationapp.patients.repository.PatientRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
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
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentMapper appointmentMapper;

    public static final int START_TIME=8;
    public static final int END_TIME=17;

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
    public Map<Object, List<Integer>> findAvailableHours() {
        val appointments = appointmentRepository.findAll();
        Map<Object, List<Integer>> objectListMap = new HashMap<>();
        LocalDate firstDate = LocalDate.now();
        LocalDate nextDate = firstDate.plusDays(7);
        long numOfDaysBetween = ChronoUnit.DAYS.between(firstDate, nextDate);

        List<LocalDate> dates = new ArrayList<>();
        dates = IntStream.iterate(0, i -> i + 1)
                .limit(numOfDaysBetween)
                .mapToObj(i -> firstDate.plusDays(i))
                .collect(Collectors.toList());

        dates.forEach(localDate -> {
           // List<Integer> reservedHours = new ArrayList<>();

            List<LocalDateTime> reservedTimes = new ArrayList<>();
            List<Integer> availableHours = new ArrayList<>();
            List<AppointmentDto> appointmentDtos = findByAppointmentDate(localDate);
            if (!CollectionUtils.isEmpty(appointmentDtos)) {
                appointmentDtos.forEach(appointmentDto -> {
                    int startHour = appointmentDto.getStartTime().getHour();
                    int endHour = appointmentDto.getEndTime().getHour();
                    for (int i = startHour; i < endHour; i++) {
                        //reservedHours.add(i);
                        reservedTimes.add(LocalDateTime.of(appointmentDto.getStartTime().getYear(),
                                appointmentDto.getStartTime().getMonth(),
                                appointmentDto.getStartTime().getDayOfMonth(),
                                i, 0, 0, 0));
                    }
                });
            }
            for (int i = START_TIME; i <= END_TIME; i++) {
//                if (!reservedHours.contains(i)) {
//                    availableHours.add(i);
//                }
                int finalI = i;
                reservedTimes.forEach(reservedTime -> {
                    if (reservedTime.getHour() != finalI) {
                        availableHours.add(finalI);
                    }
                });
            }
            objectListMap.put(localDate, availableHours);
        });
        return objectListMap;
    }

    @Override
    public List<AppointmentDto> findByAppointmentDate(LocalDate appointmentDate) {
        Map<Object, List<Object>> objectListMap = new HashMap<>();
        return appointmentRepository.findByAppointmentDate(appointmentDate)
                .stream().map(appointmentEntity -> appointmentMapper.appointmentToAppointmentDto(appointmentEntity))
                .collect(Collectors.toList());
    }
//get each day
    //find if there are reserved
        //if yes extract reserved times
            //map with Object and list of integers
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