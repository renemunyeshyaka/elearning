package com.elearning.service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private Map<String, String> otpStorage = new ConcurrentHashMap<>();

    @Autowired
    private EmailService emailService; // or SMS Service

    public String generateOtp(String email) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit OTP
        otpStorage.put(email, otp);
        sendOtp(email, otp);
        return otp;
    }

    private void sendOtp(String email, String otp) {
        String subject = "Your OTP Code";
        String message = "Your OTP is: " + otp;
        emailService.sendSimpleMessage(email, subject, message);
    }

    public boolean verifyOtp(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStorage.remove(email);
            return true;
        }
        return false;
    }
}