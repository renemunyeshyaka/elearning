package com.elearning.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_logs")
public class SystemLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String action;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "log_level")
    private String logLevel; // INFO, WARN, ERROR, DEBUG
    
    private String module;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy; // Change from Admin to User for flexibility
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "affected_resource")
    private String affectedResource;
    
    @Column(name = "resource_id")
    private Long resourceId;
    
    // Constructors
    public SystemLog() {
        this.createdAt = LocalDateTime.now();
    }
    
    public SystemLog(String action, String description, String logLevel, String module) {
        this();
        this.action = action;
        this.description = description;
        this.logLevel = logLevel;
        this.module = module;
    }
    
    public SystemLog(String action, String description, String logLevel, String module, User createdBy) {
        this(action, description, logLevel, module);
        this.createdBy = createdBy;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getLogLevel() {
        return logLevel;
    }
    
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }
    
    public String getModule() {
        return module;
    }
    
    public void setModule(String module) {
        this.module = module;
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
    
    public User getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getAffectedResource() {
        return affectedResource;
    }
    
    public void setAffectedResource(String affectedResource) {
        this.affectedResource = affectedResource;
    }
    
    public Long getResourceId() {
        return resourceId;
    }
    
    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }
    
    // Utility Methods
    public boolean isError() {
        return "ERROR".equals(logLevel);
    }
    
    public boolean isWarning() {
        return "WARN".equals(logLevel);
    }
    
    // Lifecycle Methods
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    @Override
    public String toString() {
        return "SystemLog{" +
                "id=" + id +
                ", action='" + action + '\'' +
                ", logLevel='" + logLevel + '\'' +
                ", module='" + module + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}