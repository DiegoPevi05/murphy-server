package com.medicapp.server.authentication.service;

import com.medicapp.server.authentication.dto.RegisterRequest;
import com.medicapp.server.authentication.repository.TokenRepository;
import com.medicapp.server.authentication.repository.UserRepository;
import com.medicapp.server.doctors.repository.DoctorRepository;
import com.medicapp.server.email.EmailService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;


@SpringBootTest
public class AuthenticationServiceTest {
    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private ConfirmationTokenService confirmationTokenService;

    @Test
    public void testRegister() throws Exception {
        // Mock the necessary dependencies

        // Create a RegisterRequest object
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("diego.pena@utec.edu.pe")
                .password("123456")
                .firstname("Diego")
                .lastname("Peña")
                .sex("male")
                .build();

        // Mock the UserRepository behavior
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Mock the PasswordEncoder behavior
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Capture the arguments passed to emailService.send() method
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> firstNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> profileUrlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> linkActivationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> emailTypeCaptor = ArgumentCaptor.forClass(String.class);

        // Perform the register operation
        String result = authenticationService.register(registerRequest);

        // Verify the userRepository and passwordEncoder interactions
        verify(userRepository).findByEmail(anyString());
        verify(passwordEncoder).encode(anyString());

        // Verify the emailService.send() method is called with the expected arguments
        verify(emailService).send(
                emailCaptor.capture(),
                subjectCaptor.capture(),
                messageCaptor.capture(),
                firstNameCaptor.capture(),
                profileUrlCaptor.capture(),
                linkActivationCaptor.capture(),
                emailTypeCaptor.capture()
        );

        // Assert the result
        assertEquals("Confirmation Email has been sent to " + registerRequest.getEmail() + ".", result);

        // Assert the captured arguments of emailService.send() method
        assertEquals(registerRequest.getEmail(), emailCaptor.getValue());
        assertEquals("Activation Account", subjectCaptor.getValue());
        assertEquals("Activate your Account", messageCaptor.getValue());
        assertEquals(registerRequest.getFirstname(), firstNameCaptor.getValue());
    }


}
