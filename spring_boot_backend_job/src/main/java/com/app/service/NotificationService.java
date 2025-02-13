package com.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendHtmlEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);  // `true` enables multipart (for HTML content)

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);  // `true` ensures the body is treated as HTML content

        try {
            emailSender.send(message);
        } catch (MailException e) {
            // Handle the error appropriately (e.g., log it, rethrow, etc.)
            throw new MessagingException("Error sending email", e);
        }
    }
}
