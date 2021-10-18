package com.ikub.reservationapp.appointments.service;

import com.ikub.reservationapp.appointments.constants.AppointmentConstants;
import com.ikub.reservationapp.appointments.dto.AppointmentDateHourDto;
import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.appointments.dto.AppointmentResponseDto;
import com.ikub.reservationapp.appointments.entity.AppointmentEntity;
import com.ikub.reservationapp.appointments.mapper.AppointmentMapper;
import com.ikub.reservationapp.appointments.validators.AppointmentValidator;
import com.ikub.reservationapp.appointments.utils.DateUtil;
import com.ikub.reservationapp.common.enums.Role;
import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.common.exception.BadRequest;
import com.ikub.reservationapp.common.exception.NotFound;
import com.ikub.reservationapp.doctors.service.DoctorService;
import com.ikub.reservationapp.patients.service.PatientService;
import com.ikub.reservationapp.users.mapper.UserMapper;
import com.ikub.reservationapp.users.service.RoleService;
import com.ikub.reservationapp.users.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import com.ikub.reservationapp.appointments.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import com.ikub.reservationapp.appointments.repository.AppointmentRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;
    @Autowired
    private AppointmentMapper appointmentMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public AppointmentDateHourDto getAllAvailableHours() {//DONE
        String loggedInUsername = userService.getUsernameFromContext();
        log.info("Authenticated username is: -> {}", loggedInUsername);
        Map<LocalDate, List<LocalDateTime>> allAvailableAppointmentDatesAndHours = new HashMap<>();
        val datesToIterate = DateUtil.datesFromNowToSpecificDay(AppointmentConstants.DAYS_TO_ITERATE);
        log.info("Dates to iterate: -> {}", datesToIterate);

        datesToIterate.forEach(nextDate -> {
            List<LocalDateTime> reservedHours = new ArrayList<>();
            List<LocalDateTime> availableHours = DateUtil.createAllAvailableHours(nextDate);//1-Create All available hours
            List<AppointmentDto> reservedAppointments = getAppointmentByDate(nextDate);
            log.info("Reserved appointments in date: -> {} are: -> {}", nextDate, reservedAppointments);

            reservedAppointments.forEach(reservedAppointment -> { //2-Create ALL Reserved Hours
                int startHour = reservedAppointment.getStartTime().getHour();
                int endHour = reservedAppointment.getEndTime().getHour();
                if (roleService.hasRole(Role.DOCTOR.name())) { //if doctor is logged in then check if reserved appointment belongs to this doctor.
                    val existingDoctor = userService.findByUsername(loggedInUsername);
                    if (reservedAppointment.getDoctor().getUsername().equals(existingDoctor.getUsername()))
                        addToReservations(startHour, endHour, reservedAppointment, reservedHours); //Add reserved hours
                } else {
                    if (!doctorService.hasAvailableDoctors(reservedAppointment.getStartTime(), reservedAppointment.getEndTime()))
                        addToReservations(startHour, endHour, reservedAppointment, reservedHours);
                }
            });
            log.info("Reserved hours: -> {}", reservedHours);
            removeReservedHoursFromAllAvailableHours(reservedHours, availableHours);
            log.info("Available hours for date -> {} are: -> {}", nextDate, availableHours);
            allAvailableAppointmentDatesAndHours.put(nextDate, availableHours);
        });
        AppointmentDateHourDto allFreeAppointmentHours = new AppointmentDateHourDto(allAvailableAppointmentDatesAndHours);
        return allFreeAppointmentHours;
    }

    public void removeReservedHoursFromAllAvailableHours(List<LocalDateTime> reservedHours, List<LocalDateTime> availableHours) {
        reservedHours.forEach(reservedHour ->
                availableHours.removeIf(availableHour -> availableHour.equals(reservedHour)));
    }

    public void addToReservations(int startHour, int endHour, AppointmentDto reservedAppointment, List<LocalDateTime> reservedHours) {
        log.info("Inside addToReservations...");
        for (int hour = startHour; hour < endHour; hour++) {
            reservedHours.add(LocalDateTime.of(reservedAppointment.getStartTime().getYear(),
                    reservedAppointment.getStartTime().getMonth(),
                    reservedAppointment.getStartTime().getDayOfMonth(),
                    hour, 0, 0, 0));
        }
    }

    @Override
    public AppointmentResponseDto createAppointment(AppointmentDto appointmentDto) throws ReservationAppException {//DONE
        val loggedInUsername = userService.getUsernameFromContext();
        log.info("Creating appointment: -> {}, authenticated username is: -> {}", appointmentDto, loggedInUsername);
        AppointmentValidator.validateAppointment(appointmentDto);
        val patient = userService.findByIdAndRole(appointmentDto.getPatient().getId(), Role.PATIENT.name());
        if (patientService.hasAppointment(patient, appointmentDto.getStartTime(), appointmentDto.getEndTime())) {
            log.error("You already have an appointment in start time: -> {} and end time: -> {}", appointmentDto.getStartTime(), appointmentDto.getEndTime());
            throw new ReservationAppException(BadRequest.APPOINTMENT_ALREADY_EXISTS.getMessage());
        }
        val doctor = userService.findByIdAndRole(appointmentDto.getDoctor().getId(), Role.DOCTOR.name());
        if (!doctorService.isDoctorAvailable(doctor, appointmentDto.getStartTime(), appointmentDto.getEndTime())) {
            log.error("Doctor is not available in start time: -> {} and end time: -> {}", appointmentDto.getStartTime(), appointmentDto.getEndTime());
            throw new ReservationAppException(BadRequest.DOCTOR_NOT_AVAILABLE.getMessage());
        }
        appointmentDto.setDoctor(doctor);
        appointmentDto.setPatient(patient);
        appointmentDto.setStatus(Status.PENDING);
        val appointment = appointmentMapper.toEntity(appointmentDto);
        return appointmentMapper.toResponseDto(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentDto cancelAppointment(Long appointmentId) throws ReservationAppException {//DONE
        String loggedInUsername = userService.getUsernameFromContext();
        log.info("Appointment id to cancel is: -> {}, authenticated username is: -> {}", appointmentId, loggedInUsername);
        val appointment = getAppointmentById(appointmentId);
        val patientOfAppointment = appointment.getPatient();
        val doctorOfAppointment = appointment.getDoctor();

        if (isEligibleAppointmentToCancel(appointment)) {
            if (roleService.hasRole(Role.PATIENT.getRole())) {
                if (!patientOfAppointment.getUsername().equals(loggedInUsername))
                    throw new ReservationAppException(BadRequest.UNAUTHORIZED_OWNER.getMessage());
                appointment.setStatus(Status.CANCELED_BY_PATIENT);
            }
            if (roleService.hasRole(Role.DOCTOR.getRole())) {
                if (!doctorOfAppointment.getUsername().equals(loggedInUsername))
                    throw new ReservationAppException(BadRequest.UNAUTHORIZED_OWNER.getMessage());
                appointment.setStatus(Status.CANCELED_BY_DOCTOR);
            }
            if (roleService.hasRole(Role.SECRETARY.getRole()))
                appointment.setStatus(Status.CANCELED_BY_SECRETARY);
        }
        log.info("Appointment successfully canceled!");
        return appointmentMapper.toDto(appointmentRepository.save(appointment));
    }

    @Override
    public boolean isEligibleAppointmentToCancel(AppointmentEntity appointment) {
        val current = LocalDateTime.now();
        val next = appointment.getStartTime();
        val duration = Duration.between(current, next);
        val seconds = duration.getSeconds();
        val hoursDifference = seconds / 3600;
        if (appointmentStatusIsValidForCancel(appointment)) {
            if (hoursDifference < 24) {
                log.error("Too short time to cancel, appointment starts at: -> {}!", appointment.getStartTime());
                throw new ReservationAppException(BadRequest.SHORT_TIME_TO_CANCEL.getMessage());
            }
        }
        return true;
    }

    public boolean appointmentStatusIsValidForCancel(AppointmentEntity appointment) {
        if (appointment.getStatus() == Status.CANCELED_BY_DOCTOR ||
                appointment.getStatus() == Status.CANCELED_BY_PATIENT || appointment.getStatus() == Status.CANCELED_BY_SECRETARY ||
                appointment.getStatus() == Status.DONE) {
            log.info("Appointment can't be canceled as it is in status: -> {}", appointment.getStatus());
            throw new ReservationAppException(BadRequest.APPOINTMENT_CANCELED_OR_DONE.getMessage());
        }
        return true;
    }

    @Override
    public List<AppointmentDto> getPatientCanceledAppointments(Long patientId) {
        log.info("Searching canceled appointments for patientId -> {}", patientId);
        val patient = userService.findByIdAndRole(patientId, Role.PATIENT.name());
        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusCanceledAndPatient(userMapper.toEntity(patient)))) {
            log.error("No appointment found with status canceled for patientId: -> {}", patient);
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
        }
        return appointmentRepository.findByStatusCanceledAndPatient(userMapper.toEntity(patient))
                .stream().map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getDoctorCanceledAppointments(Long doctorId) throws AppointmentNotFoundException {
        log.info("Searching appointment with status canceled for doctorId -> {}", doctorId);
        val doctor = userService.findByIdAndRole(doctorId, Role.DOCTOR.name());
        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusCanceledAndDoctor(userMapper.toEntity(doctor)))) {
            log.error("No appointment found with status canceled for doctorId: -> {}", doctor);
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
        }
        return appointmentRepository.findByStatusCanceledAndDoctor(userMapper.toEntity(doctor))
                .stream().map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getPatientActiveAppointments(Long patientId) throws AppointmentNotFoundException {
        log.info("Searching active appointment for patientId -> {}", patientId);
        val patient = userService.findByIdAndRole(patientId, Role.PATIENT.name());
        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusAndPatient(Status.APPROVED, userMapper.toEntity(patient)))) {
            log.error("No active appointment found for patientId: -> {}", patient);
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
        }
        return appointmentRepository.findByStatusAndPatient(Status.APPROVED, userMapper.toEntity(patient))
                .stream().map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getPatientFinishedAppointments(Long patientId) throws AppointmentNotFoundException {
        log.info("Searching finished appointment for patientId -> {}", patientId);
        val patient = userService.findByIdAndRole(patientId, Role.PATIENT.name());
        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusAndPatient(Status.DONE, userMapper.toEntity(patient)))) {
            log.error("No active appointment found for patientId: -> {}", patient);
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
        }
        return appointmentRepository.findByStatusAndPatient(Status.DONE, userMapper.toEntity(patient))
                .stream().map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getDoctorFinishedAppointments(Long doctorId) throws AppointmentNotFoundException {
        log.info("Searching finished appointment for doctorId -> {}", doctorId);
        val doctor = userService.findByIdAndRole(doctorId, Role.DOCTOR.name());
        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusAndDoctor(Status.DONE, userMapper.toEntity(doctor)))) {
            log.error("No active appointment found for doctorId: -> {}", doctor);
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
        }
        return appointmentRepository.findByStatusAndDoctor(Status.DONE, userMapper.toEntity(doctor))
                .stream().map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentResponseDto approveOrRejectDoctorChange(AppointmentDto appointmentDto) throws ReservationAppException {
        log.info("Approve or reject appointment-> {} authenticated user: -> {}", appointmentDto, userService.getUsernameFromContext());
        val existingAppointment = getAppointmentById(appointmentDto.getId());
        if (!existingAppointment.getPatient().getUsername().equals(userService.getUsernameFromContext())) {
            log.error("Owner of this appointment is: -> {}", existingAppointment.getPatient().getUsername());
            throw new ReservationAppException(BadRequest.UNAUTHORIZED_OWNER.getMessage());
        }
        if (existingAppointment.getStatus() != Status.DOCTOR_CHANGE_REQUEST) {
            log.error("Appointment invalid status: -> {}", existingAppointment.getStatus());
            throw new ReservationAppException(BadRequest.INVALID_STATUS.getMessage());
        }
        appointmentMapper.updateAppointmentFromDto(appointmentDto, existingAppointment);
        return appointmentMapper.toResponseDto(appointmentRepository.save(existingAppointment));
    }

    @Override
    public List<AppointmentDto> getAllPendingAppointments() throws AppointmentNotFoundException {
        log.info("Searching for PENDING appointments:");
        val appointments = appointmentRepository.findByStatus(Status.PENDING)
                .stream().map(appointmentMapper::toDto)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(appointments)) {
            log.error("No pending appointment found");
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
        }
        return appointments;
    }

    @Override
    public List<AppointmentDto> getAllFinishedAppointments() throws AppointmentNotFoundException {
        log.info("Searching for DONE appointments:");
        val appointments = appointmentRepository.findByStatus(Status.DONE)
                .stream().map(appointmentMapper::toDto)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(appointments)) {
            log.error("No pending appointment found");
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
        }
        return appointments;
    }

    @Override
    public List<AppointmentDto> getAllCanceledAppointments() throws AppointmentNotFoundException {
        log.info("Searching for CANCELED appointments:");

        List<Status> canceledStatuses = Arrays.stream(Status.values())
                .filter(status -> status == Status.CANCELED_BY_DOCTOR ||
                        status == Status.CANCELED_BY_PATIENT ||
                        status == Status.CANCELED_BY_SECRETARY)
                .collect(Collectors.toList());

        val appointments = appointmentRepository.findByStatusIn(canceledStatuses)
                .stream().map(appointmentMapper::toDto)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(appointments)) {
            log.error("No CANCELED appointment found");
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
        }
        return appointments;
    }

    @Override
    public AppointmentResponseDto approveAppointment(Long appointmentId) {
        val appointment = getAppointmentById(appointmentId);
        if (appointment.getStatus() != Status.PENDING && appointment.getStatus() != Status.DOCTOR_CHANGE_APPROVED) {
            log.error("Invalid appointment status for approval: -> {}", appointment.getStatus());
            throw new ReservationAppException(BadRequest.INVALID_STATUS.getMessage());
        }
        appointment.setStatus(Status.APPROVED);
        return appointmentMapper.toResponseDto(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentDto updateAppointment(AppointmentDto newAppointment) {
        log.info("Updating appointment: -> {}", newAppointment);
        val appointment = getAppointmentById(newAppointment.getId());
        if (newAppointment.getStatus() != null && newAppointment.getStatus() == Status.DONE) {
            if (roleService.hasRole(Role.SECRETARY.getRole()) && !StringUtils.isEmpty(appointment.getFeedback())) {
                appointmentMapper.updateAppointmentFromDto(newAppointment, appointment);
            } else {
                log.error("Failed to update to DONE. Feedback is: -> {} and user role SECRETARY is: -> {}", appointment.getFeedback(), (roleService.hasRole(Role.SECRETARY.getRole())));
                throw new ReservationAppException(BadRequest.FEEDBACK_MISSING.getMessage());
            }
        }
        return appointmentMapper.toDto(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponseDto setAppointmentToDone(Long appointmentId) {
        val appointment = getAppointmentById(appointmentId);
        if (appointment.getStatus() != Status.APPROVED) {
            log.error("Invalid appointment status for setting the appointment to DONE: -> {}", appointment.getStatus());
            throw new ReservationAppException(BadRequest.INVALID_STATUS.getMessage());
        }
        if (!StringUtils.isEmpty(appointment.getFeedback()) && appointment.getFeedback() != null) {
            log.info("Appointment was set to DONE!");
            appointment.setStatus(Status.DONE);
            appointmentRepository.save(appointment);
            return appointmentMapper.toResponseDto(appointment);
        }
        log.error("Failed to update to DONE. Feedback is: -> {}", appointment.getFeedback());
        throw new ReservationAppException(BadRequest.FEEDBACK_MISSING.getMessage());
    }

    @Override
    public List<AppointmentDto> getDoctorActiveAppointments(Long doctorId) throws AppointmentNotFoundException {
        log.info("Searching finished appointment for doctorId -> {}", doctorId);
        val doctor = userService.findByIdAndRole(doctorId, Role.DOCTOR.name());
        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusAndDoctor(Status.APPROVED, userMapper.toEntity(doctor)))) {
            log.error("No active appointment found for doctor: -> {}", doctor);
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
        }
        return appointmentRepository.findByStatusAndDoctor(Status.APPROVED, userMapper.toEntity(doctor))
                .stream().map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getAppointmentByDate(LocalDate appointmentDate) {
        log.info("Searching for appointment in date: -> {}", appointmentDate);
        return appointmentRepository.findByAppointmentDate(appointmentDate)
                .stream().map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentDto changeDoctor(AppointmentDto newAppointmentDto) throws ReservationAppException {
        log.info("Changing doctor for appointment: -> {}", newAppointmentDto.getId());
        val appointment = getAppointmentById(newAppointmentDto.getId());
        if (appointment.getStatus() != Status.APPROVED && appointment.getStatus() != Status.PENDING) {
            log.error("Cannot change doctor for this appointment as it is in status: -> {}", appointment.getStatus());
            throw new ReservationAppException(BadRequest.INVALID_STATUS.getMessage());
        }
        val doctor = userService.findByIdAndRole(newAppointmentDto.getDoctor().getId(), Role.DOCTOR.name());
        if (!doctorService.isDoctorAvailable(doctor, appointment.getStartTime(), appointment.getEndTime())) {
            log.error("Doctor is not available in start time: -> {} and end: -> {}", appointment.getStartTime(), appointment.getEndTime());
            throw new ReservationAppException(BadRequest.DOCTOR_NOT_AVAILABLE.getMessage());
        }
        val newDoctor = userService.findByIdAndRole(newAppointmentDto.getDoctor().getId(), Role.DOCTOR.name());
        appointment.setDoctor(userMapper.toEntity(newDoctor));
        appointment.setStatus(Status.DOCTOR_CHANGE_REQUEST);
        return appointmentMapper.toDto(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponseDto updateAppointmentFeedback(AppointmentDto appointmentDto) {
        val appointment = getAppointmentById(appointmentDto.getId());
        val loggedInUsername = userService.getUsernameFromContext();
        val loggedInDoctor = userService.findByUsername(loggedInUsername);
        log.info("Authenticated username is: -> {}", loggedInUsername);

        if (!appointment.getDoctor().getUsername().equals(loggedInDoctor.getUsername())) {
            log.error("Owner of this appointment is: -> {}", appointment.getDoctor().getUsername());
            throw new ReservationAppException(BadRequest.UNAUTHORIZED_OWNER.getMessage());
        }

        if (appointment.getStatus() != Status.APPROVED) {
            log.error("Invalid appointment status for updating the feedback: -> {}", appointment.getStatus());
            throw new ReservationAppException(BadRequest.INVALID_STATUS.getMessage());
        }
        appointmentMapper.updateAppointmentFromDto(appointmentDto, appointment);
        return appointmentMapper.toResponseDto(appointmentRepository.save(appointment));
    }

    @Transactional
    //@Scheduled(cron = "0 0 0 * * *")
    @Override
    public String updateDefaultFeedback() {
        log.info("Inside updateDefaultFeedback...");
        AppointmentDto appointmentForUpdate = new AppointmentDto();
        appointmentForUpdate.setFeedback(AppointmentConstants.DEFAULT_FEEDBACK);
        LocalDate previousDate = LocalDate.now().minusDays(1);
        List<AppointmentEntity> previousAppointments = appointmentRepository.findByAppointmentDate(previousDate);
        previousAppointments.stream().filter(appointmentEntity ->
                StringUtils.isEmpty(appointmentEntity.getFeedback()) || appointmentEntity.getFeedback() == null)
                .forEach(appointmentEntity -> appointmentMapper.updateAppointmentFromDto(appointmentForUpdate, appointmentEntity));

        log.info("Successful update!");
        return AppointmentConstants.SUCCESS_UPDATE;
    }

    @Override
    public AppointmentEntity getAppointmentById(Long id) throws AppointmentNotFoundException {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage()));
    }

    @Override
    public List<AppointmentDto> getPatientAllAppointments(Long patientId) {
        val patientDto = userService.findByIdAndRole(patientId, Role.PATIENT.name());
        return appointmentRepository.findByPatient(userMapper.toEntity(patientDto))
                .stream().map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getDoctorAllAppointments(Long doctorId) {
        val doctorDto = userService.findByIdAndRole(doctorId, Role.DOCTOR.name());
        return appointmentRepository.findByDoctor(userMapper.toEntity(doctorDto))
                .stream().map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream().map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }
}