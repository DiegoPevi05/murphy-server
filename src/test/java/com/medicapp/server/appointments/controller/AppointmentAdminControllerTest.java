package com.medicapp.server.appointments.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.medicapp.server.appoinments.dto.AppointmentRequest;
import com.medicapp.server.authentication.dto.AuthenticationRequest;
import com.medicapp.server.authentication.dto.AuthenticationResponse;
import com.medicapp.server.authentication.dto.UserRequest;
import com.medicapp.server.authentication.model.User;
import com.medicapp.server.authentication.repository.UserRepository;
import com.medicapp.server.doctors.model.Doctor;
import com.medicapp.server.doctors.repository.DoctorRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppointmentAdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DoctorRepository doctorRepository;

    @Value("${credentials.admin.email}")
    private String defaultAdminEmail;
    @Value("${credentials.admin.password}")
    private String defaultAdminPassword;

    private final String UserAccountEmailTest = "diego.pena@utec.edu.pe";
    private final String DoctorAccountEmailTest = "diegopevi05@gmail.com";

    static String JwtToken;

    @Test
    @Order(1)
    public void InitializeAdminToken() throws Exception {
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
        JwtToken = authenticationResponse.getToken();
    }

    @Test
    public void getAppointments() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/appointment")
                        .header("Authorization", "Bearer " + JwtToken)
                        .param("page", "0")
                        .param("size", "10")
                        .param("date", "")
                        .param("month", "false")
                        .param("specialty", "")
                        .param("user_id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void registerAppointmentFail() throws Exception {
        User user = userRepository.findByEmail(UserAccountEmailTest).orElseThrow(() -> new Exception("User not found"));
        Doctor doctor = doctorRepository.findByUserEmail(DoctorAccountEmailTest).orElseThrow(() -> new Exception("Doctor not found"));
        AppointmentRequest appointmentRequest = AppointmentRequest.builder()
                .user_id(user.getId())
                .doctor_id(doctor.getId())
                .date(LocalDateTime.now())
                .build();

        // Configure the ObjectMapper with the JSR310 module
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Serialize the appointmentRequest object to JSON
        String requestBody = objectMapper.writeValueAsString(appointmentRequest);


        var response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/admin/appointment")
                        .header("Authorization", "Bearer " + JwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
        var ResponseString  = response.andReturn().getResponse().getContentAsString();
        assertTrue(ResponseString.startsWith("No appointments available for the date "));
    }

    @Test
    public void deleteAppointmentFail() throws Exception {
        User user = userRepository.findByEmail(UserAccountEmailTest).orElseThrow(() -> new Exception("User not found"));
        var response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/admin/appointment/1")
                        .header("Authorization", "Bearer " + JwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        var ResponseString  = response.andReturn().getResponse().getContentAsString();
        assertEquals("Appointment with id 1 does not exist", ResponseString);
    }

    @Test
    public void deleteAppointmentFail2() throws Exception {
        User user = userRepository.findByEmail(UserAccountEmailTest).orElseThrow(() -> new Exception("User not found"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/admin/appointment/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
