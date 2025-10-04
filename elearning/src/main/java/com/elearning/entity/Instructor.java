package com.elearning.entity;

import jakarta.persistence.*; 
import java.time.LocalDate;

@Entity
@Table(name = "instructors")
@PrimaryKeyJoinColumn(name = "user_id")  // Links to User.id
public class Instructor extends User {
	
	@Column(name = "instructor_id", unique = true, nullable = false)
	private String instructorId;
	
    private String specialization;
    
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience = 0;
    
    private String qualifications;
    
    private String bio;
    
    @Column(name = "hourly_rate")
    private Double hourlyRate;
    
    @Column(name = "total_courses")
    private Integer totalCourses = 0;
    
    private Double rating = 0.0;
    
    @Column(name = "total_ratings")
    private Integer totalRatings = 0;
    
    @Column(name = "hire_date")
    private LocalDate hireDate;
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    private String department;
    
    // Default constructor
    public Instructor() {
        super();
    }
    
	    // Parameterized constructor
	    public Instructor(String firstName, String lastName, String email, String password,
	            String specialization, Integer yearsOfExperience, String qualifications, String department) {
	super(firstName, lastName, email, password);
	this.specialization = specialization;
	this.yearsOfExperience = yearsOfExperience;
	this.qualifications = qualifications;
	this.department = department;
	this.hireDate = LocalDate.now();
	}

    // Getters and Setters
	    
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
	    
    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public Integer getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(Integer totalCourses) {
        this.totalCourses = totalCourses;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    // Business methods
    public void addRating(Double newRating) {
        double totalScore = this.rating * this.totalRatings;
        this.totalRatings++;
        this.rating = (totalScore + newRating) / this.totalRatings;
    }
    
    public void incrementCourseCount() {
        this.totalCourses++;
    }

    // toString method
    @Override
    public String toString() {
        return "Instructor{" +
                "id=" + getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", specialization='" + specialization + '\'' +
                ", yearsOfExperience=" + yearsOfExperience +
                ", totalCourses=" + totalCourses +
                ", rating=" + rating +
                '}';
    }
}