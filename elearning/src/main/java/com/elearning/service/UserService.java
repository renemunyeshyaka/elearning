package com.elearning.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.elearning.dto.UserRegistrationDto;
import com.elearning.entity.User;
import com.elearning.enums.Role;
import com.elearning.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register user with email activation
    public void registerUser(UserRegistrationDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.USER);
        user.setEnabled(false);
        user.setActivationCode(UUID.randomUUID().toString());
        userRepository.save(user);
        emailService.sendActivationEmail(user);
    }

    // Activate account
    public boolean activateUser(String code) {
        Optional<User> userOpt = userRepository.findByActivationCode(code);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEnabled(true);
            user.setActivationCode(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Generate OTP
    public void generateAndSendOtp(User user) {
        String otp = String.valueOf(new Random().nextInt(899999) + 100000);
        user.setOtpCode(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    // Verify OTP
    public boolean verifyOtp(String email, String otp) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getOtpCode().equals(otp) && user.getOtpExpiry().isAfter(LocalDateTime.now())) {
                user.setOtpCode(null);
                user.setOtpExpiry(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
}