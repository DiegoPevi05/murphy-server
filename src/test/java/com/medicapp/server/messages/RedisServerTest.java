package com.medicapp.server.messages;
import com.medicapp.server.messages.model.Message;
import com.medicapp.server.messages.model.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ZSetOperations;


import java.util.Set;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RedisServerTest {
    @Autowired
    private ZSetOperations<String, Message> zSetOperations;

    @Test
    public void testConnectionAndStoreInRedis() {
        // Create a sample message
        Message message = new Message();
        message.setSender_id("sender123");
        message.setReceiver_id("receiver456");
        message.setMessage("Hello!");
        message.setStatus(Status.MESSAGE);
        message.setDate("2021-05-01");
        message.setTimestamp(1687790365000L);

        // Store the message in Redis using zSetOperations
        zSetOperations.add("messages", message, message.getTimestamp());

        // Retrieve the message from Redis using zSetOperations
        Set<Message> retrievedMessages = zSetOperations.range("messages", 0, -1);

        // Assert that the retrieved message matches the original message
        assertFalse(retrievedMessages.isEmpty());
        Message retrievedMessage = retrievedMessages.iterator().next();
        assertEquals("sender123", retrievedMessage.getSender_id());
        assertEquals("receiver456", retrievedMessage.getReceiver_id());
        assertEquals("Hello!", retrievedMessage.getMessage());
        assertEquals("2021-05-01", retrievedMessage.getDate());
        assertEquals(1687790365000L, retrievedMessage.getTimestamp());
        assertEquals(Status.MESSAGE, retrievedMessage.getStatus());

        // Delete the stored message
        zSetOperations.remove("messages", retrievedMessage);

        // Assert that the message is no longer present in Redis
        retrievedMessages = zSetOperations.range("messages", 0, -1);
        assertTrue(retrievedMessages.isEmpty());
    }
}
