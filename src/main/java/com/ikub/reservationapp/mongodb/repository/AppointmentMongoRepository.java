package com.ikub.reservationapp.mongodb.repository;

import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.mongodb.model.Appointment;
import com.ikub.reservationapp.users.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Repository
public interface AppointmentMongoRepository extends MongoRepository<Appointment, String> {
    List<Appointment> findByPatient(String doctorId);

    List<Appointment> findByAppointmentDateAndPatientAndStatusIn(String appointmentDate, String patient, List<Status> statuses);

    List<Appointment> findByAppointmentDateAndPatient(String appointmentDate, String patient);

    List<Appointment> findByAppointmentDateAndStatusNotIn(String date, List<Status> statuses);


//    @Query(value = "SELECT u FROM UserEntity u JOIN u.roles r where r=3 and u NOT IN (select a.doctor FROM AppointmentEntity a where (a.startTime >=:start AND a.endTime <=:end) OR (:start BETWEEN a.startTime AND a.endTime))")
//
//    // select user from users where user_id not in (   select doctor from appointment where startTime>=startTime and endTime <= end_time)
//    List<UserEntity> findAvailableDoctors(@Param("start") LocalTime start, @Param("end") LocalTime end);
}
