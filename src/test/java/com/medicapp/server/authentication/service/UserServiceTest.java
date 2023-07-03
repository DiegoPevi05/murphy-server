package com.medicapp.server.authentication.service;

import com.medicapp.server.authentication.dto.RegisterRequest;
import com.medicapp.server.authentication.dto.UserRequest;
import com.medicapp.server.authentication.model.Role;
import com.medicapp.server.authentication.model.User;
import com.medicapp.server.authentication.repository.UserRepository;
import com.medicapp.server.doctors.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DoctorRepository doctorRepository;

    @Test
    public void GetMe (){

        User user = User.builder()
                .firstname("Diego")
                .lastname("Peña")
                .email("examplegetme@hotmail.com")
                .role(Role.ROLE_USER)
                .sex("male")
                .enabled(true)
                .password("password_encoded")
                .profile_url("profile_url")
                .profile_key("profile_key")
                .build();

        // Mock the Authentication User GetMe
        when(authenticationService.getUserAuthorize()).thenReturn(user);
        var UserResponse = userService.getMe();

        verify(authenticationService).getUserAuthorize();
        assertEquals(UserResponse.getFirstname(), user.getFirstname());
        assertEquals(UserResponse.getLastname(), user.getLastname());
        assertEquals(UserResponse.getEmail(), user.getEmail());
        assertEquals(UserResponse.getRole(), user.getRole());
        assertEquals(UserResponse.getSex(), user.getSex());
        assertEquals(UserResponse.getProfile_url(), user.getProfile_url());
    }

    @Test
    public void AddUser (){
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

        // Perform the register operation
        userService.addUser(registerRequest);

        // Verify the userRepository and passwordEncoder interactions
        verify(userRepository).findByEmail(anyString());
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    public void UpdateByUser(){
        UserRequest userRequest = UserRequest.builder()
                .firstname("Diego")
                .lastname("Peña")
                .email("updateusertest@example.com")
                .build();

        User user = User.builder()
                .id(1)
                .firstname("Diego")
                .lastname("Peña")
                .email("updateusertest@example.com")
                .build();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(authenticationService.getUserAuthorize()).thenReturn(user);
        userService.updateUserByUser(1, userRequest);
        verify(userRepository).findById(1);
    }

    @Test
    public void UpdateUser(){
        UserRequest userRequest = UserRequest.builder()
                .firstname("Diego")
                .lastname("Peña")
                .email("updateusertest@example.com")
                .build();

        User user = User.builder()
                .firstname("Diego")
                .lastname("Peña")
                .email("updateusertest@example.com")
                .build();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        userService.updateUser(1, userRequest);

        verify(userRepository).findById(1);
    }

    @Test
    public void DeleteUser (){
        User user = User.builder()
                .id(1)
                .firstname("Diego")
                .lastname("Peña")
                .email("delteusertest@example.com")
                .build();

        // Mock the UserRepository behavior
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when (doctorRepository.findByUserEmail(anyString())).thenReturn(Optional.empty());
        doNothing().when(userRepository).deleteById(eq(user.getId()));

        userService.deleteUser(user.getId());
        verify(userRepository).deleteById(user.getId());
    }

}
