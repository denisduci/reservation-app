package com.ikub.reservationapp.controller;

import com.ikub.reservationapp.entity.Doctor;
import com.ikub.reservationapp.exception.DoctorNotFoundException;
import com.ikub.reservationapp.service.DoctorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping
    public ResponseEntity<?> saveDoctor(@RequestBody Doctor doctor) {
        log.info("Saving doctor...");
        return new ResponseEntity<>(doctorService.save(doctor), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable("id") Long id) throws DoctorNotFoundException {
        log.info("Retrieving doctor by id...");
        return new ResponseEntity<>(doctorService.findById(id), HttpStatus.OK);
    }
}
