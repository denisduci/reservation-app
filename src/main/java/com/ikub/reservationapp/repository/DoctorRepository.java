package com.ikub.reservationapp.repository;

import com.ikub.reservationapp.entity.Doctor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends CrudRepository<Doctor, Long> {
}
