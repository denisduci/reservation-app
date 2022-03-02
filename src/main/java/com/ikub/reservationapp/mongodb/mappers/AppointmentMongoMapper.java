package com.ikub.reservationapp.mongodb.mappers;

import com.ikub.reservationapp.mongodb.dto.AppointmentDto;
import com.ikub.reservationapp.mongodb.dto.AppointmentResponseDto;
import com.ikub.reservationapp.mongodb.model.Appointment;
import com.ikub.reservationapp.mongodb.repository.UserMongoRepository;
import com.ikub.reservationapp.mongodb.utils.DateTransformerUtil;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AppointmentMongoMapper {

    private UserMongoMapper userMongoMapper;
    private UserMongoRepository userMongoRepository;

    public Appointment updateAppointmentFromDto(AppointmentDto appointmentDto, Appointment appointment) {
        if (appointmentDto.getId() != null)
            if (appointmentDto.getAppointmentDate() != null)
                appointment.setAppointmentDate(appointmentDto.getAppointmentDate().toString());
        if (appointmentDto.getDescription() != null)
            appointment.setDescription(appointmentDto.getDescription());
        if (appointmentDto.getFeedback() != null)
            appointment.setFeedback(appointmentDto.getFeedback());
        if (appointmentDto.getStartTime() != null)
            appointment.setStartTime(appointmentDto.getStartTime().toString());
        if (appointmentDto.getEndTime() != null)
            appointment.setEndTime(appointmentDto.getEndTime().toString());
        if (appointmentDto.getStatus() != null)
            appointment.setStatus(appointmentDto.getStatus());
        if (appointmentDto.getId() != null)
            appointment.setId(appointmentDto.getId());
        if (appointmentDto.getDoctor() != null)
            appointment.setDoctor(appointmentDto.getDoctor());
        if (appointmentDto.getPatient() != null)
            appointment.setPatient(appointmentDto.getPatient());
        return appointment;
    }

    public Appointment toAppointmentModel(AppointmentDto appointmentModelDto) {
        Appointment response = new Appointment();
        if (appointmentModelDto == null)
            return null;
        if (appointmentModelDto.getAppointmentDate() != null)
            response.setAppointmentDate(appointmentModelDto.getAppointmentDate().toString());
        if (appointmentModelDto.getDescription() != null)
            response.setDescription(appointmentModelDto.getDescription());
        if (appointmentModelDto.getFeedback() != null)
            response.setFeedback(appointmentModelDto.getFeedback());
        if (appointmentModelDto.getStartTime() != null)
            response.setStartTime(appointmentModelDto.getStartTime().toString());
        if (appointmentModelDto.getEndTime() != null)
            response.setEndTime(appointmentModelDto.getEndTime().toString());
        if (appointmentModelDto.getStatus() != null)
            response.setStatus(appointmentModelDto.getStatus());
        if (appointmentModelDto.getId() != null)
            response.setId(appointmentModelDto.getId());
        if (appointmentModelDto.getDoctor() != null)
            response.setDoctor(appointmentModelDto.getDoctor());
        if (appointmentModelDto.getPatient() != null)
            response.setPatient(appointmentModelDto.getPatient());
        return response;
    }

    public AppointmentDto toAppointmentDto(Appointment appointmentModel) {
        AppointmentDto response = new AppointmentDto();
        if (appointmentModel == null)
            return null;
        if (appointmentModel.getAppointmentDate() != null) {
            val appointmentDate = DateTransformerUtil.stringToDate(appointmentModel.getAppointmentDate());
            response.setAppointmentDate(appointmentDate);
        }
        if (appointmentModel.getStartTime() != null) {
            val startTime = DateTransformerUtil.stringToTime(appointmentModel.getStartTime());
            response.setStartTime(startTime);
        }
        if (appointmentModel.getEndTime() != null) {
            val endTime = DateTransformerUtil.stringToTime(appointmentModel.getEndTime());
            response.setEndTime(endTime);
        }
        if (appointmentModel.getFeedback() != null)
            response.setFeedback(appointmentModel.getFeedback());
        if (appointmentModel.getDescription() != null)
            response.setDescription(appointmentModel.getDescription());
        if (appointmentModel.getStatus() != null)
            response.setStatus(appointmentModel.getStatus());
        if (appointmentModel.getDoctor() != null)
            response.setDoctor(appointmentModel.getDoctor());
        if (appointmentModel.getPatient() != null)
            response.setPatient(appointmentModel.getPatient());
        if (appointmentModel.getId() != null)
            response.setId(appointmentModel.getId());
        return response;
    }

    public AppointmentResponseDto toResponseDto(Appointment appointment) {
        AppointmentResponseDto response = new AppointmentResponseDto();
        if (appointment == null)
            return null;
        if (appointment.getAppointmentDate() != null)
            response.setAppointmentDate(appointment.getAppointmentDate());
        if (appointment.getStartTime() != null)
            response.setStartTime(appointment.getStartTime());
        if (appointment.getEndTime() != null)
            response.setEndTime(appointment.getEndTime());
        if (appointment.getDescription() != null)
            response.setDescription(appointment.getDescription());
        if (appointment.getFeedback() != null)
            response.setFeedback(appointment.getFeedback());
        if (appointment.getStatus() != null)
            response.setStatus(appointment.getStatus());
        if (appointment.getDoctor() != null) {
            val doctor = userMongoRepository.findById(appointment.getDoctor()).orElseGet(null);
            val doctorResponse = userMongoMapper.toResponseDto(doctor);
            response.setDoctor(doctorResponse);
        }
        if (appointment.getPatient() != null) {
            val patient = userMongoRepository.findById(appointment.getPatient()).orElseGet(null);
            val patientResponse = userMongoMapper.toResponseDto(patient);
            response.setPatient(patientResponse);
        }
        if (appointment.getId() != null)
            response.setId(appointment.getId());

        return response;
    }
}
