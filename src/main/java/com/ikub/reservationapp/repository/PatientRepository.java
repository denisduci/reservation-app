package com.ikub.reservationapp.repository;

import com.ikub.reservationapp.entity.Patient;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends CrudRepository<Patient, Long> {

    @Nullable
    List<Patient> findByFirstNameOrLastNameContaining(@Nullable String firstName, @Nullable String lastName);
//@Query("SELECT p FROM Patient p WHERE CONCAT(p.firstName, p.lastName) LIKE %?1%")
//List<Patient> search(String keyword);
}
