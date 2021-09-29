package com.ikub.reservationapp.doctors.controller;

import com.ikub.reservationapp.doctors.dto.DoctorDto;
import com.ikub.reservationapp.doctors.dto.DoctorReportDto;
import com.ikub.reservationapp.doctors.exception.DoctorNotFoundException;
import com.ikub.reservationapp.doctors.service.DoctorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorDto> saveDoctor(@RequestBody DoctorDto doctorDto) {
        log.info("Saving doctor...");
        return new ResponseEntity<>(doctorService.save(doctorDto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable("id") Long id) throws DoctorNotFoundException {
        log.info("Retrieving doctor by id...");
        return new ResponseEntity<>(doctorService.findById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<DoctorReportDto>> findDoctors() {
        log.info("Retrieving Doctors report...");
        return new ResponseEntity<>(doctorService.findDoctors(), HttpStatus.OK);
    }
}
