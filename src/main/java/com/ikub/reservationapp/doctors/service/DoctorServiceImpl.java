package com.ikub.reservationapp.doctors.service;

import com.ikub.reservationapp.doctors.dto.DoctorDto;
import com.ikub.reservationapp.doctors.dto.DoctorReportDto;
import com.ikub.reservationapp.doctors.exception.DoctorNotFoundException;
import com.ikub.reservationapp.doctors.mapper.DoctorMapper;
import com.ikub.reservationapp.doctors.repository.DoctorRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        //get current date
        //doctor_id     | count_date
        //5             | 2021-09-30
        //7             | 2021-08-31
        //5             | 2009-04-28
        //7             | 2009-04-27

        return doctorRepository.findByOrderByDoctorDesc();
    }

    @Override
    public List<DoctorDto> findAvailableDoctors(LocalDateTime start, LocalDateTime end) {
        return doctorRepository.findAvailableDoctors(start, end)
                .stream().map(doctorEntity -> doctorMapper.doctorToDoctorDto(doctorEntity))
                .collect(Collectors.toList());
    }
}
