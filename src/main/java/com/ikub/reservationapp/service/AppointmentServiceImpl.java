package com.ikub.reservationapp.service;

import com.ikub.reservationapp.dto.AppointmentDto;
import com.ikub.reservationapp.entity.Appointment;
import org.apache.commons.lang3.StringUtils;
import com.ikub.reservationapp.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.exception.DoctorNotFoundException;
import com.ikub.reservationapp.exception.GeneralException;
import com.ikub.reservationapp.exception.PatientNotFoundException;
import com.ikub.reservationapp.mapper.MapStructMapper;
import com.ikub.reservationapp.repository.AppointmentRepository;
import com.ikub.reservationapp.repository.DoctorRepository;
import com.ikub.reservationapp.repository.PatientRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private MapStructMapper mapStructMapper;

    @Override
    public List<AppointmentDto> findAvailableAppointments() {
        val datetime = LocalDateTime.now();
        val newDate = datetime.plusDays(7);
        val appointments = appointmentRepository.findByStatusAndDateTimeBetween(
                Appointment.Status.AVAILABLE, LocalDateTime.now(), newDate)
                .stream().map(appointment -> mapStructMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(appointments)) {
            throw new AppointmentNotFoundException("No appointments found!");
        }
        return appointments;
    }

    @Override
    public AppointmentDto reserveAppointment(Long id, AppointmentDto newAppointmentDto) throws AppointmentNotFoundException, PatientNotFoundException, GeneralException {
        val appointment = findById(id);
        val patient = patientRepository.findById(newAppointmentDto.getPatient().getId())
                .orElseThrow(() -> new PatientNotFoundException("Patient not found"));
        if (appointment.getStatus() == Appointment.Status.AVAILABLE) {
            appointment.setStatus(Appointment.Status.PENDING);
            appointment.setPatient(patient);
            return save(mapStructMapper.appointmentToAppointmentDto(appointment));
        }
        throw new GeneralException("This appointment is already reserved!");
    }

    @Override
    public AppointmentDto changeDoctor(Long id, AppointmentDto newAppointmentDto) {
        val appointment = findById(id);
        val doctor = doctorRepository.findById(newAppointmentDto.getDoctor().getId())
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found!"));
        appointment.setDoctor(doctor);
        appointment.setStatus(Appointment.Status.CHANGED);
        return save(mapStructMapper.appointmentToAppointmentDto(appointment));
    }

    @Override
    public AppointmentDto changeStatus(Long id, Appointment.Status status) {
        val appointment = findById(id);
        appointment.setStatus(status);
        return save(mapStructMapper.appointmentToAppointmentDto(appointment));
    }

    @Override
    public AppointmentDto updateToDone(Long id) {
        val appointment = findById(id);
        if (!StringUtils.isEmpty(appointment.getFeedback())) {
            appointment.setStatus(Appointment.Status.DONE);
            return save(mapStructMapper.appointmentToAppointmentDto(appointment));
        }
        throw new GeneralException("Cannot update to DONE. No feedback from doctor!");
    }

    @Override
    public AppointmentDto updateAppointmentFeedback(Long id, AppointmentDto appointmentDto) {
        val appointment = findById(id);
        appointment.setFeedback(appointmentDto.getFeedback());
        return save(mapStructMapper.appointmentToAppointmentDto(appointment));
    }

    @Override
    public AppointmentDto cancelAppointment(Long id) throws AppointmentNotFoundException {
        val appointment = findById(id);
        val current = LocalDateTime.now();
        val next = appointment.getDateTime();
        val duration = Duration.between(current, next);
        val seconds = duration.getSeconds();
        val hours = seconds / 3600;
        if (hours >= 24) {
            appointment.setStatus(Appointment.Status.CANCELED);
            return save(mapStructMapper.appointmentToAppointmentDto(appointment));
        }
        throw new GeneralException("Too short time to cancel Appointment!");
    }

    @Override
    public Appointment findById(Long id) throws AppointmentNotFoundException {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("No Appointment found with ID " + id));
    }

    @Override
    public List<AppointmentDto> findByPatient(Long patientId) {
        val patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found!"));
        return appointmentRepository.findByPatient(patient)
                .stream().map(appointment -> mapStructMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> findByDoctor(Long doctorId) {
        val doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found!"));
        return appointmentRepository.findByDoctor(doctor)
                .stream().map(appointment -> mapStructMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> findByStatus(Appointment.Status status) {
        val appointments = appointmentRepository.findByStatus(status)
                .stream().map(appointment -> mapStructMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(appointments)) {
            throw new AppointmentNotFoundException("No Appointments found!");
        }
        return appointments;
    }

    @Override
    public List<AppointmentDto> findAllAppointments() {
        return appointmentRepository.findAll()
                .stream().map(appointment -> mapStructMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentDto save(AppointmentDto appointmentDto) {
        val appointment = appointmentRepository.save(
                mapStructMapper.appointmentDtoToAppointment(appointmentDto));
        return mapStructMapper.appointmentToAppointmentDto(appointment);
    }

    @Override
    public List<AppointmentDto> findByStatusAndPatient(Appointment.Status status, Long patientId) {
        val patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found!"));
        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusAndPatient(status, patient))) {
            throw new AppointmentNotFoundException("No appointment found!");
        }
        return appointmentRepository.findByStatusAndPatient(status, patient)
                .stream().map(appointment -> mapStructMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> findByStatusAndDoctor(Appointment.Status status, Long doctorId) {
        val doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found!"));
        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusAndDoctor(status, doctor))) {
            throw new AppointmentNotFoundException("No appointment found!");
        }
        return appointmentRepository.findByStatusAndDoctor(status, doctor)
                .stream().map(appointment -> mapStructMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }
}