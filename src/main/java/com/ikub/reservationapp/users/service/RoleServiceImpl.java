package com.ikub.reservationapp.users.service;

import com.ikub.reservationapp.users.entity.RoleEntity;
import com.ikub.reservationapp.users.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public RoleEntity findByName(String name) {
        RoleEntity role = roleRepository.findRoleByName(name);
        return role;
    }
}