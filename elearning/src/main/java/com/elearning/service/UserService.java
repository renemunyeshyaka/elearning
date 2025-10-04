package com.elearning.service;

import com.elearning.dto.StudentDTO;
import com.elearning.dto.UserDTO;
import com.elearning.enums.UserRole;
import com.elearning.enums.UserStatus;

import java.util.List;
import java.util.Map;

public interface UserService {
    
    // ========== GENERAL USER MANAGEMENT METHODS ==========
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Long id);
    UserDTO getUserByEmail(String email);
    UserDTO getUserByUsername(String username);
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(UserDTO userDTO);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    UserDTO updateUserStatus(Long userId, UserStatus status);
    UserDTO changeUserRole(Long userId, UserRole role);
    UserDTO unlockAccount(Long userId);
    List<UserDTO> searchUsers(String keyword);
    List<UserDTO> getUsersByRole(UserRole role);
    List<UserDTO> getUsersByStatus(UserStatus status);
    Long getUserCount();
    Long getUserCountByRole(UserRole role);
    Map<String, Long> getUserStatistics();
    boolean userExists(String email);
    boolean existsByEmail(String email);
    UserDTO changePassword(Long userId, String newPassword);
    void resetPassword(String email, String newPassword);
    UserDTO updateProfile(Long userId, UserDTO userDTO);
    List<UserDTO> getRecentlyRegisteredUsers(int days);
    List<UserDTO> getUsersWithFailedLoginAttempts(int maxAttempts);
    void incrementLoginAttempts(String email);
    void resetLoginAttempts(String email);
    UserDTO updateLastLogin(Long userId);
    Map<String, Object> getUserDashboardStats();

    // ========== STUDENT-SPECIFIC METHODS ==========
    List<StudentDTO> getAllStudents();
    StudentDTO getStudentById(Long id);
    StudentDTO createStudent(StudentDTO studentDTO);
    StudentDTO updateStudent(Long id, StudentDTO studentDTO);
    void deleteStudent(Long id);
    List<StudentDTO> getStudentsByStatus(UserStatus status);
    List<StudentDTO> getStudentsByProgress(Double minProgress);
    Double getAverageProgress();
    Double getAverageProgressByCourse(Long courseId);
    Map<String, Object> getStudentCourseProgress(Long studentId);
    List<StudentDTO> getStudentsByCourse(Long courseId);
    Long getTotalStudentCount();
    List<StudentDTO> searchStudents(String keyword);
    Long getActiveStudentCount();
    List<StudentDTO> getTopPerformingStudents(int limit);
    Map<String, Long> getStudentStatistics();
}