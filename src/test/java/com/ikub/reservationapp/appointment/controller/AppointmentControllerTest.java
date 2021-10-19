package com.ikub.reservationapp.appointment.controller;

import com.ikub.reservationapp.ReservationAppTestSupport;
import com.ikub.reservationapp.appointments.dto.AppointmentDateTimeDto;
import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.appointments.dto.AppointmentResponseDto;
import com.ikub.reservationapp.appointments.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import com.ikub.reservationapp.common.model.AuthToken;
import com.ikub.reservationapp.common.model.ExceptionMessage;
import com.ikub.reservationapp.common.model.LoginUser;
import com.ikub.reservationapp.common.utils.JsonUtils;
import com.ikub.reservationapp.users.dto.UserDto;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.Local;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import springfox.documentation.spring.web.json.Json;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.MatcherAssert.assertThat;

//@SpringBootTest(classes = ReservationAppApplication.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AppointmentControllerTest extends ReservationAppTestSupport {

    private String URL = "/appointments";
    private static String TOKEN = "";
    private static String REFRESH_TOKEN = "";

    @BeforeAll
    static void setup(@Autowired MockMvc mockMvc) {

        val loginDoctor = LoginUser.builder()
                .username("doctor2").password("Abc12345?").build();

        val loginPatient = LoginUser.builder().username("patient1")
                .password("Abc12345?").build();

        val secretaryLogin = LoginUser.builder().username("secretary")
                .password("Abc12345?").build();

        try {
            val result = mockMvc
                    .perform(
                            MockMvcRequestBuilders.post("/users/authenticate")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonUtils.toJsonString(loginPatient)))
                    .andExpect(status().isOk())
                    .andReturn();
            TOKEN = JsonUtils.toObject(result.getResponse().getContentAsString(), AuthToken.class).getAccessToken();
            REFRESH_TOKEN = JsonUtils.toObject(result.getResponse().getContentAsString(), AuthToken.class).getRefreshToken();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
            TOKEN = "";
        }
    }

    /**
     * Result: 200 OK available hours are returned correctly
     */
    @Test
    void getAllAvailableHours() {
        try {

            AppointmentDto reservedAppointment = new AppointmentDto();
            reservedAppointment.setAppointmentDate(LocalDate.of(2021, 10, 20));
            reservedAppointment.setStartTime(LocalDateTime.of(2021, 10, 20, 14, 0, 0));
            reservedAppointment.setEndTime(LocalDateTime.of(2021, 10, 20, 15, 0, 0));

            val result = mockMvc
                    .perform(
                            MockMvcRequestBuilders.get(URL + "/available")
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            List<AppointmentDateTimeDto> listOfObjects = JsonUtils.toList(result, AppointmentDateTimeDto.class);

            List<LocalDateTime> timesAvailable =
                    listOfObjects.stream().filter(appointmentDateTimeDto -> appointmentDateTimeDto.getAppointmentDate().equals(reservedAppointment.getAppointmentDate())).findFirst()
                            .get().getAvailableHours().stream().filter(availableTime -> availableTime.getHour() != reservedAppointment.getStartTime().getHour())
                            .collect(Collectors.toList());

            assertThat(listOfObjects, hasSize(7));
            assertThat(timesAvailable, hasSize(8));

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    /**
     * When appointment is created successfully
     * Result: 200 OK
     */
    @Test
    @Disabled
    void createAppointmentSuccess() {
        AppointmentDto appointmentDto = new AppointmentDto();
        UserDto doctor = new UserDto();
        doctor.setId(5l);
        UserDto patient = new UserDto();
        patient.setId(3l);
        LocalDate appointmentDate = LocalDate.of(2021, 10, 21);
        LocalDateTime startTime = LocalDateTime.of(2021, 10, 21, 14, 00, 0);
        LocalDateTime endTime = LocalDateTime.of(2021, 10, 21, 15, 00, 0);
        appointmentDto.setAppointmentDate(appointmentDate);
        appointmentDto.setStartTime(startTime);
        appointmentDto.setEndTime(endTime);
        appointmentDto.setComments("Dentist appointment");
        appointmentDto.setDescription("Dentist appointment");
        appointmentDto.setDoctor(doctor);
        appointmentDto.setPatient(patient);
        val response = createPost(URL, appointmentDto, AppointmentResponseDto.class, TOKEN);
        assertEquals(Status.PENDING, response.getStatus());
    }

    /**
     * When user already has an appointment in this time
     * Result: 400 "You already have an appointment in this time!"
     * @throws ReservationAppException
     */
    @Test
    void createAppointmentFailUserAlreadyHasAppointment() throws ReservationAppException {
        AppointmentDto appointmentDto = new AppointmentDto();
        UserDto doctor = new UserDto();
        doctor.setId(5l);
        UserDto patient = new UserDto();
        patient.setId(3l);
        LocalDate appointmentDate = LocalDate.of(2021, 10, 20);
        LocalDateTime startTime = LocalDateTime.of(2021, 10, 20, 14, 00, 0);
        LocalDateTime endTime = LocalDateTime.of(2021, 10, 20, 15, 00, 0);
        appointmentDto.setAppointmentDate(appointmentDate);
        appointmentDto.setStartTime(startTime);
        appointmentDto.setEndTime(endTime);
        appointmentDto.setComments("Dentist appointment");
        appointmentDto.setDescription("Dentist appointment");
        appointmentDto.setDoctor(doctor);
        appointmentDto.setPatient(patient);

        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.post(URL)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonUtils.toJsonString(appointmentDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("You already have an appointment in this time!", result.getResolvedException().getMessage()))
                    .andReturn();
        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    /**
     * When doctor is not available in this time
     * Result: 400 "Doctor is not available in this time!"
     * @throws ReservationAppException
     */
    @Test
    void createAppointmentFailDoctorNotAvailable() throws ReservationAppException {
        AppointmentDto appointmentDto = new AppointmentDto();
        UserDto doctor = new UserDto();
        doctor.setId(5l);
        UserDto patient = new UserDto();
        patient.setId(7l);
        LocalDate appointmentDate = LocalDate.of(2021, 10, 20);
        LocalDateTime startTime = LocalDateTime.of(2021, 10, 20, 14, 00, 0);
        LocalDateTime endTime = LocalDateTime.of(2021, 10, 20, 15, 00, 0);
        appointmentDto.setAppointmentDate(appointmentDate);
        appointmentDto.setStartTime(startTime);
        appointmentDto.setEndTime(endTime);
        appointmentDto.setComments("Dentist appointment");
        appointmentDto.setDescription("Dentist appointment");
        appointmentDto.setDoctor(doctor);
        appointmentDto.setPatient(patient);

        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.post(URL)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonUtils.toJsonString(appointmentDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("Doctor is not available in this time!", result.getResolvedException().getMessage()))
                    .andReturn();
        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    /**
     * When appoitnment is after end time 17
     * Result: 400 "Appointment time is out of business hours!"
     * @throws ReservationAppException
     */
    @Test
    void createAppointmentFailOutOfBusinessHoursAfter() throws ReservationAppException {
        AppointmentDto appointmentDto = new AppointmentDto();
        UserDto doctor = new UserDto();
        doctor.setId(5l);
        UserDto patient = new UserDto();
        patient.setId(3l);
        LocalDate appointmentDate = LocalDate.of(2021, 10, 20);
        LocalDateTime startTime = LocalDateTime.of(2021, 10, 20, 17, 00, 0);//hour 17
        LocalDateTime endTime = LocalDateTime.of(2021, 10, 20, 18, 00, 0);
        appointmentDto.setAppointmentDate(appointmentDate);
        appointmentDto.setStartTime(startTime);
        appointmentDto.setEndTime(endTime);
        appointmentDto.setComments("Dentist appointment");
        appointmentDto.setDescription("Dentist appointment");
        appointmentDto.setDoctor(doctor);
        appointmentDto.setPatient(patient);

        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.post(URL)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonUtils.toJsonString(appointmentDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("Appointment time is out of business hours!", result.getResolvedException().getMessage()))
                    .andReturn();
        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    /**
     * When appointnment is before start time 8
     * Result: 400 "Appointment time is out of business hours!"
     * @throws ReservationAppException
     */
    @Test
    void createAppointmentFailOutOfBusinessHoursBefore() throws ReservationAppException {
        AppointmentDto appointmentDto = new AppointmentDto();
        UserDto doctor = new UserDto();
        doctor.setId(5l);
        UserDto patient = new UserDto();
        patient.setId(3l);
        LocalDate appointmentDate = LocalDate.of(2021, 10, 20);
        LocalDateTime startTime = LocalDateTime.of(2021, 10, 20, 7, 00, 0);//hour 7
        LocalDateTime endTime = LocalDateTime.of(2021, 10, 20, 18, 00, 0);
        appointmentDto.setAppointmentDate(appointmentDate);
        appointmentDto.setStartTime(startTime);
        appointmentDto.setEndTime(endTime);
        appointmentDto.setComments("Dentist appointment");
        appointmentDto.setDescription("Dentist appointment");
        appointmentDto.setDoctor(doctor);
        appointmentDto.setPatient(patient);

        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.post(URL)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonUtils.toJsonString(appointmentDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("Appointment time is out of business hours!", result.getResolvedException().getMessage()))
                    .andReturn();
        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    /**
     * When appoitnment is in WEEKEND
     * Result: 400 "Appointment time is out of business hours!"
     * @throws ReservationAppException
     */
    @Test
    void createAppointmentFailOutOfBusinessHoursWeekend() throws ReservationAppException {
        AppointmentDto appointmentDto = new AppointmentDto();
        UserDto doctor = new UserDto();
        doctor.setId(5l);
        UserDto patient = new UserDto();
        patient.setId(3l);
        LocalDate appointmentDate = LocalDate.of(2021, 10, 23); // Weekend day
        LocalDateTime startTime = LocalDateTime.of(2021, 10, 23, 8, 00, 0);
        LocalDateTime endTime = LocalDateTime.of(2021, 10, 23, 10, 00, 0);
        appointmentDto.setAppointmentDate(appointmentDate);
        appointmentDto.setStartTime(startTime);
        appointmentDto.setEndTime(endTime);
        appointmentDto.setComments("Dentist appointment");
        appointmentDto.setDescription("Dentist appointment");
        appointmentDto.setDoctor(doctor);
        appointmentDto.setPatient(patient);

        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.post(URL)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonUtils.toJsonString(appointmentDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("Appointment time is out of business hours!", result.getResolvedException().getMessage()))
                    .andReturn();
        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    /**
     * When appointment date is wrong
     * Result: 400 "The date selected is not valid. Please reserve a coming date!"
     * @throws ReservationAppException
     */
    @Test
    void createAppointmentFailDateWrong() throws ReservationAppException {
        AppointmentDto appointmentDto = new AppointmentDto();
        UserDto doctor = new UserDto();
        doctor.setId(5l);
        UserDto patient = new UserDto();
        patient.setId(3l);
        LocalDate appointmentDate = LocalDate.of(2021, 10, 9);
        LocalDateTime startTime = LocalDateTime.of(2021, 10, 9, 10, 00, 0);
        LocalDateTime endTime = LocalDateTime.of(2021, 10, 9, 12, 00, 0);
        appointmentDto.setAppointmentDate(appointmentDate);
        appointmentDto.setStartTime(startTime);
        appointmentDto.setEndTime(endTime);
        appointmentDto.setComments("Dentist appointment");
        appointmentDto.setDescription("Dentist appointment");
        appointmentDto.setDoctor(doctor);
        appointmentDto.setPatient(patient);

        try {
           val responseResult = mockMvc
                    .perform(
                            MockMvcRequestBuilders.post(URL)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonUtils.toJsonString(appointmentDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                    .andReturn();

            ExceptionMessage message = JsonUtils.toObject(responseResult.getResponse().getContentAsString(), ExceptionMessage.class);
            assertEquals(message.getMessage(), "Validation Failed");
            assertEquals(message.getDetails().get(0), "The date selected is not valid. Please reserve a coming date!");
        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    /**
     * Result: list of appointments has the correct size.
     */
    @Test
    void getAllAppointments(){

        val result = createGetAsString(URL, String.class, TOKEN);
        val resultList = JsonUtils.toList(result, AppointmentResponseDto.class);
        assertThat(resultList, hasSize(3));

    }


    /**
     * When appointment time is too short to cancel
     * Result: 400 "Too short time to cancel Appointment!"
     * @throws ReservationAppException
     */
    @Test
    void cancelAppointmentFailShortTime() throws ReservationAppException {
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/cancel/" + 38)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("Too short time to cancel Appointment!", result.getResolvedException().getMessage()))
                    .andReturn();
        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    /**
     * When appointment is in status DONE
     * Result: 400 "Appointment is already canceled or DONE!"
     * @throws ReservationAppException
     */
    @Test
    void cancelAppointmentWhenStatusDONE() throws ReservationAppException {
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/cancel/" + 38)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("Appointment is already canceled or DONE!", result.getResolvedException().getMessage()))
                    .andReturn();
        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    /**
     * When appointment is in status CANCELED_BY_PATIENT
     * Result: 400 "Appointment is already canceled or DONE!"
     * @throws ReservationAppException
     */
    @Test
    void cancelAppointmentWhenStatusCanceledByPatient() throws ReservationAppException {
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/cancel/" + 38)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("Appointment is already canceled or DONE!", result.getResolvedException().getMessage()))
                    .andReturn();
        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    /**
     * When appointment is in status CANCELED_BY_DOCTOR
     * Result: 400 "Appointment is already canceled or DONE!"
     * @throws ReservationAppException
     */
    @Test
    void cancelAppointmentWhenStatusCanceledByDoctor() throws ReservationAppException {
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/cancel/" + 38)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("Appointment is already canceled or DONE!", result.getResolvedException().getMessage()))
                    .andReturn();
        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    /**
     * When appointment is in status CANCELED_BY_SECRETARY
     * Result: 400 "Appointment is already canceled or DONE!"
     * @throws ReservationAppException
     */
    @Test
    void cancelAppointmentWhenStatusCanceledBySecretary() throws ReservationAppException {
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/cancel/" + 38)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("Appointment is already canceled or DONE!", result.getResolvedException().getMessage()))
                    .andReturn();
        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    /**
     * When appointment is in status Pending
     * Result: 200
     * @throws ReservationAppException
     */
    @Test
    void cancelAppointmentWhenStatusPending() throws ReservationAppException {
        try {
            //Test when secretary is logged in. Expect status in DB to be 0 -> Pending
            String response = mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/cancel/" + 39)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            AppointmentResponseDto appointmentResponseDto = JsonUtils.toObject(response, AppointmentResponseDto.class);

            assertEquals(Status.CANCELED_BY_SECRETARY, appointmentResponseDto.getStatus());

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    /**
     * When appointment is in status Approved
     * Result: 200 CANCELED_BY_SECRETARY
     * @throws ReservationAppException
     */
    @Test
    void cancelAppointmentWhenStatusApproved() throws ReservationAppException {
        try {
            //Test when secretary is logged. Expect status in DB to be 1 -> Approved
            String response = mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/cancel/" + 39)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            AppointmentResponseDto appointmentResponseDto = JsonUtils.toObject(response, AppointmentResponseDto.class);

            assertEquals(Status.CANCELED_BY_SECRETARY, appointmentResponseDto.getStatus());

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    /**
     * When appointment is in status Pending
     * Result: 200 CANCELED_BY_PATIENT
     * @throws ReservationAppException
     */
    @Test
    void cancelAppointmentWhenStatusPendingAndPatientLoggedIn() throws ReservationAppException {
        try {
            //Test when patient is logged. Expect status in DB to be 0 -> Pending
            String response = mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/cancel/" + 39)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            AppointmentResponseDto appointmentResponseDto = JsonUtils.toObject(response, AppointmentResponseDto.class);

            assertEquals(Status.CANCELED_BY_PATIENT, appointmentResponseDto.getStatus());

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    /**
     * When appointment is in status Pending
     * Result: 200 CANCELED_BY_DOCTOR
     * @throws ReservationAppException
     */
    @Test
    void cancelAppointmentWhenStatusPendingAndDoctorLoggedIn() throws ReservationAppException {
        try {
            //Test when doctor is logged. Expect status in DB to be 0 -> Pending
            String response = mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/cancel/" + 39)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            AppointmentResponseDto appointmentResponseDto = JsonUtils.toObject(response, AppointmentResponseDto.class);

            assertEquals(Status.CANCELED_BY_DOCTOR, appointmentResponseDto.getStatus());

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    /**
     * When appointment is in status Pending and does not belong to the logged in doctor
     * Result: 400 You are not the owner of the appointment
     * @throws ReservationAppException
     */
    @Test
    void cancelAppointmentWhenAppointmentDoesNotBelongToDoctor() throws ReservationAppException {
        try {
            //Test when doctor is logged. Expect status in DB to be 0 -> Pending
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/cancel/" + 39)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("You are not the owner of the appointment", result.getResolvedException().getMessage()))
                    .andReturn().getResponse().getContentAsString();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    /**
     * When no patient has active appointments
     * Result: 200
     * @throws ReservationAppException
     */
    @Test
    void getPatientActiveAppointmentsOK() throws ReservationAppException {
        Long patientId = 3l;
        try {
            String response = mockMvc
                    .perform(
                            MockMvcRequestBuilders.get(URL + "/patient/active/" + patientId)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<AppointmentResponseDto> responseDtos = JsonUtils.toList(response, AppointmentResponseDto.class);
            assertThat(responseDtos,hasSize(1));

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);

        }
    }

    /**
     * When patient has no active appointment
     * Result: 404 "No Appointment Found"
     * @throws ReservationAppException
     */
    @Test
    void getPatientActiveAppointmentsNoAppointmentFound() throws ReservationAppException {
        Long patientId = 7l;
        try {
            String response = mockMvc
                    .perform(
                            MockMvcRequestBuilders.get(URL + "/patient/active/" + patientId)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof AppointmentNotFoundException))
                    .andExpect(result -> assertEquals("No Appointment found!", result.getResolvedException().getMessage()))
                    .andReturn().getResponse().getContentAsString();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);

        }
    }

    /**
     * When patient has no CANCELED appointments
     * Result: 404 "No Appointment Found"
     * @throws ReservationAppException
     */
    @Test
    void getPatientCanceledAppointmentsNoAppointmentFound() throws ReservationAppException {
        Long patientId = 3l;
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.get(URL + "/patient/canceled/" + patientId)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof AppointmentNotFoundException))
                    .andExpect(result -> assertEquals("No Appointment found!", result.getResolvedException().getMessage()))
                    .andReturn().getResponse().getContentAsString();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);

        }
    }

    /**
     * When patient has CANCELED appointments
     * Result: 200
     * @throws ReservationAppException
     */
    @Test
    void getPatientCanceledAppointmentsOK() throws ReservationAppException {
        Long patientId = 3l;
        try {
            String response = mockMvc
                    .perform(
                            MockMvcRequestBuilders.get(URL + "/patient/canceled/" + patientId)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<AppointmentResponseDto> responseDtos = JsonUtils.toList(response, AppointmentResponseDto.class);
            assertThat(responseDtos, hasSize(1));

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);

        }
    }

    /**
     * When patient has FINISHED appointments
     * Result: 200
     * @throws ReservationAppException
     */
    @Test
    void getPatientFinishedAppointmentsOK() throws ReservationAppException {
        Long patientId = 3l;
        try {
            val response = mockMvc
                    .perform(
                            MockMvcRequestBuilders.get(URL + "/patient/done/" + patientId)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<AppointmentResponseDto> reponseDtos = JsonUtils.toList(response, AppointmentResponseDto.class);

            assertThat(reponseDtos, hasSize(2));

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);

        }
    }

    @Test
    void getPatientFinishedAppointmentsNoAppointmentFound() throws ReservationAppException {
        Long patientId = 3l;
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.get(URL + "/patient/done/" + patientId)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof AppointmentNotFoundException))
                    .andExpect(result -> assertEquals("No Appointment found!", result.getResolvedException().getMessage()))
                    .andReturn().getResponse().getContentAsString();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);

        }
    }

    @Test
    void getPatientAllAppointmentsOK() {
        Long patientId = 3l;
        try {
            String response = mockMvc
                    .perform(
                            MockMvcRequestBuilders.get(URL + "/patient/all/" + patientId)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<AppointmentResponseDto> responseDtos = JsonUtils.toList(response, AppointmentResponseDto.class);
            assertThat(responseDtos, hasSize(3));

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Test
    void getAllPendingAppointmentsOK(){
        Long patientId = 3l;
        try {
            String response = mockMvc
                    .perform(
                            MockMvcRequestBuilders.get(URL + "/status/pending")
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<AppointmentResponseDto> responseDtos = JsonUtils.toList(response, AppointmentResponseDto.class);

            long statusCounter = responseDtos.stream().filter(appointmentResponseDto -> appointmentResponseDto.getStatus().equals(Status.PENDING)).count();

            assertThat(responseDtos, hasSize(3));
            assertEquals(3, statusCounter);

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Test
    void getAllFinishedAppointmentsOK(){
        try {
            String response = mockMvc
                    .perform(
                            MockMvcRequestBuilders.get(URL + "/status/done")
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<AppointmentResponseDto> responseDtos = JsonUtils.toList(response, AppointmentResponseDto.class);

            long statusCounter = responseDtos.stream().filter(appointmentResponseDto -> appointmentResponseDto.getStatus().equals(Status.DONE)).count();

            assertThat(responseDtos, hasSize(1));
            assertEquals(1, statusCounter);

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Test
    void getAllFinishedAppointmentsNoAppointmentFound() {
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.get(URL + "/status/done")
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof AppointmentNotFoundException))
                    .andExpect(result -> assertEquals("No Appointment found!", result.getResolvedException().getMessage()))
                    .andReturn().getResponse().getContentAsString();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Test
    void getAllCanceledAppointmentsOK(){
        try {
            String response = mockMvc
                    .perform(
                            MockMvcRequestBuilders.get(URL + "/status/cancel")
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<AppointmentResponseDto> responseDtos = JsonUtils.toList(response, AppointmentResponseDto.class);

            long statusCounter = responseDtos.stream().filter(appointmentResponseDto -> appointmentResponseDto.getStatus().equals(Status.CANCELED_BY_DOCTOR) ||
                    appointmentResponseDto.getStatus().equals(Status.CANCELED_BY_PATIENT) ||
                    appointmentResponseDto.getStatus().equals(Status.CANCELED_BY_SECRETARY))
                    .count();

            assertThat(responseDtos, hasSize(1));
            assertEquals(1, statusCounter);

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Test
    void getAllCanceledAppointmentsNoAppointmentFound(){
        try {
            String response = mockMvc
                    .perform(
                            MockMvcRequestBuilders.get(URL + "/status/cancel")
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof AppointmentNotFoundException))
                    .andExpect(result -> assertEquals("No Appointment found!", result.getResolvedException().getMessage()))
                    .andReturn().getResponse().getContentAsString();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Test
    void approveAppointmentWhenStatusPending() {
        Long appointmentId = 38l;
        try {
            val response = mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/approve/" + appointmentId)
                                    .header("Authorization", "Bearer " + TOKEN))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            val appointment = JsonUtils.toObject(response, AppointmentResponseDto.class);

            assertEquals(Status.APPROVED, appointment.getStatus());
        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Test
    void approveAppointmentWhenStatusApproved() {
        Long appointmentId = 38l;
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/approve/" + appointmentId)
                                    .header("Authorization", "Bearer " + TOKEN))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("Appointment is not in valid status for this operation", result.getResolvedException().getMessage()))
                    .andReturn().getResponse().getContentAsString();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Test
    void approveAppointmentWhenStatusCanceled() {
        Long appointmentId = 38l;
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/approve/" + appointmentId)
                                    .header("Authorization", "Bearer " + TOKEN))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("Appointment is not in valid status for this operation", result.getResolvedException().getMessage()))
                    .andReturn().getResponse().getContentAsString();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Test
    void approveAppointmentWhenStatusDone() {
        Long appointmentId = 38l;
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/approve/" + appointmentId)
                                    .header("Authorization", "Bearer " + TOKEN))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("Appointment is not in valid status for this operation", result.getResolvedException().getMessage()))
                    .andReturn().getResponse().getContentAsString();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Test
    void approveAppointmentWhenStatusDoctorChangeRequest() {
        Long appointmentId = 38l;
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/approve/" + appointmentId)
                                    .header("Authorization", "Bearer " + TOKEN))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("Appointment is not in valid status for this operation", result.getResolvedException().getMessage()))
                    .andReturn().getResponse().getContentAsString();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Test
    void approveAppointmentWhenStatusDoctorChangeApprovedOK() {
        Long appointmentId = 38l;
        try {
            val response = mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/approve/" + appointmentId)
                                    .header("Authorization", "Bearer " + TOKEN))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            AppointmentResponseDto responseDto = JsonUtils.toObject(response, AppointmentResponseDto.class);
            assertEquals(Status.APPROVED, responseDto.getStatus());

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Test
    void approveAppointmentWhenStatusDoctorChangeRefused() {
        Long appointmentId = 38l;
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/approve/" + appointmentId)
                                    .header("Authorization", "Bearer " + TOKEN))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("Appointment is not in valid status for this operation", result.getResolvedException().getMessage()))
                    .andReturn().getResponse().getContentAsString();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Test
    void setAppointmentToDoneWhenStatusPending() {
        Long appointmentId = 38l;
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/done/" + appointmentId)
                                    .header("Authorization", "Bearer " + TOKEN))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("Appointment is not in valid status for this operation", result.getResolvedException().getMessage()));

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Test
    void setAppointmentToDoneWhenStatusDone() {
        Long appointmentId = 38l;
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/done/" + appointmentId)
                                    .header("Authorization", "Bearer " + TOKEN))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("Appointment is not in valid status for this operation", result.getResolvedException().getMessage()));

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Test
    void setAppointmentToDoneWhenStatusCanceled() {
        Long appointmentId = 38l;
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/done/" + appointmentId)
                                    .header("Authorization", "Bearer " + TOKEN))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("Appointment is not in valid status for this operation", result.getResolvedException().getMessage()));

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Test
    void setAppointmentToDoneWhenStatusApprovedOK() {
        Long appointmentId = 38l;
        try {
            val response = mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/done/" + appointmentId)
                                    .header("Authorization", "Bearer " + TOKEN))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            val reponseDto = JsonUtils.toObject(response, AppointmentResponseDto.class);

            assertEquals(Status.DONE, reponseDto.getStatus());

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }


    /**
     * Size of appointments with the status selected is returned correctly
     * Result: 200 OK
     */
    @Test
    @Disabled
    void appointmentsByStatus() {
        try {
            val result =
                    mockMvc
                            .perform(
                                    MockMvcRequestBuilders.get(URL + "/status")
                                            .param("status", Status.PENDING.name())
                                            .header("Authorization", "Bearer " + TOKEN)
                                            .contentType(MediaType.APPLICATION_JSON))
                            .andDo(print())
                            .andExpect(status().isOk())
                            .andReturn().getResponse().getContentAsString();

            val listResponse = JsonUtils.toList(result, AppointmentResponseDto.class);
            assertThat(listResponse, hasSize(6));

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);

        }
    }

    /**
     * Size of appointments for the patient selected is returned coorectly
     * Result: 200 OK
     */
    @Test
    @Disabled
    void appointmentsByPatientSuccess() {
        Long patientId = 3l;
        val response = createGetAsString(URL + "/patient/" + patientId, String.class, TOKEN);
        val listResponse = JsonUtils.toList(response, AppointmentResponseDto.class);
        assertThat(listResponse, hasSize(11));
    }

    /**
     * When Patient with the requested id is not found
     * Result: 400 "No user was found with this id and role"
     */
    @Test
    @Disabled
    void appointmentsByPatientNotFound() {
        Long patientId = 19l;
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.get(URL + "/patient/" + patientId)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("No user was found with this id and role", result.getResolvedException().getMessage()))
                    .andReturn().getResponse().getContentAsString();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);

        }
    }

    /**
     * When doctor is available in the requested time
     * Result: 200 OK
     */
    @Test
    void changeDoctorSuccessDoctorAvailable() {
        AppointmentDto appointmentDto = new AppointmentDto();
        UserDto doctor = new UserDto();
        doctor.setId(5l);
        appointmentDto.setId(38l);
        appointmentDto.setDoctor(doctor);
        val response = createPut(URL + "/change", appointmentDto, AppointmentResponseDto.class, TOKEN);
        assertEquals(5l, response.getDoctorName());
        assertEquals(Status.DOCTOR_CHANGE_REQUEST, response.getStatus());
    }

    @Test
    void approveDoctorChangeSuccess() {
        AppointmentDto appointmentDto = new AppointmentDto();
        appointmentDto.setId(38l);
        appointmentDto.setStatus(Status.DOCTOR_CHANGE_APPROVED);
        val response = createPut(URL + "/patient/doctor-change", appointmentDto, AppointmentResponseDto.class, TOKEN);
        assertEquals(Status.DOCTOR_CHANGE_APPROVED, response.getStatus());
    }

    /**
     * When user has not accepted doctor change request
     * Result: 200 OK
     */
    @Test
    void changeDoctorFailUserNotAccepted() {
        AppointmentDto appointmentDto = new AppointmentDto();
        UserDto doctor = new UserDto();
        doctor.setId(2l);
        appointmentDto.setId(38l);
        appointmentDto.setDoctor(doctor);
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/change")
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonUtils.toJsonString(appointmentDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("Appointment is not in valid status for this operation", result.getResolvedException().getMessage()))
                    .andReturn();
        } catch (Exception e) {
            ExceptionUtils.rethrow(e);

        }
    }

    @Test
    void setAppointmentToDoneNoDoctorFeedback() {
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/done/38")
                                    .header("Authorization", "Bearer " + TOKEN))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("Cannot update to DONE. No feedback from doctor!", result.getResolvedException().getMessage()))
                    .andReturn();
        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    /**
     * When doctor is not available in th selected time
     * Result: 400 "Doctor is not available in this time!"
     */
    @Test
    @Disabled
    void changeDoctorFailDoctorNotAvailable() throws ReservationAppException {
        AppointmentDto appointmentDto = new AppointmentDto();
        UserDto doctor = new UserDto();
        doctor.setId(2l);
        appointmentDto.setId(36l);
        appointmentDto.setDoctor(doctor);
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL + "/change")
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonUtils.toJsonString(appointmentDto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("Doctor is not available in this time!", result.getResolvedException().getMessage()))
                    .andReturn().getResponse().getContentAsString();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);

        }
    }

    @Test
    @Disabled
    void updateAppointmentSuccess() {
        AppointmentDto newAppointment = new AppointmentDto();
        newAppointment.setId(23l);
        newAppointment.setFeedback("Feedback completed");
        val response = createPut(URL, newAppointment, AppointmentResponseDto.class, TOKEN);
        assertEquals("Feedback completed", response.getFeedback());
    }

    @Test
    @Disabled
    void updateAppointmentDONEFailUserNotAllowed() {
        AppointmentDto newAppointment = new AppointmentDto();
        newAppointment.setId(23l);
        newAppointment.setFeedback("Updated feedback");
        newAppointment.setStatus(Status.DONE);
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.put(URL)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonUtils.toJsonString(newAppointment))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("Cannot update to DONE. No feedback from doctor or role not allowed!", result.getResolvedException().getMessage()))
                    .andReturn().getResponse().getContentAsString();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);

        }
    }

    /**
     * When a user is allowed to update an appointment to DONE
     * Result: 200 OK
     */
    @Test
    @Disabled
    void updateAppointmentDONEUserAllowed() {
        AppointmentDto newAppointment = new AppointmentDto();
        newAppointment.setId(23l);
        newAppointment.setFeedback("UPDATED FEEDBACK");
        newAppointment.setStatus(Status.DONE);
        val response = createPut(URL, newAppointment, AppointmentResponseDto.class, TOKEN);

        assertEquals("UPDATED FEEDBACK", response.getFeedback());
        assertEquals(Status.DONE, response.getStatus());
    }

    @Test
    void getDoctorFinishedAppointments() throws ReservationAppException {
        Long doctorId = 5l;
        try {
            val response = mockMvc
                    .perform(
                            MockMvcRequestBuilders.get(URL + "/doctor/done/" + doctorId)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<AppointmentResponseDto> reponseDtos = JsonUtils.toList(response, AppointmentResponseDto.class);

            assertThat(reponseDtos, hasSize(1));

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);

        }
    }

    @Test
    void getDoctorCanceledAppointments() throws ReservationAppException {
        Long doctorId = 5l;
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.get(URL + "/doctor/canceled/" + doctorId)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof AppointmentNotFoundException))
                    .andExpect(result -> assertEquals("No Appointment found!", result.getResolvedException().getMessage()))
                    .andReturn();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);

        }
    }

    /**
     * When no appointments are found with the requested status
     * Result: 404 "No appointment found!"
     * @throws AppointmentNotFoundException
     */
    @Test
    @Disabled
    void appointmentByStatusAndPatientNoAppointmentFound() throws AppointmentNotFoundException {
        Long patientId = 3l;
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.get(URL + "/patientstatus/" + patientId)
                                    .param("status", Status.DONE.name())
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof AppointmentNotFoundException))
                    .andExpect(result -> assertEquals("No appointment found!", result.getResolvedException().getMessage()))
                    .andReturn().getResponse().getContentAsString();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);

        }
    }
}
