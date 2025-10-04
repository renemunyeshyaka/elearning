package com.elearning.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AdminDTO {
    private Long id;
    private String adminId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String accessLevel;
    private String department;
    private String position;
    private String employeeId;
    private LocalDate hireDate;
    private Boolean superAdmin;
    private Boolean canManageUsers;
    private Boolean canManageCourses;
    private Boolean canManageContent;
    private Boolean canViewReports;
    private LocalDateTime lastSystemAccess;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public AdminDTO() {
    }

    public AdminDTO(Long id, String adminId, String email, String firstName, String lastName) {
        this.id = id;
        this.adminId = adminId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public Boolean getSuperAdmin() { return superAdmin; }
    public void setSuperAdmin(Boolean superAdmin) { this.superAdmin = superAdmin; }

    public Boolean getCanManageUsers() { return canManageUsers; }
    public void setCanManageUsers(Boolean canManageUsers) { this.canManageUsers = canManageUsers; }

    public Boolean getCanManageCourses() { return canManageCourses; }
    public void setCanManageCourses(Boolean canManageCourses) { this.canManageCourses = canManageCourses; }

    public Boolean getCanManageContent() { return canManageContent; }
    public void setCanManageContent(Boolean canManageContent) { this.canManageContent = canManageContent; }

    public Boolean getCanViewReports() { return canViewReports; }
    public void setCanViewReports(Boolean canViewReports) { this.canViewReports = canViewReports; }

    public LocalDateTime getLastSystemAccess() { return lastSystemAccess; }
    public void setLastSystemAccess(LocalDateTime lastSystemAccess) { this.lastSystemAccess = lastSystemAccess; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}