package com.medicapp.server.messages.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private String sender_id;
    private String receiver_id;
    private String message;
    private String status;
    private LocalDateTime date;
}
