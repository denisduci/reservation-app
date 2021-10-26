package com.ikub.reservationapp.patients.service;

import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.appointments.entity.AppointmentEntity;
import com.ikub.reservationapp.appointments.repository.AppointmentRepository;
import com.ikub.reservationapp.appointments.utils.DateUtil;
import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import com.ikub.reservationapp.patients.repository.PatientRepository;
import com.ikub.reservationapp.users.dto.UserDto;
import com.ikub.reservationapp.users.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<UserDto> search(String firstName, String lastName) {
        if (!(StringUtils.isEmpty(firstName) && StringUtils.isEmpty(lastName))) {
            return patientRepository.findByFirstNameOrLastNameContainingAllIgnoreCase(firstName, lastName)
                    .stream().map(patient -> {
                        log.info("Found patient {}", patient);
                        return userMapper.toDto(patient);
                    })
                    .collect(Collectors.toList());
        }
        log.warn("No patient found!");
        throw new ReservationAppException("No value to search");
    }

    @Override
    public boolean hasAppointment(AppointmentDto appointmentDto, UserDto patient) {

        List<Status> eligibleStatuses = Arrays.stream(Status.values()).filter(status -> status.equals(Status.PENDING) ||
                status.equals(Status.APPROVED) || status.equals(Status.DOCTOR_CHANGE_REQUEST) || status.equals(Status.DOCTOR_CHANGE_APPROVED))
                .collect(Collectors.toList());

        val patientAppointments = appointmentRepository.findByAppointmentDateAndPatientAndStatusIn(appointmentDto.getAppointmentDate(),
                userMapper.toEntity(patient), eligibleStatuses);

        List<AppointmentEntity> overlappingAppointments = patientAppointments.stream().filter(appointmentEntity ->
                DateUtil.isOverlapping(appointmentDto.getStartTime(), appointmentDto.getEndTime(),
                        appointmentEntity.getStartTime(),
                        appointmentEntity.getEndTime()) == true)
                .collect(Collectors.toList());

        if (overlappingAppointments.stream().count() > 0)
            return true;
        return false;
    }
}