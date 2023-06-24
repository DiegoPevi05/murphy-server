package com.medicapp.server.authentication.service;

import com.medicapp.server.authentication.dto.AuthenticationResponse;
import com.medicapp.server.authentication.dto.UserResponseOauth2;
import com.medicapp.server.authentication.model.Role;
import com.medicapp.server.authentication.model.User;
import com.medicapp.server.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceOAuth2 {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthenticationResponse authenticateOAuth2(String provider, String accessToken) {
        // Retrieve user details from the provider's API using the access token
        UserResponseOauth2 userResponseOauth2 = fetchUserDetailsFromProvider(provider, accessToken);

        // Check if the user already exists in your system
        Optional<User> existingUser = userRepository.findByEmail(userResponseOauth2.getEmail());
        if (existingUser.isPresent()) {
            // User exists, generate a JWT token and return it
            String jwtToken = jwtService.generateToken(existingUser.get());
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } else {
            // User doesn't exist, register the user in your system and generate a JWT token
            User user = User.builder()
                    .firstname(userResponseOauth2.getName())
                    .lastname(" ")
                    .email(userResponseOauth2.getEmail())
                    .sex("male")
                    .profile_url(userResponseOauth2.getPicture())
                    .role(Role.ROLE_USER)
                    .enabled(true)
                    .build();

            user = userRepository.save(user);
            String jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        }
    }

    private UserResponseOauth2 fetchUserDetailsFromProvider(String provider, String accessToken) {
        // Retrieve user details from the provider's API using the access token
        // You'll need to implement provider-specific logic to fetch the necessary user details
        // For example, using the access token, you can make HTTP requests to the provider's API endpoint
        UserResponseOauth2 user = null;
        if (provider.equalsIgnoreCase("google")) {
            // Fetch user details from Google API using the access token
            String userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";

            // Set the authorization header with the access token
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Create a GET request to the user info endpoint
            RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(userInfoEndpoint));

            // Send the request to the user info endpoint
            ResponseEntity<Map<String, Object>> responseEntity = new RestTemplate().exchange(requestEntity, new ParameterizedTypeReference<Map<String, Object>>() {});

            // Check the response status
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                // Extract the necessary user details from the response body
                Map<String, Object> userInfo = responseEntity.getBody();

                // Extract the required user details (e.g., name, email, picture) from the userInfo map
                String name = (String) userInfo.get("name");
                String email = (String) userInfo.get("email");
                String picture = (String) userInfo.get("picture");

                // Create a User object with the retrieved details
                user = UserResponseOauth2.builder()
                        .name(name)
                        .email(email)
                        .picture(picture)
                        .build();
            } else {
                throw new IllegalStateException("Failed to retrieve user details from Google API");
            }
        } else if (provider.equalsIgnoreCase("facebook")) {
            // Fetch user details from Facebook API using the access token
            // Example: Use the Facebook Graph API to fetch user details
            String userInfoEndpoint = "https://graph.facebook.com/v15.0/me?fields=name,email,picture";

            // Set the authorization header with the access token
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Create a GET request to the user info endpoint
            RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(userInfoEndpoint));

            // Send the request to the user info endpoint
            ResponseEntity<Map<String, Object>> responseEntity = new RestTemplate().exchange(requestEntity, new ParameterizedTypeReference<Map<String, Object>>() {});

            // Check the response status
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                // Extract the necessary user details from the response body
                Map<String, Object> userInfo = responseEntity.getBody();

                // Extract the required user details (e.g., name, email, picture) from the userInfo map
                String name = (String) userInfo.get("name");
                String email = (String) userInfo.get("email");
                Map<String, Object> pictureData = (Map<String, Object>) ((Map<String, Object>) userInfo.get("picture")).get("data");
                String pictureUrl = (String) pictureData.get("url");


                // Create a User object with the retrieved details
                user = UserResponseOauth2.builder()
                        .name(name)
                        .email(email)
                        .picture(pictureUrl)
                        .build();

            } else {
                throw new IllegalStateException("Failed to retrieve user details from Facebook API");
            }
        } else {
            throw new IllegalArgumentException("Invalid OAuth2 provider: " + provider);
        }

        // Extract the necessary user details and create a User object
        // Return the User object with the retrieved details
        return user;
    }

}
