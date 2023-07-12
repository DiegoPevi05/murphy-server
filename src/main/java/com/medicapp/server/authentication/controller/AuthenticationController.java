package com.medicapp.server.authentication.controller;

import com.medicapp.server.authentication.dto.AuthenticationRequest;
import com.medicapp.server.authentication.dto.AuthenticationResponse;
import com.medicapp.server.authentication.dto.RegisterRequest;
import com.medicapp.server.authentication.service.AuthenticationService;
import com.medicapp.server.authentication.service.AuthenticationServiceOAuth2;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    private final AuthenticationServiceOAuth2 authenticationServiceOAuth2;

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequest request
    ) throws MessagingException {
        return ResponseEntity.ok(authenticationService.register(request));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/oauth2/{provider}")
    public ResponseEntity<AuthenticationResponse> authenticateOauth2(
            @PathVariable("provider") String provider,
            @RequestParam("accessToken") String accessToken
    ) {
        AuthenticationResponse response = authenticationServiceOAuth2.authenticateOAuth2(provider, accessToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> authenticate(@RequestParam(value = "token", defaultValue = "") String token) {
        return ResponseEntity.ok(authenticationService.confirmToken(token));
    }

    @PostMapping(value="/reset-password/code")
    public ResponseEntity<String> GenerateResetPassword(@RequestParam String email) throws MessagingException {
        authenticationService.GenerateRecoverPasswordCode(email);
        return ResponseEntity.status(HttpStatus.OK).body("Reset Code has send to your account.");
    }
    @PostMapping(value="/validate-code")
    public ResponseEntity<String> ValidateResetPasswordCode(@RequestParam String email,
                                                            @RequestParam String recoverCode) {
        authenticationService.ValidateRecoverCode(email,recoverCode);
        return ResponseEntity.status(HttpStatus.OK).body("Reset Code is valid.");
    }
    @PostMapping(value="/reset-password/reset")
    public ResponseEntity<String> ResetPassword(@RequestParam String email,
                                                @RequestParam String recoverCode,
                                                @RequestParam String password) {
        authenticationService.ResetPasswordUser(email,recoverCode,password);
        return ResponseEntity.status(HttpStatus.OK).body("Password has been reset.");
    }
}
