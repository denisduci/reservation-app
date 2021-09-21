package com.ikub.reservationapp.repository;

import com.ikub.reservationapp.entity.Patient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends CrudRepository<Patient, Long> {
}
