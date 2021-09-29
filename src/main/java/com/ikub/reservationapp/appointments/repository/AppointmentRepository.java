package com.ikub.reservationapp.appointments.repository;

import com.ikub.reservationapp.appointments.entity.AppointmentEntity;
import com.ikub.reservationapp.doctors.entity.DoctorEntity;
import com.ikub.reservationapp.patients.entity.PatientEntity;
import com.ikub.reservationapp.common.enums.Status;
import org.apache.tomcat.jni.Local;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends CrudRepository<AppointmentEntity, Long> {

    List<AppointmentEntity> findByStatus(Status status);
    @Query("select a from AppointmentEntity a where a.appointmentDate =:appointmentDateTime")
    List<AppointmentEntity> findByAppointmentDate(@Param("appointmentDateTime") LocalDate appointmentDate);
    List<AppointmentEntity> findByPatient(PatientEntity patient);
    List<AppointmentEntity> findByDoctor(DoctorEntity doctor);
    List<AppointmentEntity> findAll();
    List<AppointmentEntity> findByStatusAndPatient(Status status, PatientEntity patient);
    List<AppointmentEntity> findByStatusAndDoctor(Status status, DoctorEntity doctor);
    //List<AppointmentEntity> findByStatusAndAppointmentDateBetween(Status status, LocalDateTime current, LocalDateTime endDate);
}