package com.elearning.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "user_id")
public class Student extends User {
	
	@Column(name = "student_id", unique = true, nullable = false)
	private String studentId;    
    
    @Column(name = "overall_progress")
    private Double overallProgress = 0.0;
    
    @Column(name = "completed_courses")
    private Integer completedCourses = 0;
    
    @Column(name = "enrolled_courses")
    private Integer enrolledCourses = 0;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    private String address;
    
    private String city;
    
    private String country;
    
    @Column(name = "emergency_contact")
    private String emergencyContact;
    
    @Column(name = "emergency_phone")
    private String emergencyPhone;
    
    // Default constructor
    public Student() {
        super();
    }
    
    
    
    // Full parameterized constructor
    public Student(String firstName, String lastName, String email, String password,
                   String studentId, LocalDate dateOfBirth) {
        super(firstName, lastName, email, password);
        this.studentId = studentId;
        this.dateOfBirth = dateOfBirth;
    }

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Double getOverallProgress() {
        return overallProgress;
    }

    public void setOverallProgress(Double overallProgress) {
        this.overallProgress = overallProgress;
    }

    public Integer getCompletedCourses() {
        return completedCourses;
    }

    public void setCompletedCourses(Integer completedCourses) {
        this.completedCourses = completedCourses;
    }

    public Integer getEnrolledCourses() {
        return enrolledCourses;
    }

    public void setEnrolledCourses(Integer enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    // Business methods
    public void incrementCompletedCourses() {
        this.completedCourses++;
    }
    
    public void updateProgress(Double progress) {
        this.overallProgress = progress;
    }

    // toString method
    @Override
    public String toString() {
        return "Student{" +
                "id=" + getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", studentId='" + studentId + '\'' +
                ", overallProgress=" + overallProgress +
                ", completedCourses=" + completedCourses +
                '}';
    }
}