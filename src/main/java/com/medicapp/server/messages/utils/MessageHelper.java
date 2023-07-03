package com.medicapp.server.messages.utils;

import com.medicapp.server.config.ExceptionHandlerConfig;
import com.medicapp.server.messages.dto.MessageResponse;
import com.medicapp.server.messages.model.Message;
import com.medicapp.server.messages.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageHelper {
    public Status getStatus(String status) {
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ExceptionHandlerConfig.ResourceNotFoundException("Status "+status+" does not exist");
        }
    }

    public MessageResponse mapToMessageResponse(Message message) {
        return MessageResponse.builder()
                .sender_id(message.getSender_id())
                .receiver_id(message.getReceiver_id())
                .message(message.getMessage())
                .status(message.getStatus().toString())
                .date(message.getCreatedDate())
                .build();
    }
}
