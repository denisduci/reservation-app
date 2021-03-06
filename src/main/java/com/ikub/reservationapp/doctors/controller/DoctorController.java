package com.ikub.reservationapp.doctors.controller;

import com.ikub.reservationapp.common.enums.Role;
import com.ikub.reservationapp.doctors.service.DoctorService;
import com.ikub.reservationapp.users.dto.UserDto;
import com.ikub.reservationapp.users.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;
    @Autowired
    private UserService userService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SECRETARY')")
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllDoctors() {
        log.info("Retrieving all doctors...");
        return new ResponseEntity<>(userService.findUsersByRole(Role.DOCTOR.name()), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<UserDto> saveDoctor(@Valid @RequestBody UserDto userDto) {
        log.info("Saving doctor {}...", userDto);
        return new ResponseEntity<>(doctorService.saveDoctor(userDto), HttpStatus.OK);
    }
}
