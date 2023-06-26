package com.medicapp.server.messages.service;

import com.medicapp.server.authentication.model.User;
import com.medicapp.server.authentication.repository.UserRepository;
import com.medicapp.server.authentication.service.AuthenticationService;
import com.medicapp.server.config.ExceptionHandlerConfig;
import com.medicapp.server.messages.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final ZSetOperations<String, Message> zSetOperations;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    @Autowired
    private  SimpMessagingTemplate simpMessagingTemplate;

    public void processPrivateMessage(Message message) {
        User user = authenticationService.getUserAuthorize();
        String senderId = user.getId().toString();
        if (!senderId.equals(message.getSender_id())) {
            throw new ExceptionHandlerConfig.ResourceNotFoundException("Sender does not exist");
        }

        boolean receiverExists = userRepository.existsById(Integer.parseInt(message.getReceiver_id()));
        if (!receiverExists) {
            throw new ExceptionHandlerConfig.ResourceNotFoundException("Receiver does not exist");
        }

        String receiverId = message.getReceiver_id();
        // Validate sender and receiver information, perform necessary checks

        saveMessageToRedis(message);
        String privateDestination = "/user/" + receiverId + "/private";
        simpMessagingTemplate.convertAndSend(privateDestination, message);

    }

    private void saveMessageToRedis(Message message) {
        zSetOperations.add("messages", message, message.getTimestamp());
    }

    public List<Message> getMessagesBySenderAndReceiver(String senderID, String receiverID) {
        Set<Message> messages = zSetOperations.rangeByScore("messages", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)
                .stream()
                .filter(message -> message.getSender_id().equals(senderID) && message.getReceiver_id().equals(receiverID))
                .sorted(Comparator.comparingLong(Message::getTimestamp))
                .collect(Collectors.toSet());
        return new ArrayList<>(messages);
    }
}

