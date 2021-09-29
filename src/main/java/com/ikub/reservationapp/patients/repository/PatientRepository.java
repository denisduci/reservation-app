package com.ikub.reservationapp.patients.repository;

import com.ikub.reservationapp.patients.entity.PatientEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends CrudRepository<PatientEntity, Long> {

    @Nullable
    List<PatientEntity> findByFirstNameOrLastNameContainingAllIgnoreCase(@Nullable String firstName, @Nullable String lastName);
//@Query("SELECT p FROM Patient p WHERE CONCAT(p.firstName, p.lastName) LIKE %?1%")
//List<Patient> search(String keyword);
}
