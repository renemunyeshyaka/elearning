package com.elearning.dto;

public class EmailDTO {
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String content;
    private boolean isHtml = false;

    // Constructors, Getters, and Setters
    public EmailDTO() {}
    
    // Add all getters and setters
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    public String getCc() { return cc; }
    public void setCc(String cc) { this.cc = cc; }
    public String getBcc() { return bcc; }
    public void setBcc(String bcc) { this.bcc = bcc; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isHtml() { return isHtml; }
    public void setHtml(boolean html) { isHtml = html; }
}