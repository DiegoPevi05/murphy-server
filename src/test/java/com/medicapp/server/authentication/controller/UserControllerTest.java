package com.medicapp.server.authentication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicapp.server.authentication.dto.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    static String JwtToken;
    static UserResponse userResponse;

    @Test
    @Order(1)
    public void InitializeUserToken() throws Exception {
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email("diego.pena@utec.edu.pe") //User is defined in AdminConfig for testing
                .password("123456") // User is defined in AdminConfig for testing
                .build();

        String requestAuthBody = new ObjectMapper().writeValueAsString(authenticationRequest);

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestAuthBody))
                .andExpect(status().isOk());
        String responseString = response.andReturn().getResponse().getContentAsString();
        AuthenticationResponse authenticationResponse = new ObjectMapper().readValue(responseString, AuthenticationResponse.class);
        JwtToken = authenticationResponse.getToken();
    }

    @Test
    @Order(2)
    public void getMyUserData() throws Exception {
        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/user")
                        .header("Authorization", "Bearer " + JwtToken))
                .andExpect(status().isOk());
        String responseString = response.andReturn().getResponse().getContentAsString();
        userResponse = new ObjectMapper().readValue(responseString, UserResponse.class);
    }

    @Test
    @Order(4)
    public void getMyUserDataFail() throws  Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(5)
    public void updateMyUserData() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .firstname("usercontroller")
                .lastname("usercontrollername")
                .build();

        String requestBody = new ObjectMapper().writeValueAsString(userRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/user/"+userResponse.getId())
                        .header("Authorization", "Bearer " + JwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    public void updateMyUserDataFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/user/"+userResponse.getId())
                        .header("Authorization", "Bearer " + JwtToken))
                .andExpect(status().isBadRequest());
    }
}
