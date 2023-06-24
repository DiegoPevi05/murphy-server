package com.medicapp.server.config;

import com.medicapp.server.authentication.model.Role;
import com.medicapp.server.authentication.model.Token;
import com.medicapp.server.authentication.model.TokenType;
import com.medicapp.server.authentication.model.User;
import com.medicapp.server.authentication.repository.TokenRepository;
import com.medicapp.server.authentication.repository.UserRepository;
import com.medicapp.server.authentication.service.JwtService;
import com.medicapp.server.doctors.model.Doctor;
import com.medicapp.server.doctors.model.Specialty;
import com.medicapp.server.doctors.repository.DoctorRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminConfig {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final DoctorRepository doctorRepository;

    @Value("${credentials.admin.username}")
    private String defaultAdminUsername;
    @Value("${credentials.admin.password}")
    private String defaultAdminPassword;
    @Value("${credentials.admin.email}")
    private String defaultAdminEmail;

    @Value("${default.image.profile_user_male}")
    private String male_profile_image;

    @PostConstruct
    public void createDefaultAdminUser() {
        var user = User.builder()
                .firstname(defaultAdminUsername)
                .lastname(defaultAdminUsername)
                .email(defaultAdminEmail)
                .password(passwordEncoder.encode(defaultAdminPassword))
                .role(Role.ROLE_ADMIN)
                .sex("male")
                .enabled(true)
                .profile_url(male_profile_image)
                .build();
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jwtToken);
    }

    @PostConstruct
    public void createDefaultUser () {
        var user = User.builder()
                .firstname("user")
                .lastname("user")
                .email("diego.pena@utec.edu.pe")
                .password(passwordEncoder.encode("123456"))
                .role(Role.ROLE_USER)
                .sex("male")
                .enabled(true)
                .profile_url(male_profile_image)
                .build();
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jwtToken);
    }

    @PostConstruct
    public void createDefaultDoctor () {
        var user = User.builder()
                .firstname("user")
                .lastname("user")
                .email("diegopevi05@gmail.com")
                .password(passwordEncoder.encode("123456"))
                .role(Role.ROLE_DOCTOR)
                .sex("male")
                .enabled(true)
                .profile_url(male_profile_image)
                .build();
        var savedUser = userRepository.save(user);
        Doctor doctor = Doctor.builder()
                .user(savedUser)
                .specialty(Specialty.CARDIOLOGY)
                .description("Medico general con 5 años de experiencia")
                .details("Medico general con 5 años de experiencia")
                .rating(0F)
                .cost(50F)
                .build();
        doctorRepository.save(doctor);
        var jwtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jwtToken);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
}
