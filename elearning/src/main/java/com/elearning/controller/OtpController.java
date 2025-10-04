package com.elearning.controller;

import com.elearning.dto.OtpDTO;
import com.elearning.dto.OtpVerificationDTO;
import com.elearning.service.OtpService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/otp")
@CrossOrigin(origins = "*")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateOtp(@Valid @RequestBody OtpDTO otpDTO) {
        String otp = otpService.generateOtp(otpDTO.getEmail());
        return ResponseEntity.ok("OTP generated and sent successfully");
    }

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verifyOtp(@Valid @RequestBody OtpVerificationDTO otpVerificationDTO) {
        boolean isValid = otpService.verifyOtp(otpVerificationDTO.getEmail(), otpVerificationDTO.getOtp());
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateOtp(@Valid @RequestBody OtpVerificationDTO otpVerificationDTO) {
        boolean isValid = otpService.validateOtp(otpVerificationDTO.getEmail(), otpVerificationDTO.getOtp());
        return ResponseEntity.ok(isValid);
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteOtp(@PathVariable String email) {
        otpService.deleteOtp(email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{email}")
    public ResponseEntity<OtpDTO> getOtpByEmail(@PathVariable String email) {
        OtpDTO otpDTO = otpService.getOtpByEmail(email);
        return ResponseEntity.ok(otpDTO);
    }
    
    @PostMapping("/cleanup")
    public ResponseEntity<String> cleanupExpiredOtps() {
        otpService.cleanupExpiredOtps();
        return ResponseEntity.ok("Expired OTPs cleaned up successfully");
    }

    @GetMapping("/count/{email}")
    public ResponseEntity<Map<String, Long>> getOtpCountByEmail(@PathVariable String email) {
        Long count = otpService.getOtpCountByEmail(email);
        return ResponseEntity.ok(Map.of("otpCount", count));
    }
    
}