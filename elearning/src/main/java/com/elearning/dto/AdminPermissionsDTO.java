package com.elearning.dto;

public class AdminPermissionsDTO {
    private Boolean canManageUsers;
    private Boolean canManageCourses;
    private Boolean canManageContent;
    private Boolean canViewReports;
    private String accessLevel;
    
    // Constructors
    public AdminPermissionsDTO() {}
    
    public AdminPermissionsDTO(Boolean canManageUsers, Boolean canManageCourses, Boolean canManageContent, Boolean canViewReports) {
        this.canManageUsers = canManageUsers;
        this.canManageCourses = canManageCourses;
        this.canManageContent = canManageContent;
        this.canViewReports = canViewReports;
    }
    
    // Getters and Setters
    public Boolean getCanManageUsers() { return canManageUsers; }
    public void setCanManageUsers(Boolean canManageUsers) { this.canManageUsers = canManageUsers; }
    
    public Boolean getCanManageCourses() { return canManageCourses; }
    public void setCanManageCourses(Boolean canManageCourses) { this.canManageCourses = canManageCourses; }
    
    public Boolean getCanManageContent() { return canManageContent; }
    public void setCanManageContent(Boolean canManageContent) { this.canManageContent = canManageContent; }
    
    public Boolean getCanViewReports() { return canViewReports; }
    public void setCanViewReports(Boolean canViewReports) { this.canViewReports = canViewReports; }
    
    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }
}