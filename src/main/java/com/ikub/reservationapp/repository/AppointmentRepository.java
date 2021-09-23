package com.ikub.reservationapp.repository;

import com.ikub.reservationapp.entity.Appointment;
import com.ikub.reservationapp.entity.Doctor;
import com.ikub.reservationapp.entity.Patient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends CrudRepository<Appointment, Long> {

    List<Appointment> findByStatus(Appointment.Status status);
    List<Appointment> findByPatient(Patient patient);
    List<Appointment> findByDoctor(Doctor doctor);
    List<Appointment> findAll();
    List<Appointment> findByStatusAndPatient(Appointment.Status status, Patient patient);
    List<Appointment> findByStatusAndDoctor(Appointment.Status status, Doctor doctor);
    List<Appointment> findByStatusAndDateTimeBetween(Appointment.Status status, LocalDateTime current, LocalDateTime endDate);
}
