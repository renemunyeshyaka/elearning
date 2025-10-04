package com.elearning.service;

import com.elearning.dto.StudentDTO;
import com.elearning.dto.UserDTO;
import com.elearning.entity.User;
import com.elearning.enums.UserRole;
import com.elearning.enums.UserStatus;
import com.elearning.enums.OtpType;
import com.elearning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    // Registration Methods
    public Map<String, Object> registerUser(String email, String password, String firstName, 
                                          String lastName, String phoneNumber, UserRole role) {
        Map<String, Object> response = new HashMap<>();

        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            response.put("success", false);
            response.put("message", "User already exists with this email");
            return response;
        }

        try {
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(email);
            userDTO.setPassword(password);
            userDTO.setFirstName(firstName);
            userDTO.setLastName(lastName);
            userDTO.setPhoneNumber(phoneNumber);
            userDTO.setRole(role);
            userDTO.setStatus(UserStatus.PENDING); // Set as pending until email verification
            
            UserDTO createdUser = userService.createUser(userDTO);

            // Generate and send registration OTP
            String otp = otpService.generateOtp(email);
            
            // Send welcome email
            emailService.sendEmail(createWelcomeEmail(email, firstName + " " + lastName));

            response.put("success", true);
            response.put("message", "Registration successful. OTP sent to email.");
            response.put("userId", createdUser.getId());
            response.put("otp", otp); // Remove this in production
            response.put("user", createdUser);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
        }

        return response;
    }

    // OTP Verification Methods
    public Map<String, Object> verifyRegistration(String email, String otpCode) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean verified = otpService.verifyOtp(email, otpCode);
            
            if (verified) {
                UserDTO userDTO = userService.getUserByEmail(email);
                if (userDTO != null) {
                    // Update user status to active and mark email as verified
                    userDTO.setEmailVerified(true);
                    userDTO.setEnabled(true);
                    userDTO.setStatus(UserStatus.ACTIVE);
                    UserDTO updatedUser = userService.updateUser(userDTO);

                    response.put("success", true);
                    response.put("message", "Email verified successfully");
                    response.put("user", updatedUser);
                } else {
                    response.put("success", false);
                    response.put("message", "User not found");
                }
            } else {
                response.put("success", false);
                response.put("message", "Invalid or expired OTP");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Verification failed: " + e.getMessage());
        }

        return response;
    }

    // Login Methods
    public Map<String, Object> initiateLogin(String email) {
        Map<String, Object> response = new HashMap<>();

        try {
            UserDTO userDTO = userService.getUserByEmail(email);
            if (userDTO == null) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }

            // Check if account is locked
            if (Boolean.TRUE.equals(userDTO.getLocked())) {
                response.put("success", false);
                response.put("message", "Account is locked. Please contact support.");
                return response;
            }

            // Check if email is verified
            if (!Boolean.TRUE.equals(userDTO.getEmailVerified())) {
                response.put("success", false);
                response.put("message", "Email not verified. Please verify your email first.");
                return response;
            }

            // Generate and send login OTP
            String otp = otpService.generateOtp(email);

            response.put("success", true);
            response.put("message", "OTP sent to your email");
            response.put("otp", otp); // Remove this in production

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Login initiation failed: " + e.getMessage());
        }

        return response;
    }

    public Map<String, Object> loginWithOtp(String email, String otpCode) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean otpValid = otpService.verifyOtp(email, otpCode);
            
            if (otpValid) {
                UserDTO userDTO = userService.getUserByEmail(email);
                if (userDTO != null) {
                    
                    // Record successful login
                    recordLogin(userDTO.getId());
                    
                    // Generate session token (simplified)
                    String sessionToken = generateSessionToken(userDTO);
                    
                    response.put("success", true);
                    response.put("message", "Login successful");
                    response.put("user", userDTO);
                    response.put("sessionToken", sessionToken);
                    response.put("role", userDTO.getRole());
                } else {
                    response.put("success", false);
                    response.put("message", "User not found");
                }
            } else {
                // Record failed login attempt
                recordFailedLogin(email);
                response.put("success", false);
                response.put("message", "Invalid OTP");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
        }

        return response;
    }

    public Map<String, Object> loginWithPassword(String email, String password) {
        Map<String, Object> response = new HashMap<>();

        try {
            UserDTO userDTO = userService.getUserByEmail(email);
            if (userDTO == null) {
                response.put("success", false);
                response.put("message", "Invalid email or password");
                return response;
            }

            // Check if account is locked
            if (Boolean.TRUE.equals(userDTO.getLocked())) {
                response.put("success", false);
                response.put("message", "Account is locked. Please contact support.");
                return response;
            }

            // Verify password
            if (!passwordEncoder.matches(password, userDTO.getPassword())) {
                recordFailedLogin(email);
                response.put("success", false);
                response.put("message", "Invalid email or password");
                return response;
            }

            // Record successful login
            recordLogin(userDTO.getId());
            
            // Generate session token
            String sessionToken = generateSessionToken(userDTO);
            
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("user", userDTO);
            response.put("sessionToken", sessionToken);
            response.put("role", userDTO.getRole());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
        }

        return response;
    }

    // Password Management Methods
    public Map<String, Object> initiatePasswordReset(String email) {
        Map<String, Object> response = new HashMap<>();

        try {
            UserDTO userDTO = userService.getUserByEmail(email);
            if (userDTO == null) {
                response.put("success", false);
                response.put("message", "User not found with this email");
                return response;
            }

            // Generate and send password reset OTP
            String otp = otpService.generateOtp(email);

            response.put("success", true);
            response.put("message", "Password reset OTP sent to your email");
            response.put("otp", otp); // Remove this in production

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Password reset initiation failed: " + e.getMessage());
        }

        return response;
    }

    public Map<String, Object> resetPassword(String email, String otpCode, String newPassword) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean otpValid = otpService.verifyOtp(email, otpCode);
            
            if (otpValid) {
                UserDTO userDTO = userService.getUserByEmail(email);
                if (userDTO != null) {
                    // Update password
                    userDTO.setPassword(newPassword);
                    UserDTO updatedUser = userService.updateUser(userDTO);
                    
                    // Send confirmation email
                    emailService.sendEmail(createPasswordChangeConfirmationEmail(email));

                    response.put("success", true);
                    response.put("message", "Password reset successfully");
                } else {
                    response.put("success", false);
                    response.put("message", "User not found");
                }
            } else {
                response.put("success", false);
                response.put("message", "Invalid or expired OTP");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Password reset failed: " + e.getMessage());
        }

        return response;
    }

    public Map<String, Object> changePassword(Long userId, String currentPassword, String newPassword) {
        Map<String, Object> response = new HashMap<>();

        try {
            UserDTO userDTO = userService.getUserById(userId);
            if (userDTO == null) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }

            // Verify current password
            if (!passwordEncoder.matches(currentPassword, userDTO.getPassword())) {
                response.put("success", false);
                response.put("message", "Current password is incorrect");
                return response;
            }

            // Update password
            userDTO.setPassword(newPassword);
            userService.updateUser(userDTO);
            
            // Send security notification
            emailService.sendEmail(createSecurityAlertEmail(
                userDTO.getEmail(),
                "Password Changed Successfully",
                "Your password was changed successfully. If you didn't make this change, please contact support immediately."
            ));

            response.put("success", true);
            response.put("message", "Password changed successfully");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Password change failed: " + e.getMessage());
        }

        return response;
    }

    // Account Management Methods
    public Map<String, Object> resendVerificationEmail(String email) {
        Map<String, Object> response = new HashMap<>();

        try {
            UserDTO userDTO = userService.getUserByEmail(email);
            if (userDTO == null) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }

            if (Boolean.TRUE.equals(userDTO.getEmailVerified())) {
                response.put("success", false);
                response.put("message", "Email is already verified");
                return response;
            }

            // Generate and send new verification OTP
            String otp = otpService.generateOtp(email);

            response.put("success", true);
            response.put("message", "Verification OTP sent to your email");
            response.put("otp", otp); // Remove this in production

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to resend verification email: " + e.getMessage());
        }

        return response;
    }

    public Map<String, Object> unlockUserAccount(Long userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            UserDTO unlockedUser = userService.unlockAccount(userId);
            if (unlockedUser != null) {
                emailService.sendEmail(createAccountUnlockedEmail(unlockedUser.getEmail()));
                
                response.put("success", true);
                response.put("message", "Account unlocked successfully");
            } else {
                response.put("success", false);
                response.put("message", "Failed to unlock account");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Account unlock failed: " + e.getMessage());
        }

        return response;
    }

    // Utility Methods
    private void recordLogin(Long userId) {
        try {
            UserDTO userDTO = userService.getUserById(userId);
            if (userDTO != null) {
                userDTO.setLastLogin(LocalDateTime.now());
                userDTO.setLastLoginAt(LocalDateTime.now());
                userDTO.setFailedLoginAttempts(0);
                userDTO.setLocked(false);
                userDTO.setAccountLocked(false);
                userService.updateUser(userDTO);
            }
        } catch (Exception e) {
            // Log error but don't break the login flow
            System.err.println("Failed to record login: " + e.getMessage());
        }
    }

    private void recordFailedLogin(String email) {
        try {
            UserDTO userDTO = userService.getUserByEmail(email);
            if (userDTO != null) {
                Integer currentAttempts = userDTO.getFailedLoginAttempts() != null ? 
                    userDTO.getFailedLoginAttempts() : 0;
                userDTO.setFailedLoginAttempts(currentAttempts + 1);
                
                // Lock account after 5 failed attempts
                if (currentAttempts + 1 >= 5) {
                    userDTO.setLocked(true);
                    userDTO.setAccountLocked(true);
                }
                
                userService.updateUser(userDTO);
            }
        } catch (Exception e) {
            // Log error but don't break the login flow
            System.err.println("Failed to record failed login: " + e.getMessage());
        }
    }

    private String generateSessionToken(UserDTO user) {
        return "SESSION-" + user.getId() + "-" + 
               UUID.randomUUID().toString().substring(0, 12).toUpperCase() + "-" +
               System.currentTimeMillis();
    }

    // Email Creation Methods
    private com.elearning.dto.EmailDTO createWelcomeEmail(String to, String fullName) {
        com.elearning.dto.EmailDTO email = new com.elearning.dto.EmailDTO();
        email.setTo(to);
        email.setSubject("Welcome to E-Learning Platform");
        email.setContent("Dear " + fullName + ",\n\nWelcome to our E-Learning Platform! " +
                        "We're excited to have you on board.\n\nBest regards,\nE-Learning Team");
        return email;
    }

    private com.elearning.dto.EmailDTO createPasswordChangeConfirmationEmail(String to) {
        com.elearning.dto.EmailDTO email = new com.elearning.dto.EmailDTO();
        email.setTo(to);
        email.setSubject("Password Changed Successfully");
        email.setContent("Your password has been changed successfully. " +
                        "If you didn't make this change, please contact support immediately.");
        return email;
    }

    private com.elearning.dto.EmailDTO createSecurityAlertEmail(String to, String subject, String content) {
        com.elearning.dto.EmailDTO email = new com.elearning.dto.EmailDTO();
        email.setTo(to);
        email.setSubject(subject);
        email.setContent(content);
        return email;
    }

    private com.elearning.dto.EmailDTO createAccountUnlockedEmail(String to) {
        com.elearning.dto.EmailDTO email = new com.elearning.dto.EmailDTO();
        email.setTo(to);
        email.setSubject("Account Unlocked");
        email.setContent("Your account has been unlocked. You can now login normally.");
        return email;
    }

    // Validation Methods
    public boolean validateSession(String sessionToken, Long userId) {
        // Simplified session validation
        // In production, use JWT or Spring Security sessions
        return sessionToken != null && sessionToken.startsWith("SESSION-" + userId);
    }

    public Map<String, Object> getUserProfile(Long userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            UserDTO userDTO = userService.getUserById(userId);
            if (userDTO != null) {
                // Remove sensitive information
                userDTO.setPassword(null);
                
                response.put("success", true);
                response.put("user", userDTO);
            } else {
                response.put("success", false);
                response.put("message", "User not found");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to get user profile: " + e.getMessage());
        }

        return response;
    }

    // Check if user exists
    public boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}