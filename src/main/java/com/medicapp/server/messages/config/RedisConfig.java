package com.medicapp.server.messages.config;

import com.medicapp.server.messages.model.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Message> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Message> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean
    public ZSetOperations<String, Message> zSetOperations(RedisTemplate<String, Message> redisTemplate) {
        return redisTemplate.opsForZSet();
    }
}
