package com.elearning.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.elearning.service.EmailService;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

@Controller
public class AuthController {

    @Autowired
    private EmailService emailService;

    // In-memory storage for OTPs (use Redis or database in production)
    private Map<String, OtpData> otpStorage = new HashMap<>();
    
    // In-memory storage for password reset tokens
    private Map<String, PasswordResetData> passwordResetStorage = new HashMap<>();
    
    // Simple session storage (use Spring Security in production)
    private Map<String, String> userSessions = new HashMap<>();

    // Home page - redirects to login or dashboard based on session
    @GetMapping("/")
    public String home(@RequestParam(value = "session", required = false) String sessionId) {
        if (sessionId != null && userSessions.containsKey(sessionId)) {
            return "redirect:/dashboard?session=" + sessionId;
        }
        return "index";
    }
    
 // Show login page
    @GetMapping("/home")
    public String showHomePage(@RequestParam(value = "error", required = false) String error, 
                               Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or OTP. Please try again.");
        }
        return "home";
    }

    // Show login page
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error, 
                               Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or OTP. Please try again.");
        }
        return "login";
    }
    
    //@PostMapping("/register")
    public String showRegisterPage(@RequestParam(value = "session", required = false) String error, 
                               Model model) {
        if (error != null) {
            model.addAttribute("error", "Please register yourself again!");
        }
        return "register";
    }
    
    // Show forget password page
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage(@RequestParam(value = "error", required = false) String error,
                                        @RequestParam(value = "success", required = false) String success,
                                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Please enter your valid email address!");
        }
        if (success != null) {
            model.addAttribute("success", "Password reset instructions sent to your email!");
        }
        return "forgot-password";
    }

    // Process forgot password request
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email,
                                       Model model) {
        
        // Basic email validation
        if (email == null || email.trim().isEmpty() || !isValidEmail(email)) {
            model.addAttribute("error", "Please enter a valid email address");
            return "forgot-password";
        }

        // Generate reset token
        String resetToken = generateResetToken();
        
        // Store reset token with timestamp
        passwordResetStorage.put(email, new PasswordResetData(resetToken, System.currentTimeMillis()));
        
        try {
            // Send reset link via email
            String resetLink = "http://localhost:8080/reset-password?token=" + resetToken + "&email=" + email;
            emailService.sendSimpleMessage(email, "Password Reset Request", 
                "To reset your password, please click the link below:\n\n" +
                resetLink + "\n\n" +
                "This link will expire in 30 minutes.\n\n" +
                "If you didn't request a password reset, please ignore this email.");
            
            return "redirect:/forgot-password?success=true";
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to send reset email. Please try again.");
            return "forgot-password";
        }
    }

    // Show reset password page
    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam("token") String token,
                                       @RequestParam("email") String email,
                                       @RequestParam(value = "error", required = false) String error,
                                       Model model) {
        
        PasswordResetData resetData = passwordResetStorage.get(email);
        
        // Check if token exists and is valid
        if (resetData == null || !resetData.getToken().equals(token)) {
            model.addAttribute("error", "Invalid or expired reset token.");
            return "reset-password-error";
        }
        
        // Check if token is expired (30 minutes)
        long currentTime = System.currentTimeMillis();
        if ((currentTime - resetData.getTimestamp()) > 30 * 60 * 1000) {
            passwordResetStorage.remove(email);
            model.addAttribute("error", "Reset token has expired. Please request a new one.");
            return "reset-password-error";
        }
        
        model.addAttribute("token", token);
        model.addAttribute("email", email);
        
        if (error != null) {
            model.addAttribute("error", "Passwords do not match or are invalid.");
        }
        
        return "reset-password";
    }

    // Process password reset
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                      @RequestParam("email") String email,
                                      @RequestParam("newPassword") String newPassword,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      Model model) {
        
        PasswordResetData resetData = passwordResetStorage.get(email);
        
        // Validate token
        if (resetData == null || !resetData.getToken().equals(token)) {
            model.addAttribute("error", "Invalid or expired reset token.");
            return "reset-password-error";
        }
        
        // Check if token is expired (30 minutes)
        long currentTime = System.currentTimeMillis();
        if ((currentTime - resetData.getTimestamp()) > 30 * 60 * 1000) {
            passwordResetStorage.remove(email);
            model.addAttribute("error", "Reset token has expired. Please request a new one.");
            return "reset-password-error";
        }
        
        // Validate passwords
        if (newPassword == null || newPassword.trim().isEmpty()) {
            model.addAttribute("error", "Password cannot be empty.");
            model.addAttribute("token", token);
            model.addAttribute("email", email);
            return "reset-password";
        }
        
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            model.addAttribute("token", token);
            model.addAttribute("email", email);
            return "reset-password";
        }
        
        // TODO: Implement actual password reset logic here
        // This is where you would update the user's password in your database
        // For now, we'll just simulate the reset
        
        try {
            // Remove the used reset token
            passwordResetStorage.remove(email);
            
            // Send confirmation email
            emailService.sendSimpleMessage(email, "Password Reset Successful", 
                "Your password has been successfully reset.\n\n" +
                "If you did not make this change, please contact support immediately.");
            
            model.addAttribute("success", "Password has been reset successfully!");
            return "reset-password-success";
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to reset password. Please try again.");
            model.addAttribute("token", token);
            model.addAttribute("email", email);
            return "reset-password";
        }
    }

    // Process login request and send OTP
    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam("email") String email, 
                         RedirectAttributes redirectAttributes,
                         Model model) {
        
        // Basic email validation
        if (email == null || email.trim().isEmpty() || !isValidEmail(email)) {
            model.addAttribute("error", "Please enter a valid email address");
            return "login";
        }

        // Generate OTP
        String otp = generateOtp();
        String sessionId = generateSessionId();
        
        // Store OTP with timestamp and session
        otpStorage.put(email, new OtpData(otp, System.currentTimeMillis(), sessionId));
        
        try {
            // Send OTP via email
            emailService.sendSimpleMessage(email, "Your Login OTP", 
                "Your OTP code is: " + otp + "\nThis code will expire in 10 minutes.");
            
            // Add email to model for the verify page
            model.addAttribute("email", email);
            return "verify";
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to send OTP. Please try again.");
            return "login";
        }
    }

    // Verify OTP
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("email") String email,
                           @RequestParam("otp") String otpInput,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        
        OtpData otpData = otpStorage.get(email);
        
        // Check if OTP exists and is not expired (10 minutes)
        if (otpData == null) {
            model.addAttribute("error", "OTP not found or expired. Please request a new one.");
            model.addAttribute("email", email);
            return "verify";
        }
        
        // Check if OTP is expired (10 minutes)
        long currentTime = System.currentTimeMillis();
        if ((currentTime - otpData.getTimestamp()) > 10 * 60 * 1000) {
            otpStorage.remove(email);
            model.addAttribute("error", "OTP has expired. Please request a new one.");
            model.addAttribute("email", email);
            return "verify";
        }
        
        // Verify OTP
        if (otpData.getOtp().equals(otpInput)) {
            // OTP is valid - create user session
            userSessions.put(otpData.getSessionId(), email);
            
            // Clean up used OTP
            otpStorage.remove(email);
            
            // Redirect to dashboard with session
            return "redirect:/dashboard?session=" + otpData.getSessionId();
        } else {
            model.addAttribute("error", "Invalid OTP. Please try again.");
            model.addAttribute("email", email);
            return "verify";
        }
    }

    // Dashboard page
    @GetMapping("/dashboard")
    public String dashboard(@RequestParam("session") String sessionId, Model model) {
        String email = userSessions.get(sessionId);
        
        if (email == null) {
            return "redirect:/login?error=session_expired";
        }
        
        model.addAttribute("email", email);
        model.addAttribute("sessionId", sessionId);
        return "dashboard";
    }

    // Resend OTP
    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam("email") String email, Model model) {
        // Remove existing OTP for this email
        otpStorage.remove(email);
        
        // Generate new OTP
        String otp = generateOtp();
        String sessionId = generateSessionId();
        
        otpStorage.put(email, new OtpData(otp, System.currentTimeMillis(), sessionId));
        
        try {
            emailService.sendSimpleMessage(email, "Your New Login OTP", 
                "Your new OTP code is: " + otp + "\nThis code will expire in 10 minutes.");
            
            model.addAttribute("email", email);
            model.addAttribute("success", "New OTP sent successfully!");
            return "verify";
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to send OTP. Please try again.");
            model.addAttribute("email", email);
            return "verify";
        }
    }

    // Logout
    @GetMapping("/logout")
    public String logout(@RequestParam("session") String sessionId) {
        userSessions.remove(sessionId);
        return "redirect:/login?message=logged_out";
    }

    // Utility methods
    private String generateOtp() {
        int otp = ThreadLocalRandom.current().nextInt(100000, 999999);
        return String.valueOf(otp);
    }
    
    private String generateResetToken() {
        return java.util.UUID.randomUUID().toString();
    }
    
    private String generateSessionId() {
        return java.util.UUID.randomUUID().toString();
    }
    
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    // Inner class to store OTP data with timestamp
    private static class OtpData {
        private String otp;
        private long timestamp;
        private String sessionId;
        
        public OtpData(String otp, long timestamp, String sessionId) {
            this.otp = otp;
            this.timestamp = timestamp;
            this.sessionId = sessionId;
        }
        
        public String getOtp() { return otp; }
        public long getTimestamp() { return timestamp; }
        public String getSessionId() { return sessionId; }
    }
    
    // Inner class to store password reset data
    private static class PasswordResetData {
        private String token;
        private long timestamp;
        
        public PasswordResetData(String token, long timestamp) {
            this.token = token;
            this.timestamp = timestamp;
        }
        
        public String getToken() { return token; }
        public long getTimestamp() { return timestamp; }
    }
}