//package com.ikub.reservationapp.user.controller;
//
//import com.ikub.reservationapp.ReservationAppApplication;
//import com.ikub.reservationapp.ReservationAppTestSupport;
//import com.ikub.reservationapp.common.model.LoginUser;
//import com.ikub.reservationapp.common.utils.JsonUtils;
//import lombok.val;
//import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.junit.jupiter.api.BeforeAll;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest(classes = ReservationAppApplication.class)
//public class UserControllerTest extends ReservationAppTestSupport {
//
//    private String url = "/users";
//    private static String TOKEN = "";
//
//    @Autowired
//    private BCryptPasswordEncoder bCryptPasswordEncoder;
//
//    @BeforeAll
//    static void setup(@Autowired MockMvc mockMvc) {
//
//        val authDto = LoginUser.builder()
//                .username("user1").password("abc123").build();
//
//        try {
//            TOKEN = mockMvc
//                    .perform(
//                            MockMvcRequestBuilders.post("/users/authenticate")
//                                    .contentType(MediaType.APPLICATION_JSON)
//                                    .content(JsonUtils.toJsonString(authDto)))
//                    .andExpect(status().isOk())
//                    .andReturn()
//                    .getResponse().getHeader("Authorization");
//        } catch (Exception e) {
//            ExceptionUtils.rethrow(e);
//            TOKEN = "";
//        }
//    }
//}
