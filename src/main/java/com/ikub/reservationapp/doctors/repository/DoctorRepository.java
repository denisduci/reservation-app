package com.ikub.reservationapp.doctors.repository;

import com.ikub.reservationapp.doctors.dto.DoctorReportDto;
import com.ikub.reservationapp.doctors.entity.DoctorEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DoctorRepository extends CrudRepository<DoctorEntity, Long> {

    //Doctor findById(Long id);

    @Query(value = "Select new com.ikub.reservationapp.doctors.dto.DoctorReportDto(d.firstName, count(d)) FROM AppointmentEntity a JOIN a.doctor d GROUP BY d.firstName ORDER BY COUNT(d) DESC")
    List<DoctorReportDto> findByOrderByDoctorDesc();

    @Query(value = "SELECT d FROM DoctorEntity d WHERE d NOT IN (select e FROM AppointmentEntity a JOIN a.doctor e where a.startTime >=:start AND a.endTime <=:end OR :start BETWEEN a.startTime AND a.endTime)")
    List<DoctorEntity> findAvailableDoctors(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
