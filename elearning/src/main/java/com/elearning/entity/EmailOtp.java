package com.elearning.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("EMAIL")
public class EmailOtp extends Otp {
    
    @Column(name = "email_template")
    private String emailTemplate;
    
    @Column(name = "subject_line")
    private String subjectLine;
    
    @Column(name = "delivery_status")
    private String deliveryStatus; // SENT, DELIVERED, FAILED
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @Column(name = "last_retry_at")
    private LocalDateTime lastRetryAt;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    // Constructors
    public EmailOtp() {
    }
    
    public EmailOtp(String email, String otpCode, LocalDateTime expiryDate, String purpose) {
        super(email, otpCode, expiryDate, purpose);
        this.deliveryStatus = "SENT";
    }
    
    // Getters and Setters
    public String getEmailTemplate() {
        return emailTemplate;
    }
    
    public void setEmailTemplate(String emailTemplate) {
        this.emailTemplate = emailTemplate;
    }
    
    public String getSubjectLine() {
        return subjectLine;
    }
    
    public void setSubjectLine(String subjectLine) {
        this.subjectLine = subjectLine;
    }
    
    public String getDeliveryStatus() {
        return deliveryStatus;
    }
    
    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
    
    public Integer getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
    
    public LocalDateTime getLastRetryAt() {
        return lastRetryAt;
    }
    
    public void setLastRetryAt(LocalDateTime lastRetryAt) {
        this.lastRetryAt = lastRetryAt;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    // Business Methods
    public void markAsDelivered() {
        this.deliveryStatus = "DELIVERED";
    }
    
    public void markAsFailed() {
        this.deliveryStatus = "FAILED";
    }
    
    public void incrementRetryCount() {
        this.retryCount++;
        this.lastRetryAt = LocalDateTime.now();
    }
    
    public boolean canRetry() {
        return retryCount < 3 && !isExpired();
    }
    
    @Override
    public String toString() {
        return "EmailOtp{" +
                "id=" + getId() +
                ", email='" + getEmail() + '\'' +
                ", deliveryStatus='" + deliveryStatus + '\'' +
                ", retryCount=" + retryCount +
                ", purpose='" + getPurpose() + '\'' +
                '}';
    }
}