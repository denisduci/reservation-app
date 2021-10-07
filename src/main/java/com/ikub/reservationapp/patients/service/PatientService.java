package com.ikub.reservationapp.patients.service;

import com.ikub.reservationapp.common.enums.Role;
import com.ikub.reservationapp.users.dto.UserDto;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PatientService {
//
    List<UserDto> search(String firstName, String lastName);

//    UserDto findPatientWithId(Long id);
    List<UserDto> findAllPatients();
}
