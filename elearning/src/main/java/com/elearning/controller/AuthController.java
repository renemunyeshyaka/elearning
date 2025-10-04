package com.elearning.controller;

import com.elearning.dto.*;
import com.elearning.enums.UserRole;
import com.elearning.service.AuthService;
import com.elearning.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    // Error codes
    private static final String ERR_VALIDATION = "ERR_400";
    private static final String ERR_UNAUTHORIZED = "ERR_401";
    private static final String ERR_FORBIDDEN = "ERR_403";
    private static final String ERR_NOT_FOUND = "ERR_404";
    private static final String ERR_CONFLICT = "ERR_409";
    private static final String ERR_SERVER = "ERR_500";

    // Registration Endpoints
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDTO registrationDTO) {
        try {
            if (registrationDTO.getEmail() == null || registrationDTO.getPassword() == null) {
                return ResponseEntity.badRequest().body(createErrorResponse(
                    ERR_VALIDATION, "Email and password are required", HttpStatus.BAD_REQUEST
                ));
            }

            Map<String, Object> result = authService.registerUser(
                registrationDTO.getEmail(),
                registrationDTO.getPassword(),
                registrationDTO.getFirstName(),
                registrationDTO.getLastName(),
                registrationDTO.getPhoneNumber(),
                registrationDTO.getRole() != null ? registrationDTO.getRole() : UserRole.ROLE_STUDENT
            );

            if (Boolean.TRUE.equals(result.get("success"))) {
                return ResponseEntity.ok(createSuccessResponse("Registration initiated successfully", result));
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse(
                    ERR_VALIDATION, (String) result.get("message"), HttpStatus.BAD_REQUEST
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(ERR_SERVER, "Registration failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/verify-registration")
    public ResponseEntity<?> verifyRegistration(@RequestBody VerifyRequest request) {
        try {
            if (request.getEmail() == null || request.getOtp() == null) {
                return ResponseEntity.badRequest().body(createErrorResponse(
                    ERR_VALIDATION, "Email and OTP are required", HttpStatus.BAD_REQUEST
                ));
            }

            Map<String, Object> result = authService.verifyRegistration(request.getEmail(), request.getOtp());
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ResponseEntity.ok(createSuccessResponse("Registration verified successfully", result));
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse(
                    ERR_VALIDATION, (String) result.get("message"), HttpStatus.BAD_REQUEST
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(ERR_SERVER, "Verification failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // Login Endpoints
    @PostMapping("/login/initiate")
    public ResponseEntity<?> initiateLogin(@RequestBody UserLoginDTO loginDTO) {
        try {
            if (loginDTO.getEmail() == null) {
                return ResponseEntity.badRequest().body(createErrorResponse(
                    ERR_VALIDATION, "Email is required", HttpStatus.BAD_REQUEST
                ));
            }

            Map<String, Object> result = authService.initiateLogin(loginDTO.getEmail());
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ResponseEntity.ok(createSuccessResponse("Login OTP sent successfully", result));
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse(
                    ERR_VALIDATION, (String) result.get("message"), HttpStatus.BAD_REQUEST
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(ERR_SERVER, "Login initiation failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/login/verify")
    public ResponseEntity<?> loginWithOtp(@RequestBody VerifyRequest request) {
        try {
            if (request.getEmail() == null || request.getOtp() == null) {
                return ResponseEntity.badRequest().body(createErrorResponse(
                    ERR_VALIDATION, "Email and OTP are required", HttpStatus.BAD_REQUEST
                ));
            }

            Map<String, Object> result = authService.loginWithOtp(request.getEmail(), request.getOtp());
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ResponseEntity.ok(createSuccessResponse("Login successful", result));
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse(
                    ERR_UNAUTHORIZED, (String) result.get("message"), HttpStatus.UNAUTHORIZED
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(ERR_SERVER, "Login failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // Password-based login
    @PostMapping("/login/password")
    public ResponseEntity<?> loginWithPassword(@RequestBody UserLoginDTO loginDTO) {
        try {
            if (loginDTO.getEmail() == null || loginDTO.getPassword() == null) {
                return ResponseEntity.badRequest().body(createErrorResponse(
                    ERR_VALIDATION, "Email and password are required", HttpStatus.BAD_REQUEST
                ));
            }

            Map<String, Object> result = authService.loginWithPassword(loginDTO.getEmail(), loginDTO.getPassword());
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ResponseEntity.ok(createSuccessResponse("Login successful", result));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorResponse(
                    ERR_UNAUTHORIZED, (String) result.get("message"), HttpStatus.UNAUTHORIZED
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(ERR_SERVER, "Login failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // Password Management Endpoints
    @PostMapping("/password/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            if (request.getEmail() == null) {
                return ResponseEntity.badRequest().body(createErrorResponse(
                    ERR_VALIDATION, "Email is required", HttpStatus.BAD_REQUEST
                ));
            }

            Map<String, Object> result = authService.initiatePasswordReset(request.getEmail());
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ResponseEntity.ok(createSuccessResponse("Password reset OTP sent successfully", result));
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse(
                    ERR_VALIDATION, (String) result.get("message"), HttpStatus.BAD_REQUEST
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(ERR_SERVER, "Password reset initiation failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            if (request.getEmail() == null || request.getOtp() == null || request.getNewPassword() == null) {
                return ResponseEntity.badRequest().body(createErrorResponse(
                    ERR_VALIDATION, "Email, OTP and new password are required", HttpStatus.BAD_REQUEST
                ));
            }

            Map<String, Object> result = authService.resetPassword(
                request.getEmail(), 
                request.getOtp(), 
                request.getNewPassword()
            );
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ResponseEntity.ok(createSuccessResponse("Password reset successfully", result));
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse(
                    ERR_VALIDATION, (String) result.get("message"), HttpStatus.BAD_REQUEST
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(ERR_SERVER, "Password reset failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "Authentication Service");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }

    // Email availability check
    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkEmailAvailability(@PathVariable String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse(
                    ERR_VALIDATION, "Email parameter is required", HttpStatus.BAD_REQUEST
                ));
            }

            boolean exists = authService.userExists(email);
            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("available", !exists);
            response.put("message", exists ? "Email already registered" : "Email available");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(ERR_SERVER, "Email check failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // Helper Methods
    private Map<String, Object> createSuccessResponse(String message, Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("timestamp", java.time.LocalDateTime.now());
        if (data != null) {
            response.putAll(data);
        }
        return response;
    }

    private Map<String, Object> createErrorResponse(String errorCode, String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", errorCode);
        response.put("message", message);
        response.put("timestamp", java.time.LocalDateTime.now());
        response.put("status", status.value());
        return response;
    }

    // Request DTO classes (inner classes)
    public static class VerifyRequest {
        private String email;
        private String otp;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getOtp() { return otp; }
        public void setOtp(String otp) { this.otp = otp; }
    }

    public static class ForgotPasswordRequest {
        private String email;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class ResetPasswordRequest {
        private String email;
        private String otp;
        private String newPassword;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getOtp() { return otp; }
        public void setOtp(String otp) { this.otp = otp; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}