package com.ikub.reservationapp.appointment.controller;

import com.ikub.reservationapp.ReservationAppTestSupport;
import com.ikub.reservationapp.appointments.dto.AppointmentDateHourDto;
import com.ikub.reservationapp.appointments.dto.AppointmentDto;
import com.ikub.reservationapp.appointments.exception.AppointmentNotFoundException;
import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import com.ikub.reservationapp.common.model.AuthToken;
import com.ikub.reservationapp.common.model.LoginUser;
import com.ikub.reservationapp.common.utils.JsonUtils;
import com.ikub.reservationapp.users.dto.UserDto;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.time.LocalDate;
import java.time.LocalDateTime;
import  static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.*;
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
                .username("doctor1").password("Abcde12345?").build();

        val loginPatient = LoginUser.builder().username("patient4")
                .password("Abcde12345?").build();

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
         createGet(URL + "/available", AppointmentDateHourDto.class, TOKEN);
    }

    /**
     * When appointment is created successfully
     * Result: 200 OK
     */
//    @Test
//    void createAppointment() {
//        AppointmentDto appointmentDto = new AppointmentDto();
//        UserDto doctor = new UserDto();
//        doctor.setId(5l);
//        UserDto patient = new UserDto();
//        patient.setId(3l);
//        LocalDate appointmentDate = LocalDate.of(2021, 10, 12);
//        LocalDateTime startTime = LocalDateTime.of(2021, 10, 12, 8, 00, 0);
//        LocalDateTime endTime = LocalDateTime.of(2021, 10, 12, 12, 00, 0);
//        appointmentDto.setAppointmentDate(appointmentDate);
//        appointmentDto.setStartTime(startTime);
//        appointmentDto.setEndTime(endTime);
//        appointmentDto.setComments("Dentist appointment");
//        appointmentDto.setDescription("Dentist appointment");
//        appointmentDto.setDoctor(doctor);
//        appointmentDto.setPatient(patient);
//        val response = createPost(URL, appointmentDto, AppointmentDto.class, TOKEN);
//        assertEquals(Status.PENDING, response.getStatus());
//    }

    /**
     * When doctor is not available in the selected time!
     * Result: 400 "Doctor is not available in this time!"
     * @throws ReservationAppException
     */
