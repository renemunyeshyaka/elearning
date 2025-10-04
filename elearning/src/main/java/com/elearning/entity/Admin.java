package com.elearning.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User {
	
	@Column(name = "admin_id", unique = true, nullable = false)
    private String adminId;
	
    @Column(name = "admin_level")
    private String adminLevel;
    
    @Column(name = "access_level") 
    private String accessLevel;
    
    private String department;
    
    @Column(name = "super_admin")
    private Boolean superAdmin = false;
    
    @Column(name = "permission_level")
    private String permissionLevel;
    
    @Column(name = "can_manage_courses")
    private Boolean canManageCourses = true;

    @Column(name = "can_manage_users")
    private Boolean canManageUsers = true;
    
    @Column(name = "can_view_reports")
    private Boolean canViewReports = false;
    
    // Default constructor
    public Admin() {
        super();
    }
    
    // Parameterized constructor
    public Admin(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }
    
    // Full parameterized constructor
    public Admin(String firstName, String lastName, String email, String password, 
                 String adminLevel, String accessLevel, String department, Boolean superAdmin, 
                 Boolean canManageUsers, Boolean canManageCourses, Boolean canViewReports) {
        super(firstName, lastName, email, password);
        this.adminLevel = adminLevel;
        this.department = department;
        this.superAdmin = superAdmin;
        this.accessLevel = accessLevel; 
        this.canManageUsers  = canManageUsers;
        this.canManageCourses = canManageCourses;
        this.canViewReports = canViewReports;
    }
    
    public Boolean getCanViewReports() {
        return canViewReports;
    }

    public void setCanViewReports(Boolean canViewReports) {
        this.canViewReports = canViewReports;
    }
    
    public Boolean getCanManageCourses() {
        return canManageCourses;
    }

    public void setCanManageCourses(Boolean canManageCourses) {
        this.canManageCourses = canManageCourses;
    }
    
    
    public Boolean getCanManageUsers() {
        return canManageUsers;
    }

    public void setCanManageUsers(Boolean canManageUsers) {
        this.canManageUsers = canManageUsers;
    }

    // Getters and Setters
    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    public String getDepartment() {
        return department;
    }
    
    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Boolean getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(Boolean superAdmin) {
        this.superAdmin = superAdmin;
    }

    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    // toString method
    @Override
    public String toString() {
        return "Admin{" +
                "id=" + getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", adminLevel='" + adminLevel + '\'' +
                ", department='" + department + '\'' +
                ", superAdmin=" + superAdmin +
                '}';
    }
}