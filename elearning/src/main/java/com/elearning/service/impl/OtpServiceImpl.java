package com.elearning.service.impl;

import com.elearning.dto.OtpDTO;
import com.elearning.entity.Otp;
import com.elearning.repository.OtpRepository;
import com.elearning.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private OtpRepository otpRepository;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_VALIDITY_MINUTES = 5;

    @Override
    public String generateOtp(String email) {
        // Generate random OTP
        String otpCode = generateRandomOtp();
        
        // Delete any existing OTP for this email
        Optional<Otp> existingOtp = otpRepository.findByEmail(email);
        existingOtp.ifPresent(otp -> otpRepository.delete(otp));
        
        // Create new OTP
        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setOtpCode(otpCode);
        otp.setExpiryDate(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));
        otp.setUsed(false);
        
        otpRepository.save(otp);
        
        return otpCode;
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        Optional<Otp> otpEntityOpt = otpRepository.findByEmailAndOtpCode(email, otp);
        
        if (otpEntityOpt.isEmpty()) {
            return false;
        }
        
        Otp otpEntity = otpEntityOpt.get();
        
        // Check if OTP is expired
        if (otpEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otpEntity);
            return false;
        }
        
        // Check if OTP is already used
        if (otpEntity.getUsed()) {
            return false;
        }
        
        // Mark OTP as used
        otpEntity.setUsed(true);
        otpRepository.save(otpEntity);
        
        return true;
    }

    @Override
    public boolean validateOtp(String email, String otp) {
        Optional<Otp> otpEntityOpt = otpRepository.findByEmailAndOtpCode(email, otp);
        
        if (otpEntityOpt.isEmpty()) {
            return false;
        }
        
        Otp otpEntity = otpEntityOpt.get();
        
        // Check if OTP is expired
        if (otpEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }
        
        // Check if OTP is already used
        return !otpEntity.getUsed();
    }

    @Override
    public void deleteOtp(String email) {
        Optional<Otp> otpOpt = otpRepository.findByEmail(email);
        otpOpt.ifPresent(otp -> otpRepository.delete(otp));
    }

    @Override
    public OtpDTO getOtpByEmail(String email) {
        Optional<Otp> otpOpt = otpRepository.findByEmail(email);
        
        if (otpOpt.isEmpty()) {
            return null;
        }
        
        Otp otp = otpOpt.get();
        OtpDTO dto = new OtpDTO();
        dto.setId(otp.getId());
        dto.setEmail(otp.getEmail());
        dto.setOtpCode(otp.getOtpCode());
        dto.setExpiryTime(otp.getExpiryDate());
        dto.setUsed(otp.getUsed());
        dto.setCreatedAt(otp.getCreatedAt());
        
        return dto;
    }

    @Override
    public void cleanupExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        Iterable<Otp> allOtps = otpRepository.findAll();
        
        for (Otp otp : allOtps) {
            if (otp.getExpiryDate().isBefore(now)) {
                otpRepository.delete(otp);
            }
        }
    }

    @Override
    public Long getOtpCountByEmail(String email) {
        return otpRepository.countByEmail(email);
    }

    private String generateRandomOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        
        return otp.toString();
    }
}