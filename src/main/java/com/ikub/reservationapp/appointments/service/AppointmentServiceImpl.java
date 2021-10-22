package com.ikub.reservationapp.appointments.service;

import com.ikub.reservationapp.appointments.constants.AppointmentConstants;
import com.ikub.reservationapp.appointments.dto.AppointmentDateTimeDto;
import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.appointments.dto.AppointmentResponseDto;
import com.ikub.reservationapp.appointments.dto.AppointmentSearchRequestDto;
import com.ikub.reservationapp.appointments.entity.AppointmentEntity;
import com.ikub.reservationapp.appointments.mapper.AppointmentMapper;
import com.ikub.reservationapp.appointments.specifications.AppointmentSpecification;
import com.ikub.reservationapp.appointments.validators.AppointmentValidator;
import com.ikub.reservationapp.appointments.utils.DateUtil;
import com.ikub.reservationapp.common.enums.Role;
import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.common.exception.BadRequest;
import com.ikub.reservationapp.common.exception.NotFound;
import com.ikub.reservationapp.doctors.service.DoctorService;
import com.ikub.reservationapp.patients.service.PatientService;
import com.ikub.reservationapp.users.dto.UserResponseDto;
import com.ikub.reservationapp.users.entity.UserEntity;
import com.ikub.reservationapp.users.exception.UserNotFoundException;
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
import org.springframework.data.domain.*;
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
    @Autowired
    private AppointmentSpecification appointmentSpecification;

    @Override
    public List<AppointmentDateTimeDto> getAllAvailableHours() {
        String loggedInUsername = userService.getUsernameFromContext();
        log.info("Authenticated username is: -> {}", loggedInUsername);
        List<AppointmentDateTimeDto> allAvailableAppointmentDatesAndHoursResponse = new ArrayList<>();
        val datesToIterate = DateUtil.datesFromNowToSpecificDay(AppointmentConstants.DAYS_TO_ITERATE);
        log.info("Dates to iterate: -> {}", datesToIterate);

        datesToIterate.forEach(nextDate -> {
            List<LocalDateTime> reservedHours = new ArrayList<>();
            List<LocalDateTime> availableHours = DateUtil.createAllAvailableHours(nextDate);//1-Create All available hours for nextDate
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
            AppointmentDateTimeDto availableAppointmentDto = new AppointmentDateTimeDto(nextDate, availableHours);
            allAvailableAppointmentDatesAndHoursResponse.add(availableAppointmentDto);
        });
        return allAvailableAppointmentDatesAndHoursResponse;
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
    public AppointmentResponseDto createAppointment(AppointmentDto appointmentDto) throws ReservationAppException {
        val loggedInUsername = userService.getUsernameFromContext();
        log.info("Creating appointment: -> {}, authenticated username is: -> {}", appointmentDto, loggedInUsername);
        AppointmentValidator.validateAppointment(appointmentDto);
        val patient = userService.findByIdAndRole(appointmentDto.getPatient().getId(), Role.PATIENT.name());

        if (patientService.hasAppointment(appointmentDto, patient)) {
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
    public AppointmentResponseDto cancelAppointment(AppointmentDto appointmentDto) throws ReservationAppException {
        String loggedInUsername = userService.getUsernameFromContext();
        log.info("Appointment to cancel is: -> {}, authenticated username is: -> {}", appointmentDto, loggedInUsername);
        val appointment = getAppointmentById(appointmentDto.getId());
        val patientOfAppointment = appointment.getPatient();
        val doctorOfAppointment = appointment.getDoctor();

        if (appointmentDto.getComments() == null || appointmentDto.getComments().trim().isEmpty()) {
            log.error("Comment is missing for appointment to cancel");
            throw new ReservationAppException(BadRequest.COMMENT_MISSING.getMessage());
        }

        if (isEligibleAppointmentToCancel(appointment)) {
            if (roleService.hasRole(Role.PATIENT.getRole())) {
                if (!patientOfAppointment.getUsername().equals(loggedInUsername))
                    throw new ReservationAppException(BadRequest.UNAUTHORIZED_OWNER.getMessage());
                appointment.setStatus(Status.CANCELED_BY_PATIENT);
                appointment.setComments(appointmentDto.getComments());
            }
            if (roleService.hasRole(Role.DOCTOR.getRole())) {
                if (!doctorOfAppointment.getUsername().equals(loggedInUsername))
                    throw new ReservationAppException(BadRequest.UNAUTHORIZED_OWNER.getMessage());
                appointment.setStatus(Status.CANCELED_BY_DOCTOR);
                appointment.setComments(appointmentDto.getComments());
            }
            if (roleService.hasRole(Role.SECRETARY.getRole())) {
                appointment.setStatus(Status.CANCELED_BY_SECRETARY);
                appointment.setComments(appointmentDto.getComments());
            }
        }
        log.info("Appointment successfully canceled!");
        return appointmentMapper.toResponseDto(appointmentRepository.save(appointment));
    }

    @Override
    public boolean isEligibleAppointmentToCancel(AppointmentEntity appointment) {
        val currentTime = LocalDateTime.now();
        val appointmentStartTime = appointment.getStartTime();
        val durationBetweenCurrentTimeAndAppointmentStartTime = Duration.between(currentTime, appointmentStartTime);
        val durationInSeconds = durationBetweenCurrentTimeAndAppointmentStartTime.getSeconds();
        val hoursDifference = durationInSeconds / 3600;
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
    public List<AppointmentResponseDto> getPatientCanceledAppointments() {
        log.info("Searching canceled appointments for patientId -> {}", userService.getUsernameFromContext());
        val patient = userService.findByUsername(userService.getUsernameFromContext());
        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusCanceledAndPatient(userMapper.toEntity(patient)))) {
            log.error("No appointment found with status canceled for patientId: -> {}", patient);
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
        }
        return appointmentRepository.findByStatusCanceledAndPatient(userMapper.toEntity(patient))
                .stream().map(appointmentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDto> getDoctorCanceledAppointments() throws AppointmentNotFoundException {
        log.info("Searching appointment with status canceled for doctorId -> {}", userService.getUsernameFromContext());
        val doctor = userService.findByUsername(userService.getUsernameFromContext());
        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusCanceledAndDoctor(userMapper.toEntity(doctor)))) {
            log.error("No appointment found with status canceled for doctorId: -> {}", doctor);
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
        }
        return appointmentRepository.findByStatusCanceledAndDoctor(userMapper.toEntity(doctor))
                .stream().map(appointmentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDto> getPatientActiveAppointments() throws AppointmentNotFoundException {
        log.info("Searching active appointment for patientId -> {}", userService.getUsernameFromContext());
        val patient = userService.findByUsername(userService.getUsernameFromContext());
        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusAndPatient(Status.APPROVED, userMapper.toEntity(patient)))) {
            log.error("No active appointment found for patientId: -> {}", patient);
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
        }
        return appointmentRepository.findByStatusAndPatient(Status.APPROVED, userMapper.toEntity(patient))
                .stream().map(appointmentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDto> getPatientFinishedAppointments() throws AppointmentNotFoundException {
        log.info("Searching finished appointment for patient -> {}", userService.getUsernameFromContext());
        val patient = userService.findByUsername(userService.getUsernameFromContext());
        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusAndPatient(Status.DONE, userMapper.toEntity(patient)))) {
            log.error("No active appointment found for patientId: -> {}", patient);
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
        }
        return appointmentRepository.findByStatusAndPatient(Status.DONE, userMapper.toEntity(patient))
                .stream().map(appointmentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDto> getPatientAppointmentsInSpecificDay(String date) throws AppointmentNotFoundException, ReservationAppException {
        log.info("Searching finished appointment for patient -> {} in date -> {}", userService.getUsernameFromContext(), date);

        val appointmentDateToSearch = DateUtil.validateDateFormat(date);

        val patient = userService.findByUsername(userService.getUsernameFromContext());
        val appointments = appointmentRepository.findByAppointmentDateAndPatient(appointmentDateToSearch, userMapper.toEntity(patient));
        if (CollectionUtils.isEmpty(appointments)) {
            log.error("No appointment Found!");
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
        }
        return appointments.stream().map(appointmentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDto> getDoctorAppointmentsInSpecificDay(String date) throws AppointmentNotFoundException, ReservationAppException {
        log.info("Searching finished appointment for doctor -> {} in date -> {}", userService.getUsernameFromContext(), date);

        val appointmentDateToSearch = DateUtil.validateDateFormat(date);

        val doctor = userService.findByUsername(userService.getUsernameFromContext());
        val appointments = appointmentRepository.findByAppointmentDateAndDoctor(appointmentDateToSearch, userMapper.toEntity(doctor));
        if (CollectionUtils.isEmpty(appointments)) {
            log.error("No appointment Found!");
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
        }
        return appointments.stream().map(appointmentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDto> getDoctorFinishedAppointments() throws AppointmentNotFoundException {
        log.info("Searching finished appointment for doctorId -> {}", userService.getUsernameFromContext());
        val doctor = userService.findByUsername(userService.getUsernameFromContext());
        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusAndDoctor(Status.DONE, userMapper.toEntity(doctor)))) {
            log.error("No active appointment found for doctorId: -> {}", doctor);
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
        }
        return appointmentRepository.findByStatusAndDoctor(Status.DONE, userMapper.toEntity(doctor))
                .stream().map(appointmentMapper::toResponseDto)
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
    public List<AppointmentResponseDto> getAllPendingAppointments() throws AppointmentNotFoundException {
        log.info("Searching for PENDING appointments:");
        val appointments = appointmentRepository.findByStatus(Status.PENDING)
                .stream().map(appointmentMapper::toResponseDto)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(appointments)) {
            log.error("No pending appointment found");
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
        }
        return appointments;
    }

    @Override
    public List<AppointmentResponseDto> getAllFinishedAppointments(AppointmentSearchRequestDto requestDto) throws AppointmentNotFoundException {
        log.info("Searching for DONE appointments:");
        Optional<AppointmentSearchRequestDto> requestExists = Optional.ofNullable(requestDto);
        if (!requestExists.isPresent())
            return appointmentRepository.findAll().stream().map(appointmentMapper::toResponseDto)
                    .collect(Collectors.toList());
        if (requestDto.getPageNumber() == null)
            requestDto.setPageNumber(AppointmentConstants.DEFAULT_PAGE_NUMBER);
        if (requestDto.getPageSize() == null)
            requestDto.setPageSize(AppointmentConstants.DEFAULT_PAGE_SIZE);

        PageRequest page = PageRequest.of(requestDto.getPageNumber() - 1, requestDto.getPageSize());
        val appointments = appointmentRepository.findByStatus(Status.DONE, page);

        if (CollectionUtils.isEmpty(appointments.getContent())) {
            log.error("No DONE appointment found");
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
        }
        return appointments.getContent().stream().map(appointmentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDto> getAllCanceledAppointments() throws AppointmentNotFoundException {
        log.info("Searching for CANCELED appointments:");

        List<Status> canceledStatuses = Arrays.stream(Status.values())
                .filter(status -> status == Status.CANCELED_BY_DOCTOR ||
                        status == Status.CANCELED_BY_PATIENT ||
                        status == Status.CANCELED_BY_SECRETARY)
                .collect(Collectors.toList());

        val appointments = appointmentRepository.findByStatusIn(canceledStatuses)
                .stream().map(appointmentMapper::toResponseDto)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(appointments)) {
            log.error("No CANCELED appointment found");
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
        }
        return appointments;
    }

    @Override
    public List<AppointmentResponseDto> getAllAppointmentsInSpecificDay(String date) throws AppointmentNotFoundException {
        log.info("Retrieving all appointments in date -> {}", date);

        LocalDate dateToSearch = DateUtil.validateDateFormat(date);
        val appointments = appointmentRepository.findByAppointmentDate(dateToSearch);
        if (CollectionUtils.isEmpty(appointments)) {
            log.error("No appointment found");
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
        }
        return appointments.stream().map(appointmentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDto> getAllAppointmentsInSpecificDay(AppointmentSearchRequestDto searchRequestDto) throws AppointmentNotFoundException {
        log.info("Retrieving appointments in specific day with search fields: -> {}", searchRequestDto);
        AppointmentValidator.validateAppointmentSearchDto(searchRequestDto);

        Pageable pageRequest = PageRequest.of(searchRequestDto.getPageNumber() - 1, searchRequestDto.getPageSize(),
                Sort.by("startTime").ascending());
        val pagesResponse = appointmentRepository.findAll(
                appointmentSpecification.getAppointments(searchRequestDto), pageRequest);

        if (pagesResponse != null && pagesResponse.getContent() != null) {
            val appointmentsMatched = pagesResponse.getContent();
            if (appointmentsMatched != null && appointmentsMatched.size() > 0) {
                List<AppointmentResponseDto> responseDtos = appointmentsMatched.stream().map(appointmentMapper::toResponseDto)
                        .collect(Collectors.toList());
                return responseDtos;
            }
        }
        throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
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
    public AppointmentResponseDto updateAppointment(AppointmentDto newAppointment) {
        log.info("Updating appointment: -> {}", newAppointment);
        val appointment = getAppointmentById(newAppointment.getId());
        if (newAppointment.getStatus() != null) {
            throw new ReservationAppException("status cannot be updated");
        }
        appointmentMapper.updateAppointmentFromDto(newAppointment, appointment);
        return appointmentMapper.toResponseDto(appointmentRepository.save(appointment));
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
    public List<AppointmentResponseDto> getDoctorActiveAppointments() throws AppointmentNotFoundException {
        log.info("Searching finished appointment for doctorId -> {}", userService.getUsernameFromContext());
        val doctor = userService.findByUsername(userService.getUsernameFromContext());
        if (CollectionUtils.isEmpty(appointmentRepository.findByStatusAndDoctor(Status.APPROVED, userMapper.toEntity(doctor)))) {
            log.error("No active appointment found for doctor: -> {}", doctor);
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());
        }
        return appointmentRepository.findByStatusAndDoctor(Status.APPROVED, userMapper.toEntity(doctor))
                .stream().map(appointmentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getAppointmentByDate(LocalDate appointmentDate) {
        log.info("Searching for appointment in date: -> {}", appointmentDate);
        return appointmentRepository.findByAppointmentDateAndNotCanceled(appointmentDate)
                .stream().map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentResponseDto changeDoctor(AppointmentDto newAppointmentDto) throws ReservationAppException {
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
        return appointmentMapper.toResponseDto(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponseDto updateAppointmentFeedback(AppointmentDto appointmentDto) {
        val existingAppointment = getAppointmentById(appointmentDto.getId());
        val loggedInUsername = userService.getUsernameFromContext();
        val loggedInDoctor = userService.findByUsername(loggedInUsername);
        log.info("Authenticated username is: -> {}", loggedInUsername);
        if (!existingAppointment.getDoctor().getUsername().equals(loggedInDoctor.getUsername())) {
            log.error("Owner of this appointment is: -> {}", existingAppointment.getDoctor().getUsername());
            throw new ReservationAppException(BadRequest.UNAUTHORIZED_OWNER.getMessage());
        }
        AppointmentValidator.validateAppointmentFeedback(existingAppointment, appointmentDto);

        appointmentMapper.updateAppointmentFromDto(appointmentDto, existingAppointment);
        return appointmentMapper.toResponseDto(appointmentRepository.save(existingAppointment));
    }

    @Transactional
    //@Scheduled(cron = "0 0 0 * * *")
    @Override
    public String updateDefaultFeedback() {
        log.info("Inside updateDefaultFeedback...");
        AppointmentDto appointmentForUpdate = new AppointmentDto();
        appointmentForUpdate.setFeedback(AppointmentConstants.DEFAULT_FEEDBACK);
        LocalDate previousDate = LocalDate.now().minusDays(1);
        List<AppointmentEntity> previousAppointments = appointmentRepository.findByAppointmentDateAndNotCanceled(previousDate);
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
    public List<AppointmentResponseDto> getPatientAllAppointments() {
        val patientDto = userService.findByUsername(userService.getUsernameFromContext());
        return appointmentRepository.findByPatient(userMapper.toEntity(patientDto))
                .stream().map(appointmentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDto> getDoctorAllAppointments() {
        val doctorDto = userService.findByUsername(userService.getUsernameFromContext());
        return appointmentRepository.findByDoctor(userMapper.toEntity(doctorDto))
                .stream().map(appointmentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDto> getAllAppointments(Integer pageNumber, Integer size) {
        if (size == null || pageNumber == null)
            throw new ReservationAppException(BadRequest.APPOINTMENT_SEARCH_FIELDS_MISSING.getMessage());
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, size,
                Sort.by("appointmentDate").descending());
        return appointmentRepository.findAll(pageRequest).getContent()
                .stream().map(appointmentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDto> getAllAppointmentsWithStatusAndPagination(AppointmentSearchRequestDto searchRequestDto) {
        log.info("Search appointment with criteria -> {}", searchRequestDto);
        AppointmentValidator.validateAppointmentSearchDto(searchRequestDto);

        PageRequest page = PageRequest.of(searchRequestDto.getPageNumber() - 1,
                searchRequestDto.getPageSize(), Sort.by("appointmentDate").descending());
        val appointmentsPage = appointmentRepository.findByStatus(searchRequestDto.getStatus(), page);

        if (CollectionUtils.isEmpty(appointmentsPage.getContent()))
            throw new AppointmentNotFoundException(NotFound.APPOINTMENT.getMessage());

        return appointmentsPage.getContent().stream().map(appointmentMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}