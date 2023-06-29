package com.medicapp.server.messages.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicapp.server.messages.model.Message;
import com.medicapp.server.messages.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final MessageService messageService;

    @MessageMapping("/private-message")
    public void recMessage(@Payload byte[] messageBytes) {
        // Convert the byte array to a String
        String messageJson = new String(messageBytes, StandardCharsets.UTF_8);

        // Convert the JSON string to a Message object
        ObjectMapper objectMapper = new ObjectMapper();
        Message message;
        try {
            message = objectMapper.readValue(messageJson, Message.class);
        } catch (JsonProcessingException e) {
            return;
        }
        // Process the received message
        messageService.processPrivateMessage(message);
    }
}
