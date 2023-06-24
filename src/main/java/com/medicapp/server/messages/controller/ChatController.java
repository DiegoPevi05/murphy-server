package com.medicapp.server.messages.controller;


import com.medicapp.server.messages.model.Message;
import com.medicapp.server.messages.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final MessageService messageService;

    @MessageMapping("/private-message")
    public void recMessage(@Payload Message message) {
        messageService.processPrivateMessage(message);
    }
}
