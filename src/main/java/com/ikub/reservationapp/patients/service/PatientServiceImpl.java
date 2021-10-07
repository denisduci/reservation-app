package com.ikub.reservationapp.patients.service;

import com.ikub.reservationapp.common.enums.Role;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import com.ikub.reservationapp.patients.repository.PatientRepository;
import com.ikub.reservationapp.users.dto.UserDto;
import com.ikub.reservationapp.users.mapper.UserMapper;
import com.ikub.reservationapp.users.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<UserDto> findAllPatients() {
        return patientRepository.findByRolesName(Role.PATIENT.name())
                .stream().map(userEntity -> userMapper.userToUserDto(userEntity))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> search(String firstName, String lastName) {
        if (!(StringUtils.isEmpty(firstName) && StringUtils.isEmpty(lastName))) {
            return patientRepository.findByFirstNameOrLastNameContainingAllIgnoreCase(firstName, lastName)
                    .stream().map(patient -> userMapper.userToUserDto(patient))
                    .collect(Collectors.toList());
        }
        throw new ReservationAppException("No value to search");
    }
}