//    @Test
//    void createAppointmentFailDoctorNotAvailable() throws ReservationAppException {
//        AppointmentDto appointmentDto = new AppointmentDto();
//        UserDto doctor = new UserDto();
//        doctor.setId(5l);
//        UserDto patient = new UserDto();
//        patient.setId(3l);
//        LocalDate appointmentDate = LocalDate.of(2021, 10, 12);
//        LocalDateTime startTime = LocalDateTime.of(2021, 10, 12, 8, 00, 0);
//        LocalDateTime endTime = LocalDateTime.of(2021, 10, 12, 12, 00, 0);
//        appointmentDto.setAppointmentDate(appointmentDate);
//        appointmentDto.setStartTime(startTime);
//        appointmentDto.setEndTime(endTime);
//        appointmentDto.setComments("Dentist appointment");
//        appointmentDto.setDescription("Dentist appointment");
//        appointmentDto.setDoctor(doctor);
//        appointmentDto.setPatient(patient);
//        //val response = createPost(URL, appointmentDto, AppointmentDto.class, TOKEN);
//
//        try {
//            mockMvc
//                    .perform(
//                            MockMvcRequestBuilders.post(URL)
//                                    .header("Authorization", "Bearer " + TOKEN)
//                                    .contentType(MediaType.APPLICATION_JSON)
//                                    .content(JsonUtils.toJsonString(appointmentDto)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
//                    .andExpect(result -> assertEquals("Doctor is not available in this time!", result.getResolvedException().getMessage()))
//                    .andReturn();
//        } catch (Exception e) {
//            ExceptionUtils.rethrow(e);
//        }
//    }

    /**
     * When appoitnment is out of normal business hours
     * Result: 400 "Appointment time is out of business hours!"
     * @throws ReservationAppException
     */
    @Test
    void createAppointmentFailOutOfBusinessHours() throws ReservationAppException {
        AppointmentDto appointmentDto = new AppointmentDto();
        UserDto doctor = new UserDto();
        doctor.setId(5l);
        UserDto patient = new UserDto();
        patient.setId(3l);
        LocalDate appointmentDate = LocalDate.of(2021, 10, 12);
        LocalDateTime startTime = LocalDateTime.of(2021, 10, 12, 7, 00, 0);
        LocalDateTime endTime = LocalDateTime.of(2021, 10, 12, 12, 00, 0);
        appointmentDto.setAppointmentDate(appointmentDate);
        appointmentDto.setStartTime(startTime);
        appointmentDto.setEndTime(endTime);
        appointmentDto.setComments("Dentist appointment");
        appointmentDto.setDescription("Dentist appointment");
        appointmentDto.setDoctor(doctor);
        appointmentDto.setPatient(patient);
        //val response = createPost(URL, appointmentDto, AppointmentDto.class, TOKEN);

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
        //val response = createPost(URL, appointmentDto, AppointmentDto.class, TOKEN);

        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.post(URL)
                                    .header("Authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonUtils.toJsonString(appointmentDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
                    .andExpect(result -> assertEquals("The date selected is not valid. Please reserve a coming date!", result.getResolvedException().getMessage()))
                    .andReturn();
        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    /**
     * Result: list of appointments has the correct size.
     */
//    @Test
//    void getAllAppointments(){
//
//        val result = createGetAsString(URL, String.class, TOKEN);
//        val resultList = JsonUtils.toList(result, AppointmentDto.class);
//        assertThat(resultList, hasSize(10));
//
//    }


    /**
     * When appointment time is too short to cancel
     * Result: 400 "Too short time to cancel Appointment!"
     * @throws ReservationAppException
     */
//    @Test
//    void cancelAppointmentFailShortTime() throws ReservationAppException {
//        AppointmentDto appointmentDto = new AppointmentDto();
//        appointmentDto.setId(6l);
//        try {
//            mockMvc
//                    .perform(
//                            MockMvcRequestBuilders.put(URL + "/cancel")
//                                    .header("Authorization", "Bearer " + TOKEN)
//                                    .contentType(MediaType.APPLICATION_JSON)
//                                    .content(JsonUtils.toJsonString(appointmentDto)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
//                    .andExpect(result -> assertEquals("Too short time to cancel Appointment!", result.getResolvedException().getMessage()))
//                    .andReturn();
//        } catch (Exception e) {
//            ExceptionUtils.rethrow(e);
//        }
//    }

    /**
     * When appointment can be canceled successfully
     * Result: 200 OK
     */
//        @Test
//    void cancelAppointmentSuccess() {
//
//        AppointmentDto appointmentDto = new AppointmentDto();
//        appointmentDto.setId(9l);
//
//        val response = createPut(URL + "/cancel", appointmentDto, AppointmentDto.class, TOKEN);
//        assertEquals(Status.CANCELED, response.getStatus());
//
//    }

    /**
     * When patient exists and appointment exists
     * Result: 200 OK
     */
    @Test
    void appointmentsByStatusAndPatientSuccess() {
        Long patientId = 3l;
        try {
            val result =
                    mockMvc
                            .perform(
                                    MockMvcRequestBuilders.get(URL + "/patientstatus/" + patientId)
                                            .param("status", Status.PENDING.name())
                                            .header("Authorization", "Bearer " + TOKEN)
                                            .contentType(MediaType.APPLICATION_JSON))
                            .andDo(print())
                            .andExpect(status().isOk())
                            .andReturn().getResponse().getContentAsString();

            val listResponse = JsonUtils.toList(result, AppointmentDto.class);
            assertThat(listResponse, hasSize(6));

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);

        }
    }

    /**
     * Size of appointments with the status selected is returned correctly
     * Result: 200 OK
     */
    @Test
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

            val listResponse = JsonUtils.toList(result, AppointmentDto.class);
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
    void appointmentsByPatientSuccess() {
        Long patientId = 3l;
        val response = createGetAsString(URL + "/patient/" + patientId, String.class, TOKEN);
        val listResponse = JsonUtils.toList(response, AppointmentDto.class);
        assertThat(listResponse, hasSize(11));
    }

    /**
     * When Patient with the requested id is not found
     * Result: 400 "No user was found with this id and role"
     */
    @Test
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
        doctor.setId(2l);
        appointmentDto.setId(22l);
        appointmentDto.setDoctor(doctor);
        val response = createPut(URL + "/change", appointmentDto, AppointmentDto.class, TOKEN);
        assertEquals(2l, response.getDoctor().getId());
    }

    /**
     * When doctor is not available in th selected time
     * Result: 400 "Doctor is not available in this time!"
     */
