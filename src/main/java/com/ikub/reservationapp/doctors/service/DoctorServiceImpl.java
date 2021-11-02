package com.ikub.reservationapp.doctors.service;

import com.ikub.reservationapp.appointments.repository.AppointmentRepository;
import com.ikub.reservationapp.common.enums.Role;
import com.ikub.reservationapp.common.exception.PasswordNotValidException;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import com.ikub.reservationapp.doctors.repository.DoctorRepository;
import com.ikub.reservationapp.users.dto.UserDto;
import com.ikub.reservationapp.users.entity.RoleEntity;
import com.ikub.reservationapp.users.mapper.UserMapper;
import com.ikub.reservationapp.users.repository.UserRepository;
import com.ikub.reservationapp.users.service.RoleService;
import com.ikub.reservationapp.users.validators.PasswordValidationUtil;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;
    @Resource(name = "bcrypt")
    private PasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private RoleService roleService;


//    @Override
//    public List<DoctorReportDto> findDoctors() {
//        return doctorRepository.findByOrderByDoctorDesc();
//    }

    @Override
    public boolean hasAvailableDoctors(LocalDateTime start, LocalDateTime end) {
        return doctorRepository.findAvailableDoctors(start, end)
                .stream().map(userEntity -> userMapper.toDto(userEntity))
                .collect(Collectors.toList()).stream().count() > 0;
    }

    @Override
    public boolean isDoctorAvailable(UserDto doctor, LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByDoctorAvailability(
                userMapper.toEntity(doctor), start, end)
                .stream().count() == 0;
    }

    @Override
    public UserDto saveDoctor(UserDto userDto) {
        val userEntity = userMapper.toEntity(userDto);
        //1 - CHECK IF USER EXISTS | THROW EXCEPTION
        Optional.ofNullable(userRepository.findByUsername(userDto.getUsername()))
                .ifPresent(user -> {
                    throw new ReservationAppException("User with username already exists!");
                });
        //2 - CHECK PASSWORD MATCH | THROW EXCEPTION
        if (!PasswordValidationUtil.isPasswordMatch(userDto.getPassword(), userDto.getConfirmPassword())) {
            throw new PasswordNotValidException(Arrays.asList("Passwords do not match!"));
        }
        userEntity.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        RoleEntity role = roleService.findByName(Role.DOCTOR.name());
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(role);
        userEntity.setRoles(roles);
        return userMapper.toDto(userRepository.save(userEntity));
    }

}
