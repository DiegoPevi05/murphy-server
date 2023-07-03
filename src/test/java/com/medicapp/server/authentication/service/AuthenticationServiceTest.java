package com.medicapp.server.authentication.service;

import com.medicapp.server.authentication.dto.AuthenticationRequest;
import com.medicapp.server.authentication.dto.AuthenticationResponse;
import com.medicapp.server.authentication.dto.RegisterRequest;
import com.medicapp.server.authentication.model.ConfirmationToken;
import com.medicapp.server.authentication.model.User;
import com.medicapp.server.authentication.repository.UserRepository;
import com.medicapp.server.email.EmailService;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;


    @Test
    public void testRegister() throws Exception {

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

    @Test
    public void testAuthenticate() {
        // Create an AuthenticationRequest object
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email("test@example.com" )
                .password("password")
                .build();

        // Create a User object
        User user = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .build();

        // Create a JWT token
        String jwtToken = "dummyToken";

        // Mock the authenticationManager behavior
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // Mock the userRepository behavior
        Mockito.when(userRepository.findByEmail(authenticationRequest.getEmail()))
                .thenReturn(Optional.of(user));

        // Mock the jwtService behavior
        Mockito.when(jwtService.generateToken(user))
                .thenReturn(jwtToken);

        // Perform the authenticate operation
        AuthenticationResponse result = authenticationService.authenticate(authenticationRequest);

        // Verify the authenticationManager interaction
        Mockito.verify(authenticationManager)
                .authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class));

        // Verify the userRepository interaction
        Mockito.verify(userRepository).findByEmail(authenticationRequest.getEmail());

        // Verify the jwtService interaction
        Mockito.verify(jwtService).generateToken(user);

        // Assert the result
        assertNotNull(result);
        assertEquals(jwtToken, result.getToken());
    }

    @Test
    public void testConfirmToken() {

        // Create a mock User
        User user = User.builder()
                .email("test@example.com")
                .enabled(false)
                .build();

        // Create a mock ConfirmationToken
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token("dummyToken")
                .confirmedAt(null)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .user(user)
                .build();

        // Mock the confirmationTokenService behavior
        Mockito.when(confirmationTokenService.getToken("dummyToken"))
                .thenReturn(Optional.of(confirmationToken));
        //Mockito.when(confirmationTokenService).setConfirmedAt("dummyToken");

        // Mock the userRepository behavior
        Mockito.when(userRepository.findByEmailDisable(user.getEmail()))
                .thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(user))
                .thenReturn(user);

        // Perform the confirmToken operation
        String result =  authenticationService.confirmToken("dummyToken");

        // Verify the confirmationTokenService interactions
        Mockito.verify(confirmationTokenService).getToken("dummyToken");
        Mockito.verify(confirmationTokenService).setConfirmedAt("dummyToken");

        // Verify the userRepository interactions
        Mockito.verify(userRepository).findByEmailDisable(user.getEmail());
        Mockito.verify(userRepository).save(user);

        // Assert the result
        assertNotNull(result);
        assertEquals("confirmed", result);
        assertTrue(user.isEnabled());
    }


}
