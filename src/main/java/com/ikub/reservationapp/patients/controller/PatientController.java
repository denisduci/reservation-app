package com.ikub.reservationapp.patients.controller;

import com.ikub.reservationapp.common.enums.Role;
import com.ikub.reservationapp.patients.service.PatientService;
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
@RequestMapping(value = "/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;
    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<UserDto> savePatient(@Valid @RequestBody UserDto userDto) {
        log.info("Saving patient...");
        return new ResponseEntity<>(userService.save(userDto), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SECRETARY')")
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllPatients() {
        log.info("Retrieving all patients...");
        return new ResponseEntity<>(userService.findUsersByRole(Role.PATIENT.name()), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SECRETARY','ROLE_DOCTOR')")
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchPatient(@RequestParam("firstname") String firstName,
                                                       @RequestParam("lastname") String lastName) {
        log.info("Searching for patient with firstName {} and/or lastName {} ...", firstName, lastName);
        return new ResponseEntity<>(patientService.search(firstName, lastName), HttpStatus.OK);
    }


}
