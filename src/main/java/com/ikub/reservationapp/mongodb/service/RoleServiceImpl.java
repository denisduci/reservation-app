package com.ikub.reservationapp.mongodb.service;

import com.ikub.reservationapp.mongodb.model.Role;
import com.ikub.reservationapp.mongodb.repository.RoleMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleMongoRepository roleMongoRepository;

    @Override
    public Role getRoleByName(String roleName) {
        return roleMongoRepository.findByName(roleName);
    }
}
