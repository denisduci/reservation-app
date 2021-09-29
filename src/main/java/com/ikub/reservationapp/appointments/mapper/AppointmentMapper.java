package com.ikub.reservationapp.appointments.mapper;

import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.appointments.entity.AppointmentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    AppointmentDto appointmentToAppointmentDto(AppointmentEntity appointmentEntity);
    AppointmentEntity appointmentDtoToAppointment(AppointmentDto appointmentDto);

}
