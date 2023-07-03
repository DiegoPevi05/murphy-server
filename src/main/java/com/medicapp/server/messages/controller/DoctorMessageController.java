package com.medicapp.server.messages.controller;

import com.medicapp.server.messages.dto.MessageResponse;
import com.medicapp.server.messages.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/doctor/messages")
@RequiredArgsConstructor
public class DoctorMessageController {
    private final MessageService messageService;
    @GetMapping
    public Page<MessageResponse> getDoctors(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "receiverId", defaultValue = "") String receiverId
    ){
        return messageService.retrieveMessageFromDBByDoctor(page,size,receiverId);
    }
}