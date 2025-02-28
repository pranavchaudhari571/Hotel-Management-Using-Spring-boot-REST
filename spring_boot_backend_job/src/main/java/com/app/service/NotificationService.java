package com.app.service;

import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@XSlf4j
public class NotificationService {

    @Autowired
    private JavaMailSender emailSender;

    /**
     * Sends an HTML email to the recipient.
     * @param to recipient email address
     * @param subject subject of the email
     * @param body the HTML body content of the email
     * @throws MessagingException in case of an error while sending the email
     */
    public void sendHtmlEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);  // `true` enables multipart (for HTML content)

        helper.setTo(to);            // set the recipient
        helper.setSubject(subject);  // set the subject
        helper.setText(body, true);  // treat the body as HTML content

        try {
            emailSender.send(message);
            log.info("Email sent successfully to: " + to);

        } catch (MailException e) {
            // Log the error or rethrow with additional information if needed
            log.error("Error sending email: " + e.getMessage());
            throw new MessagingException("Error sending email to: " + to, e);
        }
    }

    /**
     * Sends an HTML email with optional CC and BCC.
     * @param to recipient email address
     * @param subject subject of the email
     * @param body the HTML body content of the email
     * @param cc optional CC email address(es)
     * @param bcc optional BCC email address(es)
     * @throws MessagingException in case of an error while sending the email
     */
    public void sendHtmlEmailWithOptionalCCandBCC(String to, String subject, String body, String[] cc, String[] bcc) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);  // treat as HTML

        if (cc != null && cc.length > 0) {
            helper.setCc(cc);  // Set CC if provided
        }

        if (bcc != null && bcc.length > 0) {
            helper.setBcc(bcc);  // Set BCC if provided
        }

        try {
            emailSender.send(message);
            log.info("Email sent successfully to: " + to);
        } catch (MailException e) {
         log.error("Error sending email: " + e.getMessage());
            throw new MessagingException("Error sending email with CC and BCC to: " + to, e);
        }
    }
}
