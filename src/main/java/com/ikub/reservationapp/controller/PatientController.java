package com.ikub.reservationapp.controller;

import com.ikub.reservationapp.dto.PatientDto;
import com.ikub.reservationapp.entity.Patient;
import com.ikub.reservationapp.exception.PatientNotFoundException;
import com.ikub.reservationapp.mapper.MapStructMapper;
import com.ikub.reservationapp.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private MapStructMapper mapStructMapper;

    @PostMapping
    public ResponseEntity<PatientDto> savePatient(@RequestBody PatientDto patientDto) {
        log.info("Saving patient...");
        return new ResponseEntity<>(patientService.save(patientDto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable("id") Long id) throws PatientNotFoundException {
        log.info("Retrieving patient by id...");
        Patient patient = patientService.findById(id);
        return new ResponseEntity<>(mapStructMapper.patientToPatientDto(patient), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PatientDto>> searchPatient( @RequestParam("firstname") String firstName,
                                                           @RequestParam("lastname") String lastName) throws PatientNotFoundException {
        log.info("Searching for patient...");
        return new ResponseEntity<>(patientService.search(firstName, lastName), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDto> updatePatient(@PathVariable("id") Long id,
                                                    @RequestBody PatientDto patientDto) throws PatientNotFoundException {
        log.info("Updating patient...");
        return new ResponseEntity<>(patientService.updatePatient(id, patientDto), HttpStatus.OK);
    }
}
