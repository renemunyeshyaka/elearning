package com.elearning.controller;

import com.elearning.dto.EmailDTO;
import com.elearning.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@Valid @RequestBody EmailDTO emailDTO) {
        emailService.sendEmail(emailDTO);
        return ResponseEntity.ok("Email sent successfully");
    }

    @PostMapping("/send-html")
    public ResponseEntity<String> sendHtmlEmail(@Valid @RequestBody EmailDTO emailDTO) {
        emailService.sendHtmlEmail(emailDTO);
        return ResponseEntity.ok("HTML email sent successfully");
    }

    @PostMapping("/send-template")
    public ResponseEntity<String> sendTemplateEmail(@Valid @RequestBody EmailDTO emailDTO) {
        emailService.sendTemplateEmail(emailDTO);
        return ResponseEntity.ok("Template email sent successfully");
    }

    @PostMapping("/send-bulk")
    public ResponseEntity<String> sendBulkEmail(@Valid @RequestBody EmailDTO emailDTO) {
        emailService.sendBulkEmail(emailDTO);
        return ResponseEntity.ok("Bulk email sent successfully");
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEmail() {
        EmailDTO testEmail = new EmailDTO();
        testEmail.setTo("test@example.com");
        testEmail.setSubject("Test Email");
        testEmail.setContent("This is a test email from eLearning system");
        
        emailService.sendEmail(testEmail);
        return ResponseEntity.ok("Test email sent successfully");
    }
}