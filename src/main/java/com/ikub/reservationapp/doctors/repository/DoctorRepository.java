package com.ikub.reservationapp.doctors.repository;

import com.ikub.reservationapp.doctors.dto.DoctorReportDto;
import com.ikub.reservationapp.doctors.entity.DoctorEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DoctorRepository extends CrudRepository<DoctorEntity, Long> {

    //Doctor findById(Long id);

    @Query(value = "Select new com.ikub.reservationapp.doctors.dto.DoctorReportDto(d.firstName, count(d)) FROM AppointmentEntity a JOIN a.doctor d GROUP BY d.firstName ORDER BY COUNT(d) DESC")
    List<DoctorReportDto> findByOrderByDoctorDesc();
}
