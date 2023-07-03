package com.medicapp.server.messages.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicapp.server.authentication.model.User;
import com.medicapp.server.authentication.repository.UserRepository;
import com.medicapp.server.authentication.service.AuthenticationService;
import com.medicapp.server.config.ExceptionHandlerConfig;
import com.medicapp.server.doctors.dto.DoctorResponse;
import com.medicapp.server.doctors.model.Doctor;
import com.medicapp.server.messages.dto.MessageResponse;
import com.medicapp.server.messages.model.Message;
import com.medicapp.server.messages.dto.MessageRequest;
import com.medicapp.server.messages.repository.MessageRepository;
import com.medicapp.server.messages.utils.MessageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final MessageHelper messageHelper;

    @Autowired
    private  SimpMessagingTemplate simpMessagingTemplate;

    public void processPrivateMessage(MessageRequest messageRequest) {
        User user = authenticationService.getUserAuthorize();
        String senderId = user.getId().toString();
        if (!senderId.equals(messageRequest.getSender_id())) {
            throw new ExceptionHandlerConfig.ResourceNotFoundException("Sender does not exist");
        }

        boolean receiverExists = userRepository.existsById(Integer.parseInt(messageRequest.getReceiver_id()));
        if (!receiverExists) {
            throw new ExceptionHandlerConfig.ResourceNotFoundException("Receiver does not exist");
        }

        String receiverId = messageRequest.getReceiver_id();
        // Validate sender and receiver information, perform necessary checks

        saveMessageToDB(messageRequest);
        ObjectMapper objectMapper = new ObjectMapper();
        String messageJson;
        try {
            messageJson = objectMapper.writeValueAsString(messageRequest);
        } catch (JsonProcessingException e) {
            return;
        }
        // Convert the messageJson string to a byte array
        byte[] messageBytes = messageJson.getBytes(StandardCharsets.UTF_8);
        String privateDestination = "/user/" + receiverId + "/private";

        simpMessagingTemplate.convertAndSend(privateDestination, messageBytes);

    }

    private void saveMessageToDB(MessageRequest messageRequest) {
        messageRepository.save(Message.builder()
                .sender_id(messageRequest.getSender_id())
                .receiver_id(messageRequest.getReceiver_id())
                .message(messageRequest.getMessage())
                .status(messageHelper.getStatus(messageRequest.getStatus()))
                .build());

    }

    public Page<MessageResponse> retrieveMessageFromDBByUser(int page, int size, String receiverId) {
        User user = authenticationService.getUserAuthorize();
        if(receiverId == null || receiverId.isEmpty()){
            throw new ExceptionHandlerConfig.ResourceNotFoundException("Need Receiver Id");
        }
        return retrieveMessageFromDB(page,size,user.getId().toString(),receiverId);
    }

    public Page<MessageResponse> retrieveMessageFromDBByDoctor(int page, int size, String receiverId) {
        Doctor doctor = authenticationService.getDoctorAuthorize();
        if(receiverId == null || receiverId.isEmpty()){
            throw new ExceptionHandlerConfig.ResourceNotFoundException("Need Receiver Id");
        }
        return retrieveMessageFromDB(page,size,doctor.getUser().getId().toString(),receiverId);
    }

    public Page<MessageResponse> retrieveMessageFromDB(int page, int size,String senderId, String receiverId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messagePage = messageRepository.findBySenderIdAndReceiverId(senderId,receiverId,pageable);

        List<MessageResponse> messageResponses = messagePage.getContent().stream()
                .map(messageHelper::mapToMessageResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(messageResponses, pageable,messagePage.getTotalElements());
    }
}

