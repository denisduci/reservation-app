package com.ikub.reservationapp.service;

import com.ikub.reservationapp.entity.Role;

public interface RoleService {
    Role findByName(String name);
}