package com.ikub.reservationapp.service;

import com.ikub.reservationapp.entity.Doctor;
import com.ikub.reservationapp.exception.DoctorNotFoundException;
import com.ikub.reservationapp.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Override
    public Doctor findById(Long id) throws DoctorNotFoundException {
        return doctorRepository.findById(id)
                .orElseThrow(()-> new DoctorNotFoundException("Doctor not found with id" + id));
    }

    @Override
    public Doctor save(Doctor doctor) {
        return doctorRepository.save(doctor);
    }
}
