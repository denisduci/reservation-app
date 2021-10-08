package com.ikub.reservationapp.doctors.service;

import com.ikub.reservationapp.common.enums.Role;
import com.ikub.reservationapp.common.exception.PasswordNotValidException;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import com.ikub.reservationapp.doctors.repository.DoctorRepository;
import com.ikub.reservationapp.users.dto.UserDto;
import com.ikub.reservationapp.users.entity.RoleEntity;
import com.ikub.reservationapp.users.mapper.UserMapper;
import com.ikub.reservationapp.users.repository.UserRepository;
import com.ikub.reservationapp.users.service.RoleService;
import com.ikub.reservationapp.users.utils.PasswordValidationUtil;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordValidationUtil passwordValidation;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private RoleService roleService;


//    @Override
//    public List<DoctorReportDto> findDoctors() {
//        return doctorRepository.findByOrderByDoctorDesc();
//    }

    @Override
    public List<UserDto> findAvailableDoctors(LocalDateTime start, LocalDateTime end) {
        return doctorRepository.findAvailableDoctors(start, end)
                .stream().map(userEntity -> userMapper.userToUserDto(userEntity))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto saveDoctor(UserDto userDto) {
        val userEntity = userMapper.userDtoToUser(userDto);
        //1 - CHECK IF USER EXISTS | THROW EXCEPTION
        Optional.ofNullable(userRepository.findByUsername(userDto.getUsername()))
                .ifPresent(user -> {
                    throw new ReservationAppException("User with username already exists!");
                });
        //2 - CHECK PASSWORD VALIDATION | THROW EXCEPTION
        if (!passwordValidation.isValid(userDto.getPassword())) {
            throw new PasswordNotValidException(Arrays.asList("Password doesn't meet security!"));
        }
        //3 - CHECK PASSWORD MATCH | THROW EXCEPTION
        if (!passwordValidation.isPasswordMatch(userDto.getPassword(), userDto.getConfirmPassword())) {
            throw new PasswordNotValidException(Arrays.asList("Passwords do not match!"));
        }
        userEntity.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        RoleEntity role = roleService.findByName(Role.DOCTOR.name());
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(role);
        userEntity.setRoles(roles);
        return userMapper.userToUserDto(userRepository.save(userEntity));
    }

}
