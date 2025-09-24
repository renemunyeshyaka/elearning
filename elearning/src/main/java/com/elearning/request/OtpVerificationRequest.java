package com.elearning.request;

public class OtpVerificationRequest {
    private String username;
    private String otp;
    // getters/setters
    
 // Default constructor
    public OtpVerificationRequest() {
    }

    // Constructor with parameters (optional)
    public OtpVerificationRequest(String username, String otp) {
        this.username = username;
        this.otp = otp;
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Setter for username
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter for otp
    public String getOtp() {
        return otp;
    }

    // Setter for otp
    public void setOtp(String otp) {
        this.otp = otp;
    }
}
