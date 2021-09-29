package com.ikub.reservationapp.doctors.service;

import com.ikub.reservationapp.doctors.dto.DoctorDto;
import com.ikub.reservationapp.doctors.dto.DoctorReportDto;
import com.ikub.reservationapp.doctors.exception.DoctorNotFoundException;
import com.ikub.reservationapp.doctors.mapper.DoctorMapper;
import com.ikub.reservationapp.doctors.repository.DoctorRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorMapper doctorMapper;

    @Override
    public DoctorDto findById(Long id) throws DoctorNotFoundException {
        return doctorMapper.doctorToDoctorDto(doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id" + id)));
    }

    @Override
    public DoctorDto save(DoctorDto doctorDto) {
        val doctor = doctorRepository.save(doctorMapper.doctorDtoToDoctor(doctorDto));
        return doctorMapper.doctorToDoctorDto(doctor);
    }

    @Override
    public List<DoctorReportDto> findDoctors() {
        return doctorRepository.findByOrderByDoctorDesc();
    }
}
