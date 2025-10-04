package com.elearning.service;

import com.elearning.dto.OtpDTO;

public interface OtpService {
    String generateOtp(String email);
    boolean verifyOtp(String email, String otp);
    boolean validateOtp(String email, String otp);
    void deleteOtp(String email);
    OtpDTO getOtpByEmail(String email);
    void cleanupExpiredOtps();
    Long getOtpCountByEmail(String email);
}