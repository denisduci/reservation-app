package com.ikub.reservationapp.mongodb.mappers;

import com.ikub.reservationapp.mongodb.model.Role;
import com.ikub.reservationapp.mongodb.repository.RoleMongoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RoleMapper {

    private RoleMongoRepository roleMongoRepository;

    public Role mapStringToRole(String roleId) {
        return roleMongoRepository.findById(roleId).orElseGet(null);
    }
}

