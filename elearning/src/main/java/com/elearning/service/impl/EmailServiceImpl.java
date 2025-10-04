package com.elearning.service.impl;

import com.elearning.dto.EmailDTO;
import com.elearning.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public void sendEmail(EmailDTO emailDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDTO.getTo());
        
        if (emailDTO.getCc() != null && !emailDTO.getCc().isEmpty()) {
            message.setCc(emailDTO.getCc());
        }
        
        message.setSubject(emailDTO.getSubject());
        message.setText(emailDTO.getContent());
        
        mailSender.send(message);
    }

    @Override
    public void sendHtmlEmail(EmailDTO emailDTO) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(emailDTO.getTo());
            
            if (emailDTO.getCc() != null && !emailDTO.getCc().isEmpty()) {
                helper.setCc(emailDTO.getCc());
            }
            
            helper.setSubject(emailDTO.getSubject());
            helper.setText(emailDTO.getContent(), true); // true indicates HTML
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }

    @Override
    public void sendTemplateEmail(EmailDTO emailDTO) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(emailDTO.getTo());
            helper.setSubject(emailDTO.getSubject());
            
            // Create Thymeleaf context and set variables
            Context context = new Context();
            context.setVariable("content", emailDTO.getContent());
            context.setVariable("subject", emailDTO.getSubject());
            
            // Process the template (you can create actual Thymeleaf templates)
            String htmlContent = templateEngine.process("email-template", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send template email", e);
        }
    }

    @Override
    public void sendBulkEmail(EmailDTO emailDTO) {
        // For bulk emails, you might want to split the recipients
        String[] recipients = emailDTO.getTo().split(",");
        
        for (String recipient : recipients) {
            EmailDTO individualEmail = new EmailDTO();
            individualEmail.setTo(recipient.trim());
            individualEmail.setSubject(emailDTO.getSubject());
            individualEmail.setContent(emailDTO.getContent());
            
            sendEmail(individualEmail);
        }
    }
}