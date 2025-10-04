package com.elearning.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.elearning.enums.EnrollmentStatus;

@Entity
@Table(name = "enrollments")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "enrollment_type", discriminatorType = DiscriminatorType.STRING)
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(nullable = false)
    private Double progress = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    @Column(name = "enrollment_date")
    private LocalDateTime enrollmentDate;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Column(name = "last_accessed")
    private LocalDateTime lastAccessed;

    @Column(name = "total_time_spent")
    private Integer totalTimeSpent = 0; // in minutes

    @Column(name = "current_section")
    private String currentSection;

    @Column(name = "current_lesson")
    private String currentLesson;

    @Column(name = "grade")
    private Double grade;

    @Column(name = "certificate_issued")
    private Boolean certificateIssued = false;

    @Column(name = "certificate_issue_date")
    private LocalDateTime certificateIssueDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "enrollment_type", insertable = false, updatable = false)
    private String enrollmentType;

    // Constructors
    public Enrollment() {
    }

    public Enrollment(User student, Course course) {
        this.student = student;
        this.course = course;
        this.enrollmentDate = LocalDateTime.now();
        this.progress = 0.0;
        this.status = EnrollmentStatus.ACTIVE;
    }

    public Enrollment(User student, Course course, Double progress, EnrollmentStatus status) {
        this.student = student;
        this.course = course;
        this.progress = progress;
        this.status = status;
        this.enrollmentDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
        // Auto-update status based on progress
        if (progress >= 100.0 && this.status != EnrollmentStatus.COMPLETED) {
            this.status = EnrollmentStatus.COMPLETED;
            this.completionDate = LocalDateTime.now();
        } else if (progress > 0 && progress < 100 && this.status == EnrollmentStatus.ACTIVE) {
            this.status = EnrollmentStatus.IN_PROGRESS;
        }
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
        if (certificateIssued && this.certificateIssueDate == null) {
            this.certificateIssueDate = LocalDateTime.now();
        }
    }

    public LocalDateTime getCertificateIssueDate() {
        return certificateIssueDate;
    }

    public void setCertificateIssueDate(LocalDateTime certificateIssueDate) {
        this.certificateIssueDate = certificateIssueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getEnrollmentType() {
        return enrollmentType;
    }

    public void setEnrollmentType(String enrollmentType) {
        this.enrollmentType = enrollmentType;
    }

    // Utility Methods
    public boolean isCompleted() {
        return progress >= 100.0;
    }

    public boolean isInProgress() {
        return progress > 0 && progress < 100;
    }

    public boolean isNotStarted() {
        return progress == 0;
    }

    public void updateProgress(Double newProgress) {
        if (newProgress != null && newProgress >= 0 && newProgress <= 100) {
            this.progress = newProgress;
            this.updatedAt = LocalDateTime.now();
            
            // Auto-update status
            if (newProgress >= 100.0) {
                this.status = EnrollmentStatus.COMPLETED;
                if (this.completionDate == null) {
                    this.completionDate = LocalDateTime.now();
                }
            } else if (newProgress > 0) {
                this.status = EnrollmentStatus.IN_PROGRESS;
            }
        }
    }

    public void addTimeSpent(Integer minutes) {
        if (minutes != null && minutes > 0) {
            this.totalTimeSpent += minutes;
            this.lastAccessed = LocalDateTime.now();
        }
    }

    public void markAsCompleted() {
        this.progress = 100.0;
        this.status = EnrollmentStatus.COMPLETED;
        this.completionDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Lifecycle Methods
    @PrePersist
    protected void onCreate() {
        if (enrollmentDate == null) {
            enrollmentDate = LocalDateTime.now();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (progress == null) {
            progress = 0.0;
        }
        if (status == null) {
            status = EnrollmentStatus.ACTIVE;
        }
        if (totalTimeSpent == null) {
            totalTimeSpent = 0;
        }
        if (certificateIssued == null) {
            certificateIssued = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Enrollment)) return false;
        Enrollment that = (Enrollment) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // toString
    @Override
    public String toString() {
        return "Enrollment{" +
                "id=" + id +
                ", studentId=" + (student != null ? student.getId() : "null") +
                ", courseId=" + (course != null ? course.getId() : "null") +
                ", progress=" + progress +
                ", status=" + status +
                ", enrollmentType='" + enrollmentType + '\'' +
                '}';
    }
}