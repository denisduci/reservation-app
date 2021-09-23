package com.ikub.reservationapp.service;

import com.ikub.reservationapp.dto.AppointmentDto;
import com.ikub.reservationapp.entity.Appointment;
import com.ikub.reservationapp.entity.Doctor;
import com.ikub.reservationapp.entity.Patient;
import com.ikub.reservationapp.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.exception.DoctorNotFoundException;
import com.ikub.reservationapp.exception.GeneralException;
import com.ikub.reservationapp.exception.PatientNotFoundException;
import com.ikub.reservationapp.mapper.MapStructMapper;
import com.ikub.reservationapp.repository.AppointmentRepository;
import com.ikub.reservationapp.repository.DoctorRepository;
import com.ikub.reservationapp.repository.PatientRepository;
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
        LocalDateTime datetime = LocalDateTime.now();
        LocalDateTime newDate = datetime.plusDays(7);
        return appointmentRepository.findByStatusAndDateTimeBetween(
                Appointment.Status.AVAILABLE, LocalDateTime.now(), newDate)
                .stream().map(appointment -> mapStructMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentDto reserveAppointment(Long id, AppointmentDto newAppointmentDto) throws AppointmentNotFoundException, PatientNotFoundException, GeneralException {
        Appointment appointment = findById(id);
        Optional<Patient> optionalPatient = patientRepository.findById(newAppointmentDto.getPatient().getId());
        if (!optionalPatient.isPresent()) {
            throw new PatientNotFoundException("Patient not found");
        }
        if (appointment.getStatus().name().equalsIgnoreCase("available")) {
            appointment.setStatus(Appointment.Status.PENDING);
            appointment.setPatient(optionalPatient.get());
            return save(mapStructMapper.appointmentToAppointmentDto(appointment));
        }
        throw new GeneralException("This appointment is already reserved!");
    }

    @Override
    public AppointmentDto changeDoctor(Long id, AppointmentDto newAppointmentDto) {
        Appointment appointment = findById(id);
        Optional<Doctor> optionalDoctor = doctorRepository.findById(newAppointmentDto.getDoctor().getId());
        if (!optionalDoctor.isPresent()) {
            throw new DoctorNotFoundException("Doctor not found");
        }
        Doctor doctor = optionalDoctor.get();
        appointment.setDoctor(doctor);
        appointment.setStatus(Appointment.Status.CHANGED);
        return save(mapStructMapper.appointmentToAppointmentDto(appointment));
    }

    @Override
    public AppointmentDto changeStatus(Long id, Appointment.Status status) {
        Appointment appointment = findById(id);
        appointment.setStatus(status);
        return save(mapStructMapper.appointmentToAppointmentDto(appointment));
    }

    @Override
    public AppointmentDto updateToDone(Long id) {
        Appointment appointment = findById(id);
        Optional<String> optionalFeedback = Optional.ofNullable(appointment.getFeedback());
        if (optionalFeedback.isPresent()) {
            appointment.setStatus(Appointment.Status.DONE);
            return save(mapStructMapper.appointmentToAppointmentDto(appointment));
        }
        throw new GeneralException("Cannot update to DONE. No feedback from doctor!");
    }

    @Override
    public List<AppointmentDto> search(String keyword) {
        return null;
    }

    @Override
    public AppointmentDto cancelAppointment(Long id) throws AppointmentNotFoundException {

        Appointment appointment = findById(id);
        LocalDateTime current = LocalDateTime.now();
        LocalDateTime next = appointment.getDateTime();
        Duration duration = Duration.between(current, next);
        long seconds = duration.getSeconds();
        long hours = seconds / 3600;

        if (hours >= 24) {
            appointment.setStatus(Appointment.Status.CANCELED);
            appointment.setPatient(null);
            Optional<Doctor> optionalDoctor = Optional.ofNullable(appointment.getDoctor());
            if (optionalDoctor.isPresent()) {
                appointment.setDoctor(null);
            }
            return (save(mapStructMapper.appointmentToAppointmentDto(appointment)));
        }
        throw new GeneralException("Too short time to cancel Appointment!");
    }

    @Override
    public Appointment findById(Long id) throws AppointmentNotFoundException {
        return appointmentRepository.findById(id)
                .orElseThrow(()-> new AppointmentNotFoundException("No Appointment found with ID" + id));
    }

    @Override
    public List<AppointmentDto> findByPatient(Long patientId) {
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);
        if (!optionalPatient.isPresent()) {
            throw new PatientNotFoundException("Patient not found!");
        }
        return appointmentRepository.findByPatient(optionalPatient.get())
                .stream().map(appointment -> mapStructMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> findByDoctor(Long doctorId) {
        Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorId);
        if (!optionalDoctor.isPresent()) {
            throw new DoctorNotFoundException("Doctor Not found");
        }
        return appointmentRepository.findByDoctor(optionalDoctor.get())
                .stream().map(appointment -> mapStructMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> findByStatus(Appointment.Status status) {
        return appointmentRepository.findByStatus(status)
                .stream().map(appointment -> mapStructMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> findAllAppointments() {
        return appointmentRepository.findAll()
                .stream().map(appointment -> mapStructMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentDto save(AppointmentDto appointmentDto) {
        Appointment appointment = appointmentRepository.save(
                mapStructMapper.appointmentDtoToAppointment(appointmentDto));
        return mapStructMapper.appointmentToAppointmentDto(appointment);
    }

    @Override
    public List<AppointmentDto> findByStatusAndPatient(Appointment.Status status, Long patientId) {
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);
        if (!optionalPatient.isPresent()) {
            throw new PatientNotFoundException("Patient not found");
        }

        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusAndPatient(status, optionalPatient.get()))) {
            throw new AppointmentNotFoundException("No appointment found!");
        }
        return appointmentRepository.findByStatusAndPatient(status, optionalPatient.get())
                .stream().map(appointment -> mapStructMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> findByStatusAndDoctor(Appointment.Status status, Long doctorId) {
        Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorId);
        if (!optionalDoctor.isPresent()) {
            throw new DoctorNotFoundException("Doctor not found");
        }

        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusAndDoctor(status, optionalDoctor.get()))) {
            throw new AppointmentNotFoundException("No appointment found!");
        }
        return appointmentRepository.findByStatusAndDoctor(status, optionalDoctor.get())
                .stream().map(appointment -> mapStructMapper.appointmentToAppointmentDto(appointment))
                .collect(Collectors.toList());
    }
}