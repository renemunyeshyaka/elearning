package com.elearning.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("PUSH")
public class PushOtp extends Otp {
    
    @Column(name = "device_token", nullable = false)
    private String deviceToken;
    
    @Column(name = "device_type")
    private String deviceType; // IOS, ANDROID, WEB
    
    @Column(name = "app_version")
    private String appVersion;
    
    @Column(name = "push_notification_id")
    private String pushNotificationId;
    
    @Column(name = "delivery_status")
    private String deliveryStatus; // SENT, DELIVERED, FAILED
    
    @Column(name = "clicked")
    private Boolean clicked = false;
    
    @Column(name = "clicked_at")
    private LocalDateTime clickedAt;
    
    @Column(name = "notification_title")
    private String notificationTitle;
    
    @Column(name = "notification_body")
    private String notificationBody;
    
    // Constructors
    public PushOtp() {
    }
    
    public PushOtp(String deviceToken, String deviceType, String otpCode, LocalDateTime expiryDate, String purpose) {
        super(deviceToken + "@push", otpCode, expiryDate, purpose);
        this.deviceToken = deviceToken;
        this.deviceType = deviceType;
        this.deliveryStatus = "SENT";
    }
    
    // Getters and Setters
    public String getDeviceToken() {
        return deviceToken;
    }
    
    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
    
    public String getDeviceType() {
        return deviceType;
    }
    
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    
    public String getAppVersion() {
        return appVersion;
    }
    
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
    
    public String getPushNotificationId() {
        return pushNotificationId;
    }
    
    public void setPushNotificationId(String pushNotificationId) {
        this.pushNotificationId = pushNotificationId;
    }
    
    public String getDeliveryStatus() {
        return deliveryStatus;
    }
    
    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
    
    public Boolean getClicked() {
        return clicked;
    }
    
    public void setClicked(Boolean clicked) {
        this.clicked = clicked;
        if (clicked && clickedAt == null) {
            this.clickedAt = LocalDateTime.now();
        }
    }
    
    public LocalDateTime getClickedAt() {
        return clickedAt;
    }
    
    public void setClickedAt(LocalDateTime clickedAt) {
        this.clickedAt = clickedAt;
    }
    
    public String getNotificationTitle() {
        return notificationTitle;
    }
    
    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }
    
    public String getNotificationBody() {
        return notificationBody;
    }
    
    public void setNotificationBody(String notificationBody) {
        this.notificationBody = notificationBody;
    }
    
    // Business Methods
    public void markAsDelivered() {
        this.deliveryStatus = "DELIVERED";
    }
    
    public void markAsFailed() {
        this.deliveryStatus = "FAILED";
    }
    
    public void markAsClicked() {
        this.clicked = true;
        this.clickedAt = LocalDateTime.now();
    }
    
    public boolean isNotificationEngaged() {
        return clicked && "DELIVERED".equals(deliveryStatus);
    }
    
    @Override
    public boolean isValid() {
        return super.isValid() && isNotificationEngaged();
    }
    
    @Override
    public String toString() {
        return "PushOtp{" +
                "id=" + getId() +
                ", deviceType='" + deviceType + '\'' +
                ", deliveryStatus='" + deliveryStatus + '\'' +
                ", clicked=" + clicked +
                ", purpose='" + getPurpose() + '\'' +
                '}';
    }
}