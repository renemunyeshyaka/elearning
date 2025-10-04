package com.elearning.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InstructorDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String employeeId;
    private String department;
    private String specialization;
    private String qualifications;
    private Integer yearsOfExperience;
    private BigDecimal hourlyRate;
    private String bio;
    private String profilePicture;
    private Boolean isAvailable;
    private Integer totalCoursesTaught;
    private Integer totalStudentsTaught;
    private Double averageRating;
    private LocalDate hireDate;
    
    // Constructors
    public InstructorDTO() {}
    
    public InstructorDTO(Long id, String email, String firstName, String lastName, String employeeId) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.employeeId = employeeId;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    
    public String getQualifications() { return qualifications; }
    public void setQualifications(String qualifications) { this.qualifications = qualifications; }
    
    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    
    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
    
    public Integer getTotalCoursesTaught() { return totalCoursesTaught; }
    public void setTotalCoursesTaught(Integer totalCoursesTaught) { this.totalCoursesTaught = totalCoursesTaught; }
    
    public Integer getTotalStudentsTaught() { return totalStudentsTaught; }
    public void setTotalStudentsTaught(Integer totalStudentsTaught) { this.totalStudentsTaught = totalStudentsTaught; }
    
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    
    // Utility methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public String getExperienceLevel() {
        if (yearsOfExperience == null) return "Beginner";
        if (yearsOfExperience < 2) return "Beginner";
        if (yearsOfExperience < 5) return "Intermediate";
        return "Expert";
    }
}