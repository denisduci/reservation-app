//package com.ikub.reservationapp;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import com.ikub.reservationapp.common.utils.JsonUtils;
//import lombok.val;
//import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
////@ActiveProfiles("test")
//@AutoConfigureMockMvc
//public abstract class ReservationAppTestSupport {
//
//    @Autowired
//    protected MockMvc mockMvc;
//
//    @Test
//	void contextLoads() {
//	}
//
//    protected <INPUT, OUTPUT> OUTPUT createPost(
//            String url, INPUT dto, Class<OUTPUT> objectType, String token) {
//        OUTPUT resultDto = null;
//        try {
//            val result =
//                    mockMvc
//                            .perform(
//                                    MockMvcRequestBuilders.post(url)
//                                            .header("Authorization", token)
//                                            .contentType(MediaType.APPLICATION_JSON)
//                                            .content(JsonUtils.toJsonString(dto)))
//                            .andExpect(status().isOk())
//                            .andReturn();
//
//            resultDto = JsonUtils.toObject(result.getResponse().getContentAsString(), objectType);
//        } catch (Exception e) {
//            ExceptionUtils.rethrow(e);
//        }
//
//        return resultDto;
//    }
//
//    protected <OUTPUT> OUTPUT createGet(String url, Class<OUTPUT> objectType, String token) {
//        OUTPUT resultDto = null;
//        try {
//            val result =
//                    mockMvc
//                            .perform(
//                                    MockMvcRequestBuilders.get(url)
//                                            .header("Authorization", token)
//                                            .contentType(MediaType.APPLICATION_JSON))
//                            .andExpect(status().isOk())
//                            .andReturn();
//
//            resultDto = JsonUtils.toObject(result.getResponse().getContentAsString(), objectType);
//        } catch (Exception e) {
//            ExceptionUtils.rethrow(e);
//
//        }
//
//        return resultDto;
//    }
//
//    protected <INPUT, OUTPUT> OUTPUT createPut(
//            String url, INPUT dto, Class<OUTPUT> objectType, String token) {
//        OUTPUT resultDto = null;
//        try {
//            val result =
//                    mockMvc
//                            .perform(
//                                    MockMvcRequestBuilders.put(url, "id")
//                                            .header("Authorization", token)
//                                            .contentType(MediaType.APPLICATION_JSON)
//                                            .content(JsonUtils.toJsonString(dto)))
//                            .andExpect(status().isOk())
//                            .andReturn();
//
//            resultDto = JsonUtils.toObject(result.getResponse().getContentAsString(), objectType);
//        } catch (Exception e) {
//            ExceptionUtils.rethrow(e);
//        }
//        return resultDto;
//    }
//
//    protected <INPUT, OUTPUT> OUTPUT createPatch(String url, INPUT dto, Class<OUTPUT> objectType, String token) {
//
//        OUTPUT resultDto = null;
//        try {
//            val resultlt = mockMvc.perform(MockMvcRequestBuilders.patch(url, "id")
//                    .header("Authorization", token)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(JsonUtils.toJsonString(dto))).andExpect(status().isOk())
//                    .andReturn();
//            resultDto = JsonUtils.toObject(resultlt.getResponse().getContentAsString(), objectType);
//        } catch (Exception e) {
//            ExceptionUtils.rethrow(e);
//        }
//        return resultDto;
//
//    }
//
//    protected void createDelete(String url, String token) {
//        try {
//            val result =
//                    mockMvc
//                            .perform(
//                                    MockMvcRequestBuilders.delete(url)
//                                            .header("Authorization", token)
//                                            .contentType(MediaType.APPLICATION_JSON))
//                            .andExpect(status().isOk())
//                            .andReturn();
//
//        } catch (Exception e) {
//            ExceptionUtils.rethrow(e);
//        }
//    }
//}