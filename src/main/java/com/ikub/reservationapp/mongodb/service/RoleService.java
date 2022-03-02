package com.ikub.reservationapp.mongodb.service;

import com.ikub.reservationapp.mongodb.model.Role;

public interface RoleService {
    Role getRoleByName(String roleName);
}
