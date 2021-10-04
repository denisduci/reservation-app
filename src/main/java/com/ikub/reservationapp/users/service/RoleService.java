package com.ikub.reservationapp.users.service;

import com.ikub.reservationapp.users.entity.RoleEntity;

public interface RoleService {
    RoleEntity findByName(String name);
    boolean hasRole(String role);
}