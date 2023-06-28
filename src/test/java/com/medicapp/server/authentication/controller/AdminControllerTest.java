package com.medicapp.server.authentication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicapp.server.authentication.dto.AuthenticationRequest;
import com.medicapp.server.authentication.dto.AuthenticationResponse;
import com.medicapp.server.authentication.dto.RegisterRequest;
import com.medicapp.server.authentication.dto.UserRequest;
import com.medicapp.server.authentication.repository.TokenRepository;
import com.medicapp.server.authentication.repository.UserRepository;
import com.medicapp.server.authentication.service.JwtService;
import com.medicapp.server.config.AdminConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Value("${credentials.admin.email}")
    private String defaultAdminEmail;
    @Value("${credentials.admin.password}")
    private String defaultAdminPassword;

    @Test
    public void testRegisterUser() throws Exception {

        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email(defaultAdminEmail)
                .password(defaultAdminPassword)
                .build();

        String requestAuthBody = new ObjectMapper().writeValueAsString(authenticationRequest);

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestAuthBody))
                .andExpect(status().isOk());

        String responseString = response.andReturn().getResponse().getContentAsString();
        AuthenticationResponse authenticationResponse = new ObjectMapper().readValue(responseString, AuthenticationResponse.class);
        String JwtToken = authenticationResponse.getToken();

        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("maria.gracia@gmail.com")
                .password("123456")
                .firstname("Diego")
                .lastname("Peña")
                .sex("male")
                .build();

        String requestBody = new ObjectMapper().writeValueAsString(registerRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/admin/user")
                        .header("Authorization", "Bearer " + JwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegisterUserFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/admin/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testUpdateUser() throws Exception {
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email(defaultAdminEmail)
                .password(defaultAdminPassword)
                .build();

        String requestAuthBody = new ObjectMapper().writeValueAsString(authenticationRequest);

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestAuthBody))
                .andExpect(status().isOk());

        String responseString = response.andReturn().getResponse().getContentAsString();
        AuthenticationResponse authenticationResponse = new ObjectMapper().readValue(responseString, AuthenticationResponse.class);
        String JwtToken = authenticationResponse.getToken();

        UserRequest userRequest = UserRequest.builder()
                .firstname("Maria")
                .build();

        String requestBody = new ObjectMapper().writeValueAsString(userRequest);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/admin/user/2")
                        .header("Authorization", "Bearer " + JwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateUserFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/admin/user/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }


    @Test
    public void testDeleteUser() throws Exception {
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email(defaultAdminEmail)
                .password(defaultAdminPassword)
                .build();

        String requestAuthBody = new ObjectMapper().writeValueAsString(authenticationRequest);

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestAuthBody))
                .andExpect(status().isOk());

        String responseString = response.andReturn().getResponse().getContentAsString();
        AuthenticationResponse authenticationResponse = new ObjectMapper().readValue(responseString, AuthenticationResponse.class);
        String JwtToken = authenticationResponse.getToken();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/admin/user/2")
                        .header("Authorization", "Bearer " + JwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }

    @Test
    public void testDeleteUserFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/admin/user/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}
