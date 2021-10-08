package com.ikub.reservationapp.patients.service;

import com.ikub.reservationapp.common.exception.ReservationAppException;
import com.ikub.reservationapp.patients.repository.PatientRepository;
import com.ikub.reservationapp.users.dto.UserDto;
import com.ikub.reservationapp.users.mapper.UserMapper;
import com.ikub.reservationapp.users.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<UserDto> search(String firstName, String lastName) {
        if (!(StringUtils.isEmpty(firstName) && StringUtils.isEmpty(lastName))) {
            return patientRepository.findByFirstNameOrLastNameContainingAllIgnoreCase(firstName, lastName)
                    .stream().map(patient -> {
                        log.info("Found patient {}", patient);
                        return userMapper.userToUserDto(patient);
                    })
                    .collect(Collectors.toList());
        }
        log.warn("No patient found!");
        throw new ReservationAppException("No value to search");
    }
}