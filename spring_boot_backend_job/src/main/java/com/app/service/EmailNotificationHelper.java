package com.app.service;

import com.app.dto.EmailMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@XSlf4j
public class EmailNotificationHelper {
    private final ObjectMapper objectMapper;

    @Autowired
    public EmailNotificationHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void sendEmailMessage(KafkaTemplate<String, String> kafkaTemplate, String topic, EmailMessage emailMessage) {
        try {
            String emailJson = objectMapper.writeValueAsString(emailMessage);
            kafkaTemplate.send(topic, emailJson);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize email message to JSON: {}", e.getMessage());
            // Optionally, handle retries or fallback logic
        }
    }
}
