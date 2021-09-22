package com.ikub.reservationapp.repository;

import com.ikub.reservationapp.entity.Appointment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;

@Repository
public interface AppointmentRepository extends CrudRepository<Appointment, Long> {

    Iterable<Appointment> findByStatus(Appointment.Status status);

    Iterable<Appointment> findAllByStatusAndDateBetween(Appointment.Status status, Date current, Date endDate);

    Iterable<Appointment> findByStatusAndDateBetween(Appointment.Status status, Instant current, Instant endDate);
}
