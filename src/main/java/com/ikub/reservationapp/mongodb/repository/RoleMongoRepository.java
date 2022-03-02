package com.ikub.reservationapp.mongodb.repository;

import com.ikub.reservationapp.mongodb.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleMongoRepository extends MongoRepository<Role, String> {
    Role findByName(String name);
}
