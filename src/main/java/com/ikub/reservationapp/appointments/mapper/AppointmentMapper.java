package com.ikub.reservationapp.appointments.mapper;

import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.appointments.dto.AppointmentResponseDto;
import com.ikub.reservationapp.appointments.entity.AppointmentEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AppointmentMapper {
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "appointmentDate", target = "appointmentDate"),
            @Mapping(source = "startTime", target = "startTime"),
            @Mapping(source = "endTime", target = "endTime"),
            @Mapping(source = "feedback", target = "feedback"),
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "comments", target = "comments"),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "doctor", target = "doctor"),
            @Mapping(source = "patient", target = "patient"),

    })
    AppointmentDto toDto(AppointmentEntity appointmentEntity);
    @InheritInverseConfiguration
    AppointmentEntity toEntity(AppointmentDto appointmentDto);

    void updateAppointmentFromDto(AppointmentDto dto, @MappingTarget AppointmentEntity entity);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "appointmentDate", target = "appointmentDate"),
            @Mapping(source = "startTime", target = "startTime"),
            @Mapping(source = "endTime", target = "endTime"),
            @Mapping(source = "feedback", target = "feedback"),
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "comments", target = "comments"),
            @Mapping(source = "status", target = "status"),
            @Mapping(target = "doctorName", expression = "java(appointmentEntity.getDoctor().getFirstName() + \" \" + appointmentEntity.getDoctor().getLastName())"),
            @Mapping(target = "patientName", expression = "java(appointmentEntity.getPatient().getFirstName() + \" \" + appointmentEntity.getPatient().getLastName())")
    })
    AppointmentResponseDto toResponseDto(AppointmentEntity appointmentEntity);

    @Mappings({
            @Mapping(source = "appointmentDate", target = "appointmentDate"),
            @Mapping(source = "startTime", target = "startTime"),
            @Mapping(source = "endTime", target = "endTime"),
            @Mapping(target = "doctorName", expression = "java(appointmentDto.getDoctor().getFirstName() + \" \" + appointmentDto.getDoctor().getLastName())"),
            @Mapping(target = "patientName", expression = "java(appointmentDto.getPatient().getFirstName() + \" \" + appointmentDto.getPatient().getLastName())")
    })
    AppointmentResponseDto dtoToResponseDto(AppointmentDto appointmentDto);
}
