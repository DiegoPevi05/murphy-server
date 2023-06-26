package com.medicapp.server.authentication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicapp.server.authentication.dto.AuthenticationRequest;
import com.medicapp.server.authentication.dto.AuthenticationResponse;
import com.medicapp.server.authentication.dto.RegisterRequest;
import com.medicapp.server.authentication.model.Token;
import com.medicapp.server.authentication.model.TokenType;
import com.medicapp.server.authentication.repository.TokenRepository;
import com.medicapp.server.authentication.repository.UserRepository;
import com.medicapp.server.authentication.service.AuthenticationService;
import com.medicapp.server.authentication.service.JwtService;
import com.medicapp.server.authentication.service.UserService;
import com.medicapp.server.config.AdminConfig;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminConfig adminConfig;

    private String token;
    @Value("${credentials.admin.email}")
    private String defaultAdminEmail;

    @Before
    public void setup() throws Exception {
        adminConfig.createDefaultAdminUser();
        // Perform the authentication process to obtain the token
        var user = userRepository.findByEmail(defaultAdminEmail)
                .orElseThrow();
        this.token = jwtService.generateToken(user);

        Token tokenObject = Token.builder()
                .user(user)
                .token(this.token)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(tokenObject);
    }

    @Test
    public void testRegisterUser() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("diego.pena@utec.edu.pe")
                .password("123456")
                .firstname("Diego")
                .lastname("Peña")
                .sex("male")
                .build();
        System.out.println(token);
        String requestBody = new ObjectMapper().writeValueAsString(registerRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/admin/user")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Ignore
    @Test
    public void testRegisterUserFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/admin/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Ignore
    @Test
    public void testUpdateUser() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("diego.pena@utec.edu.pe")
                .password("123456")
                .firstname("Diego")
                .lastname("Peña")
                .sex("male")
                .build();

        String requestBody = new ObjectMapper().writeValueAsString(registerRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/admin/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Ignore
    @Test
    public void testUpdateUserFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/admin/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Ignore
    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/admin/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Ignore
    @Test
    public void testDeleteUserFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/admin/user/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
