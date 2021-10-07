package com.ikub.reservationapp.doctors.repository;

import com.ikub.reservationapp.users.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DoctorRepository extends CrudRepository<UserEntity, Long> {

    @Query(value = "SELECT u FROM UserEntity u JOIN u.roles r where r=3 and u NOT IN (select a.doctor FROM AppointmentEntity a where (a.startTime >=:start AND a.endTime <=:end) OR (:start BETWEEN a.startTime AND a.endTime))")
    List<UserEntity> findAvailableDoctors(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<UserEntity> findByRolesName(String roleName);

}
