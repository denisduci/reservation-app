package com.ikub.reservationapp.doctors.mapper;

import com.ikub.reservationapp.doctors.dto.DoctorDto;
import com.ikub.reservationapp.doctors.entity.DoctorEntity;
import org.mapstruct.Mapper;;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    DoctorDto doctorToDoctorDto(DoctorEntity doctorEntity);
    DoctorEntity doctorDtoToDoctor(DoctorDto doctorDto);

}
