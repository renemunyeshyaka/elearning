package com.elearning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.elearning.entity.User;

@Service
public class EmailService {
	
	// Example of sending a simple email
    public void sendSimpleMessage(String to, String subject, String text) {
        // Use JavaMailSender or your preferred email API
        SimpleMailMessage message = new SimpleMailMessage(); 
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    @Autowired
    private JavaMailSender mailSender;
    
    

    public void sendActivationEmail(User user) {
        String subject = "Activate your account";
        String activationLink = "http://yourdomain.com/activate?code=" + user.getActivationCode();
        String message = "Click the link to activate your account: " + activationLink;
        sendEmail(user.getEmail(), subject, message);
    }

    public void sendOtpEmail(String email, String otp) {
        String subject = "Your OTP Code";
        String message = "Your OTP code is: " + otp;
        sendEmail(email, subject, message);
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
    
    
}
