package com.ikub.reservationapp.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikub.reservationapp.ReservationAppTestSupport;
import com.ikub.reservationapp.common.model.AuthToken;
import com.ikub.reservationapp.common.model.LoginUser;
import com.ikub.reservationapp.common.utils.JsonUtils;
import com.ikub.reservationapp.users.dto.RoleDto;
import com.ikub.reservationapp.users.dto.UserDto;
import lombok.val;
import org.apache.catalina.User;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import  static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.MatcherAssert.assertThat;

//@SpringBootTest(classes = ReservationAppApplication.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest extends ReservationAppTestSupport {

    private String URL = "/users";
    private static String TOKEN = "";
    private static String REFRESH_TOKEN = "";

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeAll
    static void setup(@Autowired MockMvc mockMvc) {

        val loginUser = LoginUser.builder()
                .username("doctor1").password("Abcde12345?").build();

        try {
            val result = mockMvc
                    .perform(
                            MockMvcRequestBuilders.post("/users/authenticate")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonUtils.toJsonString(loginUser)))
                    .andExpect(status().isOk())
                    .andReturn();
            TOKEN = JsonUtils.toObject(result.getResponse().getContentAsString(), AuthToken.class).getAccessToken();
            REFRESH_TOKEN = JsonUtils.toObject(result.getResponse().getContentAsString(), AuthToken.class).getRefreshToken();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
            TOKEN = "";
        }
    }

    @Test
    void getUser() {
        val response = createGet(URL + "/1", UserDto.class, TOKEN);
        assertEquals("admin@mail.com", response.getEmail());
    }

    @Test
    void refreshToken() {
        try {
            val result = mockMvc
                    .perform(
                            MockMvcRequestBuilders.get("/users/refreshtoken")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + REFRESH_TOKEN))
                    .andExpect(status().isOk())
                    .andReturn();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Test
    void authenticateOk() {
        val loginUser = LoginUser.builder()
                .username("doctor1").password("Abcde12345?").build();

        try {
            val result =
                    mockMvc
                            .perform(
                                    MockMvcRequestBuilders.post(URL + "/authenticate")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(JsonUtils.toJsonString(loginUser)))
                            .andExpect(status().isOk())
                            .andReturn();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Test
    void authenticateFail() {
        val loginUserFail = LoginUser.builder()
                .username("nouserexist").password("Abcde12345?").build();

        try {
            val result =
                    mockMvc
                            .perform(
                                    MockMvcRequestBuilders.post(URL + "/authenticate")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(JsonUtils.toJsonString(loginUserFail)))
                            .andExpect(status().is4xxClientError())
                            .andReturn();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Test
    void saveUserExists() {

        val userExists = UserDto.builder()
                .username("test_user").firstName("TEST USER").lastName("TEST USER")
                .password("Abcde12345?").confirmPassword("Abcde12345?")
                .email("testuser@mail.com").phone("0000000000")
                .build();

        try {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.post(URL + "/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonUtils.toJsonString(userExists)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("User with username already exists"))
                    .andReturn();

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Test
    void listAllUsers() {
        try {
            val result = createGetAsString(URL + "/all", String.class, TOKEN);
            val resultList = JsonUtils.toList(result, UserDto.class);
            assertThat(resultList, hasSize(8));
        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }
//    @Test
//    void saveUserNotExists() {
//
//        val userNotExists = UserDto.builder()
//                .username("new_user").firstName("TEST USER").lastName("TEST USER")
//                .password("Abcde12345?").confirmPassword("Abcde12345?")
//                .email("new@mail.com").phone("0000000000")
//                .build();
//
//        try {
//
//            val resultNotExists = mockMvc
//                    .perform(
//                            MockMvcRequestBuilders.post(URL + "/register")
//                                    .contentType(MediaType.APPLICATION_JSON)
//                                    .content(JsonUtils.toJsonString(userNotExists)))
//                    .andExpect(status().isOk())
//                    .andReturn();
//
//            assertEquals("new@mail.com", JsonUtils.toObject(resultNotExists.getResponse().getContentAsString(), UserDto.class).getEmail());
//
//        } catch (Exception e) {
//            ExceptionUtils.rethrow(e);
//        }
//    }
}
