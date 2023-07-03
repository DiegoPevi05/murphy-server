package com.medicapp.server.authentication.service;

import com.medicapp.server.authentication.dto.AuthenticationRequest;
import com.medicapp.server.authentication.dto.AuthenticationResponse;
import com.medicapp.server.authentication.dto.RegisterRequest;
import com.medicapp.server.authentication.model.*;
import com.medicapp.server.authentication.repository.TokenRepository;
import com.medicapp.server.authentication.repository.UserRepository;
import com.medicapp.server.config.ExceptionHandlerConfig;
import com.medicapp.server.doctors.model.Doctor;
import com.medicapp.server.doctors.repository.DoctorRepository;
import com.medicapp.server.email.EmailService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private HttpServletRequest request;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    private final DoctorRepository doctorRepository;

    private final ConfirmationTokenService confirmationTokenService;

    private final EmailService emailService;
    @Value("${server.host}")
    private String SERVER_HOST;

    @Value("${server.port}")
    private String SERVER_PORT;

    @Value("${default.image.profile_user_male}")
    private String male_profile_image;

    @Value("${default.image.profile_user_female}")
    private String female_profile_image;

    public String register(RegisterRequest request) throws MessagingException {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new ExceptionHandlerConfig.ResourceNotFoundException(
                            "User with this email "+request.getEmail()+ " already exists");
                });

        String defaultProfileImage = "";
        if(!request.getSex().isEmpty() && request.getSex().equals("male")){
            defaultProfileImage = male_profile_image;
        }else if(!request.getSex().isEmpty() && request.getSex().equals("female")){
            defaultProfileImage = female_profile_image;
        }else{
            defaultProfileImage = male_profile_image;
        }

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .sex(request.getSex())
                .enabled(false)
                .profile_url(defaultProfileImage)
                .build();
        var savedUser = userRepository.save(user);
        String ConfirmationTokenString = UUID.randomUUID().toString();

        String linkActivation = "http://"+SERVER_HOST+":"+SERVER_PORT+"/api/v1/public/auth/confirm?token=" + ConfirmationTokenString;

        emailService.send(request.getEmail(),
                "Activation Account",
                "Activate your Account",
                 request.getFirstname(),
                null,
                linkActivation,
                "register");

        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(ConfirmationTokenString)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(savedUser)
                .build();

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        return "Confirmation Email has been sent to "+request.getEmail()+".";
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("Token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired please request a new one");
        }

        confirmationTokenService.setConfirmedAt(token);
        User user = userRepository.findByEmailDisable(confirmationToken.getUser().getEmail())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        return "confirmed";
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
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

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private UserDetails getAuthenticatedUserDetail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails;
    }

    private boolean isUser(UserDetails userDetails){
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if(Objects.equals(role,"ROLE_USER")){
                return true;
            }
        }
        return false;
    }

    private boolean isDoctor(UserDetails userDetails){
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if(Objects.equals(role,"ROLE_DOCTOR")){
                return true;
            }
        }
        return false;
    }

    public User getUserAuthorize(){
        UserDetails userDetails = getAuthenticatedUserDetail();
        if(!isUser(userDetails)){
            throw new IllegalArgumentException("Is not a user account");
        }
        String userEmail = userDetails.getUsername();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException(
                        "Users with this email "+userEmail+ " does not exist"
                ));
    }

    public Doctor getDoctorAuthorize(){
        UserDetails userDetails = getAuthenticatedUserDetail();
        if(!isDoctor(userDetails)){
            throw new IllegalArgumentException("Is not a doctor account");
        }
        String doctorEmail = userDetails.getUsername();
        return doctorRepository.findByUserEmail(doctorEmail)
                .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException(
                        "Doctors with this email "+doctorEmail+ " does not exist"
                ));
    }
}
