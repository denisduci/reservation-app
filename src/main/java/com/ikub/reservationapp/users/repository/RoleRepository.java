package com.ikub.reservationapp.users.repository;

import com.ikub.reservationapp.users.entity.RoleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Long> {
    RoleEntity findRoleByName(String name);
}