package com.medicapp.server.authentication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicapp.server.authentication.dto.AuthenticationRequest;
import com.medicapp.server.authentication.dto.RegisterRequest;
import com.medicapp.server.authentication.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRegister() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("diego.pena@utec.edu.pe")
                .password("123456")
                .firstname("Diego")
                .lastname("Peña")
                .sex("male")
                .build();

        String requestBody = new ObjectMapper().writeValueAsString(registerRequest);

       mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isOk());
    }

    @Test
    public void testRegisterFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/auth/register")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
    }

    @Test
    public void testAuthenticate() throws Exception {
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email("diego10azul@hotmail.com")
                .password("123456")
                .build();

        String requestBody = new ObjectMapper().writeValueAsString(authenticationRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isOk());
    }

    @Test
    public void testAuthenticateFail() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
    }

    @Test
    public void testConfirmToken() throws Exception {
        String token = "confirmation_token";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/auth/confirm")
                        .param("token", token))
                        .andExpect(status().isOk());
    }
}
