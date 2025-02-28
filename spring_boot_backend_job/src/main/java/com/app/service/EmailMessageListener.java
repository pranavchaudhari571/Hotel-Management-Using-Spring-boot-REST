package com.app.service;

import com.app.dto.EmailMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Service;
@Service("emailMessageListenerService")
@XSlf4j
public class EmailMessageListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Autowired
    public EmailMessageListener(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "reservation-email-topic", groupId = "your-consumer-group-id")
    public void listen(String message) {
        try {
            // Convert JSON string to EmailMessage object
            EmailMessage emailMessage = objectMapper.readValue(message, EmailMessage.class);

            // Log and process the email message
            log.info("Email to: {} with subject: {}", emailMessage.getEmail(), emailMessage.getSubject());
            log.debug("Email body: {}", emailMessage.getBody());

            // Send the email
            notificationService.sendHtmlEmail(emailMessage.getEmail(), emailMessage.getSubject(), emailMessage.getBody());

            log.info("Email successfully sent to: {}", emailMessage.getEmail());
        } catch (JsonProcessingException e) {
            log.error("Error parsing Kafka message: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", e.getMessage());
            // Optionally send to a dead letter queue or handle retries
        }
    }

    @KafkaListener(topics = "reservation-cancellation-topic", groupId = "your-consumer-group-id")
    public void listenCancellation(String message) {
        try {
            // Convert JSON string to EmailMessage object
            EmailMessage emailMessage = objectMapper.readValue(message, EmailMessage.class);

            // Log and process the cancellation message
            log.info("Processing cancellation for email: {} with subject: {}", emailMessage.getEmail(), emailMessage.getSubject());
            log.debug("Cancellation details: {}", emailMessage.getBody());

            // Optionally, perform actions like notifying users about cancellation
            // Send the cancellation confirmation email
            notificationService.sendHtmlEmail(emailMessage.getEmail(), emailMessage.getSubject(), emailMessage.getBody());

            log.info("Cancellation email successfully sent to: {}", emailMessage.getEmail());
        } catch (JsonProcessingException e) {
            log.error("Error parsing Kafka message for cancellation: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing cancellation Kafka message: {}", e.getMessage());
            // Optionally send to a dead letter queue or handle retries
        }
    }

}