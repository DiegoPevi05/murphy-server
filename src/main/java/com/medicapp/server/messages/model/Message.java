package com.medicapp.server.messages.model;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String sender_id;
    private String receiver_id;
    private String message;
    private String date;
    private Status status;
    private long timestamp;

}
