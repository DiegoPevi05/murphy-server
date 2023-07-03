package com.medicapp.server.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicapp.server.authentication.model.Role;
import com.medicapp.server.authentication.model.Token;
import com.medicapp.server.authentication.model.TokenType;
import com.medicapp.server.authentication.model.User;
import com.medicapp.server.authentication.repository.TokenRepository;
import com.medicapp.server.authentication.repository.UserRepository;
import com.medicapp.server.authentication.service.JwtService;
import com.medicapp.server.messages.dto.MessageResponse;
import com.medicapp.server.messages.dto.MessageRequest;
import com.medicapp.server.messages.model.Status;
import com.medicapp.server.messages.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.socket.messaging.WebSocketStompClient;


import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
public class ChatControllerTest {

    @Autowired
    private MessageService messageService;
    @Autowired
    private WebSocketStompClient stompClient;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    @Test
    public void testSendMessage() throws Exception {
        // Create User 1
        User user1 = User.builder()
                .firstname("User1")
                .lastname("User1")
                .email("user1@example.com")
                .password(passwordEncoder.encode("password1"))
                .role(Role.ROLE_USER)
                .sex("male")
                .enabled(true)
                .profile_url("example.com")
                .build();
        // Set other properties as needed
        userRepository.save(user1);

        //Get Token
        var jwtToken1 = jwtService.generateToken(user1);
        revokeAllUserTokens(user1);
        saveUserToken(user1, jwtToken1);

        // Create User 1
        User user2 = User.builder()
                .firstname("User2")
                .lastname("User2")
                .email("user2@example.com")
                .password(passwordEncoder.encode("password2"))
                .role(Role.ROLE_USER)
                .sex("male")
                .enabled(true)
                .profile_url("example.com")
                .build();
        // Set other properties as needed
        userRepository.save(user2);

        //Get Token user 2
        var jwtToken2 = jwtService.generateToken(user2);
        revokeAllUserTokens(user2);
        saveUserToken(user2, jwtToken2);


        // Establish WebSocket connection for User 1
        StompHeaders headers1 = new StompHeaders();
        headers1.add("Authorization", jwtToken1);
        StompSession session1 = stompClient.connectAsync("ws://localhost:8080/ws", new StompSessionHandlerAdapter() {}).get();

        // Establish WebSocket connection for User 2
        StompHeaders headers2 = new StompHeaders();
        headers2.add("Authorization", jwtToken2);
        StompSession session2 = stompClient.connectAsync("ws://localhost:8080/ws", new StompSessionHandlerAdapter() {}).get();

        // Prepare the message payload
        String messagePayload = "Hello, User 2!";
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setMessage(messagePayload);
        messageRequest.setReceiver_id(user2.getId().toString());
        messageRequest.setSender_id(user1.getId().toString());
        messageRequest.setStatus(Status.MESSAGE.toString());

        // Convert the message object to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String messageJson = objectMapper.writeValueAsString(messageRequest);
        // Convert the messageJson string to a byte array
        byte[] messageBytes = messageJson.getBytes(StandardCharsets.UTF_8);

        // Send the message from User 1 to User 2
        session1.send("/app/private-message", messageBytes);

        // Wait for a response or perform assertions based on the behavior of your application

        // Sleep for a while to allow processing and storing the message in Redis
        Thread.sleep(1000); // Adjust the duration as needed

        // Retrieve messages from Redis
        Page<MessageResponse> messageResponses= messageService.retrieveMessageFromDB(0,10,user1.getId().toString(), user2.getId().toString());

        // Assert that the message is stored in Redis
        assertNotNull(messageResponses);
        assertEquals(1, messageResponses.getTotalElements());
        System.out.println("Number of messages " +messageResponses.getTotalElements());
        //Message storedMessage = messages.iterator().next();
        //assertEquals(messagePayload, storedMessage.getMessage());

        // Close the WebSocket sessions
        //session1.disconnect();
        //session2.disconnect();
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
}
