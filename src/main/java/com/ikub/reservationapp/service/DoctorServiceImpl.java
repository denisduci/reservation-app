package com.ikub.reservationapp.service;

import com.ikub.reservationapp.dto.DoctorDto;
import com.ikub.reservationapp.entity.Doctor;
import com.ikub.reservationapp.exception.DoctorNotFoundException;
import com.ikub.reservationapp.mapper.MapStructMapper;
import com.ikub.reservationapp.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private MapStructMapper mapStructMapper;

    @Override
    public Doctor findById(Long id) throws DoctorNotFoundException {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id" + id));
    }

    @Override
    public DoctorDto save(DoctorDto doctorDto) {
        Doctor doctor = doctorRepository.save(mapStructMapper.doctorDtoToDoctor(doctorDto));
        return mapStructMapper.doctorToDoctorDto(doctor);
    }
}
