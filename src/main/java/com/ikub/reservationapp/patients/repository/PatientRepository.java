package com.ikub.reservationapp.patients.repository;

import com.ikub.reservationapp.users.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PatientRepository extends CrudRepository<UserEntity, Long> {

    @Nullable
    List<UserEntity> findByFirstNameOrLastNameContainingAllIgnoreCase(@Nullable String firstName, @Nullable String lastName);

    List<UserEntity> findByRolesName(String roleName);

}
