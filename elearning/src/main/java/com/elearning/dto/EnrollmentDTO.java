package com.elearning.dto;

import com.elearning.enums.EnrollmentStatus;
import java.time.LocalDateTime;

public class EnrollmentDTO {
    private Long id;
    private Long studentId;
    private Long courseId;
    private Double progress;
    private EnrollmentStatus status;
    private LocalDateTime enrollmentDate;
    private LocalDateTime completionDate;
    private LocalDateTime lastAccessed;
    private Integer totalTimeSpent;
    private String currentSection;
    private String currentLesson;
    private Double grade;
    private Boolean certificateIssued;
    private LocalDateTime certificateIssueDate;

    // Constructors
    public EnrollmentDTO() {
    }

    public EnrollmentDTO(Long id, Long studentId, Long courseId, Double progress, EnrollmentStatus status) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.progress = progress;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDateTime enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    public LocalDateTime getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(LocalDateTime lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public Integer getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public void setTotalTimeSpent(Integer totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    public String getCurrentSection() {
        return currentSection;
    }

    public void setCurrentSection(String currentSection) {
        this.currentSection = currentSection;
    }

    public String getCurrentLesson() {
        return currentLesson;
    }

    public void setCurrentLesson(String currentLesson) {
        this.currentLesson = currentLesson;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public Boolean getCertificateIssued() {
        return certificateIssued;
    }

    public void setCertificateIssued(Boolean certificateIssued) {
        this.certificateIssued = certificateIssued;
    }

    public LocalDateTime getCertificateIssueDate() {
        return certificateIssueDate;
    }

    public void setCertificateIssueDate(LocalDateTime certificateIssueDate) {
        this.certificateIssueDate = certificateIssueDate;
    }
}