//    @Test
//    void changeDoctorFailDoctorNotAvailable() throws ReservationAppException {
//        AppointmentDto appointmentDto = new AppointmentDto();
//        UserDto doctor = new UserDto();
//        doctor.setId(2l);
//        appointmentDto.setId(22l);
//        appointmentDto.setDoctor(doctor);
//        try {
//            mockMvc
//                    .perform(
//                            MockMvcRequestBuilders.put(URL + "/change")
//                                    .header("Authorization", "Bearer " + TOKEN)
//                                    .contentType(MediaType.APPLICATION_JSON)
//                                    .content(JsonUtils.toJsonString(appointmentDto)))
//                    .andDo(print())
//                    .andExpect(status().isBadRequest())
//                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
//                    .andExpect(result -> assertEquals("Doctor is not available in this time!", result.getResolvedException().getMessage()))
//                    .andReturn().getResponse().getContentAsString();
//
//        } catch (Exception e) {
//            ExceptionUtils.rethrow(e);
//
//        }
//    }

//    @Test
//    void updateAppointmentSuccess() {
//        AppointmentDto newAppointment = new AppointmentDto();
//        newAppointment.setId(23l);
//        newAppointment.setFeedback("Feedback completed");
//        val response = createPut(URL, newAppointment, AppointmentDto.class, TOKEN);
//        assertEquals("Feedback completed", response.getFeedback());
//    }

//    @Test
//    void updateAppointmentDONEFailUserNotAllowed() {
//        AppointmentDto newAppointment = new AppointmentDto();
//        newAppointment.setId(23l);
//        newAppointment.setFeedback("Updated feedback");
//        newAppointment.setStatus(Status.DONE);
//        try {
//            mockMvc
//                    .perform(
//                            MockMvcRequestBuilders.put(URL)
//                                    .header("Authorization", "Bearer " + TOKEN)
//                                    .contentType(MediaType.APPLICATION_JSON)
//                                    .content(JsonUtils.toJsonString(newAppointment))
//                    )
//                    .andDo(print())
//                    .andExpect(status().isBadRequest())
//                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservationAppException))
//                    .andExpect(result -> assertEquals("Cannot update to DONE. No feedback from doctor or role not allowed!", result.getResolvedException().getMessage()))
//                    .andReturn().getResponse().getContentAsString();
//
//        } catch (Exception e) {
//            ExceptionUtils.rethrow(e);
//
//        }
//    }

    /**
     * When a user is allowed to update an appointment to DONE
     * Result: 200 OK
     */
    @Test
    void updateAppointmentDONEUserAllowed() {
        AppointmentDto newAppointment = new AppointmentDto();
        newAppointment.setId(23l);
        newAppointment.setFeedback("UPDATED FEEDBACK");
        newAppointment.setStatus(Status.DONE);
        val response = createPut(URL, newAppointment, AppointmentDto.class, TOKEN);

        assertEquals("UPDATED FEEDBACK", response.getFeedback());
        assertEquals(Status.DONE, response.getStatus());
    }

    /**
     * When no patient is found with the requested id
     * Result: 400 "No user was found with this id and role"
     * @throws ReservationAppException
     */
    @Test
    void appointmentByStatusAndPatientNoPatientFound() throws ReservationAppException {
        Long patientId = 14l;
        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.get(URL + "/patientstatus/" + patientId)
                                    .param("status", Status.PENDING.name())
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
     * When no appointments are found with the requested status
     * Result: 404 "No appointment found!"
     * @throws AppointmentNotFoundException
     */
    @Test
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
