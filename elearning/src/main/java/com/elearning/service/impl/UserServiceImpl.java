package com.elearning.service.impl;

import com.elearning.dto.StudentDTO;
import com.elearning.dto.UserDTO;
import com.elearning.entity.Enrollment;
import com.elearning.entity.User;
import com.elearning.enums.UserRole;
import com.elearning.enums.UserStatus;
import com.elearning.repository.EnrollmentRepository;
import com.elearning.repository.UserRepository;
import com.elearning.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ========== WORKAROUND METHODS FOR MISSING REPOSITORY METHODS ==========
    
    private List<User> findByUserType(String userType) {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream()
                .filter(user -> userType.equals(user.getUserType()))
                .collect(Collectors.toList());
    }
    
    private Long countByUserType(String userType) {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream()
                .filter(user -> userType.equals(user.getUserType()))
                .count();
    }
    
    private Optional<User> findStudentById(Long id) {
        return userRepository.findById(id)
                .filter(user -> "STUDENT".equals(user.getUserType()));
    }
    
    private List<User> findByStatusAndUserType(UserStatus status, String userType) {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream()
                .filter(user -> status == user.getStatus() && userType.equals(user.getUserType()))
                .collect(Collectors.toList());
    }
    
    private Long countByStatusAndUserType(UserStatus status, String userType) {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream()
                .filter(user -> status == user.getStatus() && userType.equals(user.getUserType()))
                .count();
    }
    
    private Long countByStatus(UserStatus status) {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream()
                .filter(user -> status == user.getStatus())
                .count();
    }

    // ========== GENERAL USER MANAGEMENT METHODS ==========

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return convertToUserDTO(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return convertToUserDTO(user);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        return getUserByEmail(username);
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + userDTO.getEmail());
        }

        User user = convertToUserEntity(userDTO);
        
        if (user.getUserType() == null) {
            user.setUserType("STUDENT");
        }
        if (user.getStatus() == null) {
            user.setStatus(UserStatus.ACTIVE);
        }
        
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        
        User savedUser = userRepository.save(user);
        return convertToUserDTO(savedUser);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        return updateUser(userDTO.getId(), userDTO);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        
        if (userDTO.getRole() != null) {
            existingUser.setUserType(userDTO.getRole().name());
        }
        
        if (userDTO.getStatus() != null) {
            existingUser.setStatus(userDTO.getStatus());
        }

        if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);
        return convertToUserDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
    }

    @Override
    public UserDTO updateUserStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.setStatus(status);
        User updatedUser = userRepository.save(user);
        return convertToUserDTO(updatedUser);
    }

    @Override
    public UserDTO changeUserRole(Long userId, UserRole role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.setUserType(role.name());
        User updatedUser = userRepository.save(user);
        return convertToUserDTO(updatedUser);
    }

    @Override
    public UserDTO unlockAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.setAccountLocked(false);
        user.setLoginAttempts(0);
        user.setFailedLoginAttempts(0);
        user.setLocked(false);
        User updatedUser = userRepository.save(user);
        return convertToUserDTO(updatedUser);
    }

    @Override
    public List<UserDTO> searchUsers(String keyword) {
        List<User> users = userRepository.findAll().stream()
                .filter(user -> 
                    user.getFirstName().toLowerCase().contains(keyword.toLowerCase()) ||
                    user.getLastName().toLowerCase().contains(keyword.toLowerCase()) ||
                    user.getEmail().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        return users.stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getUsersByRole(UserRole role) {
        List<User> users = findByUserType(role.name());
        return users.stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getUsersByStatus(UserStatus status) {
        List<User> users = userRepository.findAll().stream()
                .filter(user -> status == user.getStatus())
                .collect(Collectors.toList());
        return users.stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long getUserCount() {
        return userRepository.count();
    }

    @Override
    public Long getUserCountByRole(UserRole role) {
        return countByUserType(role.name());
    }

    @Override
    public Map<String, Long> getUserStatistics() {
        Long totalUsers = userRepository.count();
        Long students = countByUserType("STUDENT");
        Long instructors = countByUserType("INSTRUCTOR");
        Long admins = countByUserType("ADMIN");
        Long activeUsers = countByStatus(UserStatus.ACTIVE);

        return Map.of(
            "totalUsers", totalUsers,
            "students", students,
            "instructors", instructors,
            "admins", admins,
            "activeUsers", activeUsers
        );
    }

    @Override
    public boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public UserDTO changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        User updatedUser = userRepository.save(user);
        return convertToUserDTO(updatedUser);
    }

    @Override
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public UserDTO updateProfile(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());

        User updatedUser = userRepository.save(user);
        return convertToUserDTO(updatedUser);
    }

    @Override
    public List<UserDTO> getRecentlyRegisteredUsers(int days) {
        LocalDateTime sinceDate = LocalDateTime.now().minusDays(days);
        List<User> allUsers = userRepository.findAll();
        List<User> recentUsers = allUsers.stream()
                .filter(user -> user.getCreatedAt() != null && user.getCreatedAt().isAfter(sinceDate))
                .collect(Collectors.toList());
        return recentUsers.stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getUsersWithFailedLoginAttempts(int maxAttempts) {
        List<User> allUsers = userRepository.findAll();
        List<User> usersWithFailedAttempts = allUsers.stream()
                .filter(user -> user.getFailedLoginAttempts() != null && user.getFailedLoginAttempts() > maxAttempts)
                .collect(Collectors.toList());
        return usersWithFailedAttempts.stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void incrementLoginAttempts(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        int currentAttempts = user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0;
        user.setFailedLoginAttempts(currentAttempts + 1);
        
        if (currentAttempts + 1 >= 5) {
            user.setAccountLocked(true);
            user.setLocked(true);
        }
        
        userRepository.save(user);
    }

    @Override
    public void resetLoginAttempts(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        user.setFailedLoginAttempts(0);
        user.setLoginAttempts(0);
        userRepository.save(user);
    }

    @Override
    public UserDTO updateLastLogin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.setLastLoginAt(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        return convertToUserDTO(updatedUser);
    }

    @Override
    public Map<String, Object> getUserDashboardStats() {
        Long totalUsers = userRepository.count();
        Long totalStudents = countByUserType("STUDENT");
        Long totalInstructors = countByUserType("INSTRUCTOR");
        Long activeUsers = countByStatus(UserStatus.ACTIVE);
        
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        List<User> allUsers = userRepository.findAll();
        Long newUsersToday = allUsers.stream()
                .filter(user -> user.getCreatedAt() != null && user.getCreatedAt().isAfter(yesterday))
                .count();

        return Map.of(
            "totalUsers", totalUsers,
            "totalStudents", totalStudents,
            "totalInstructors", totalInstructors,
            "activeUsers", activeUsers,
            "newUsersToday", newUsersToday
        );
    }

    // ========== STUDENT-SPECIFIC METHODS ==========

    @Override
    public List<StudentDTO> getAllStudents() {
        List<User> students = findByUserType("STUDENT");
        return students.stream()
                .map(this::convertToStudentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StudentDTO getStudentById(Long id) {
        User student = findStudentById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        return convertToStudentDTO(student);
    }

    @Override
    public StudentDTO createStudent(StudentDTO studentDTO) {
        if (userRepository.findByEmail(studentDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + studentDTO.getEmail());
        }

        User student = new User();
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setEmail(studentDTO.getEmail());
        student.setUserType("STUDENT");
        student.setStatus(UserStatus.ACTIVE);
        
        String password = studentDTO.getPassword() != null ? studentDTO.getPassword() : "defaultPassword123";
        student.setPassword(passwordEncoder.encode(password));
        
        User savedStudent = userRepository.save(student);
        return convertToStudentDTO(savedStudent);
    }

    @Override
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        User existingStudent = findStudentById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        
        existingStudent.setFirstName(studentDTO.getFirstName());
        existingStudent.setLastName(studentDTO.getLastName());
        existingStudent.setEmail(studentDTO.getEmail());
        
        if (studentDTO.getStatus() != null) {
            existingStudent.setStatus(studentDTO.getStatus());
        }
        
        if (studentDTO.getPassword() != null && !studentDTO.getPassword().trim().isEmpty()) {
            existingStudent.setPassword(passwordEncoder.encode(studentDTO.getPassword()));
        }
        
        User updatedStudent = userRepository.save(existingStudent);
        return convertToStudentDTO(updatedStudent);
    }

    @Override
    public void deleteStudent(Long id) {
        User student = findStudentById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        userRepository.delete(student);
    }

    @Override
    public List<StudentDTO> getStudentsByStatus(UserStatus status) {
        List<User> students = findByStatusAndUserType(status, "STUDENT");
        return students.stream()
                .map(this::convertToStudentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getStudentsByProgress(Double minProgress) {
        // Simplified version - in real app, this would query enrollments
        List<User> students = findByUserType("STUDENT");
        return students.stream()
                .map(this::convertToStudentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Double getAverageProgress() {
        Double average = enrollmentRepository.findAll().stream()
                .mapToDouble(enrollment -> enrollment.getProgress() != null ? enrollment.getProgress() : 0.0)
                .average()
                .orElse(0.0);
        return average;
    }

    @Override
    public Double getAverageProgressByCourse(Long courseId) {
        Double average = enrollmentRepository.findAll().stream()
                .filter(enrollment -> courseId.equals(enrollment.getCourse().getId()))
                .mapToDouble(enrollment -> enrollment.getProgress() != null ? enrollment.getProgress() : 0.0)
                .average()
                .orElse(0.0);
        return average;
    }

    @Override
    public Map<String, Object> getStudentCourseProgress(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findAll().stream()
                .filter(enrollment -> studentId.equals(enrollment.getStudent().getId()))
                .collect(Collectors.toList());
        
        double overallProgress = enrollments.stream()
                .mapToDouble(enrollment -> enrollment.getProgress() != null ? enrollment.getProgress() : 0.0)
                .average()
                .orElse(0.0);
        
        long completedCourses = enrollments.stream()
                .filter(e -> e.getProgress() != null && e.getProgress() >= 100.0)
                .count();
        
        long inProgressCourses = enrollments.stream()
                .filter(e -> e.getProgress() != null && e.getProgress() > 0 && e.getProgress() < 100)
                .count();
        
        long notStartedCourses = enrollments.stream()
                .filter(e -> e.getProgress() == null || e.getProgress() == 0)
                .count();
        
        return Map.of(
            "studentId", studentId,
            "overallProgress", Math.round(overallProgress * 100.0) / 100.0,
            "totalCourses", enrollments.size(),
            "completedCourses", completedCourses,
            "inProgressCourses", inProgressCourses,
            "notStartedCourses", notStartedCourses
        );
    }

    @Override
    public List<StudentDTO> getStudentsByCourse(Long courseId) {
        // Simplified version - filter students who have enrollments for this course
        List<User> allStudents = findByUserType("STUDENT");
        List<User> studentsInCourse = allStudents.stream()
                .filter(student -> enrollmentRepository.findAll().stream()
                        .anyMatch(enrollment -> 
                            courseId.equals(enrollment.getCourse().getId()) && 
                            student.getId().equals(enrollment.getStudent().getId())))
                .collect(Collectors.toList());
        return studentsInCourse.stream()
                .map(this::convertToStudentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long getTotalStudentCount() {
        return countByUserType("STUDENT");
    }

    @Override
    public List<StudentDTO> searchStudents(String keyword) {
        List<User> students = findByUserType("STUDENT").stream()
                .filter(student -> 
                    student.getFirstName().toLowerCase().contains(keyword.toLowerCase()) ||
                    student.getLastName().toLowerCase().contains(keyword.toLowerCase()) ||
                    student.getEmail().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        return students.stream()
                .map(this::convertToStudentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long getActiveStudentCount() {
        return countByStatusAndUserType(UserStatus.ACTIVE, "STUDENT");
    }

    @Override
    public List<StudentDTO> getTopPerformingStudents(int limit) {
        List<User> students = findByUserType("STUDENT");
        // Simplified - return first N students
        return students.stream()
                .limit(limit)
                .map(this::convertToStudentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getStudentStatistics() {
        Long totalStudents = countByUserType("STUDENT");
        Long activeStudents = countByStatusAndUserType(UserStatus.ACTIVE, "STUDENT");
        Long inactiveStudents = countByStatusAndUserType(UserStatus.INACTIVE, "STUDENT");
        
        Long completedEnrollments = enrollmentRepository.findAll().stream()
                .filter(enrollment -> enrollment.getProgress() != null && enrollment.getProgress() >= 100.0)
                .count();
        
        return Map.of(
            "totalStudents", totalStudents,
            "activeStudents", activeStudents,
            "inactiveStudents", inactiveStudents,
            "completedEnrollments", completedEnrollments
        );
    }

    // ========== CONVERSION METHODS ==========

    private UserDTO convertToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        
        // Convert userType to UserRole enum
        try {
            if (user.getUserType() != null) {
                dto.setRole(UserRole.valueOf(user.getUserType()));
            } else {
                dto.setRole(UserRole.ROLE_STUDENT);
            }
        } catch (IllegalArgumentException e) {
            dto.setRole(UserRole.ROLE_STUDENT);
        }
        
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setLastLoginAt(user.getLastLoginAt());
        dto.setAccountLocked(user.isAccountLocked());
        dto.setLoginAttempts(user.getLoginAttempts());
        
        dto.setEnabled(user.isEnabled());
        dto.setLocked(user.isLocked());
        dto.setFailedLoginAttempts(user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0);
        dto.setLastLogin(user.getLastLogin());
        dto.setEmailVerified(user.isEmailVerified() != null ? user.isEmailVerified() : false);
        
        return dto;
    }

    private User convertToUserEntity(UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        
        if (userDTO.getRole() != null) {
            user.setUserType(userDTO.getRole().name());
        } else {
            user.setUserType(UserRole.ROLE_STUDENT.name());
        }
        
        user.setStatus(userDTO.getStatus() != null ? userDTO.getStatus() : UserStatus.ACTIVE);
        
        user.setEnabled(userDTO.getEnabled() != null ? userDTO.getEnabled() : true);
        user.setLocked(userDTO.getLocked() != null ? userDTO.getLocked() : false);
        user.setFailedLoginAttempts(userDTO.getFailedLoginAttempts() != null ? userDTO.getFailedLoginAttempts() : 0);
        user.setLastLogin(userDTO.getLastLogin());
        user.setEmailVerified(userDTO.getEmailVerified() != null ? userDTO.getEmailVerified() : false);
        
        return user;
    }

    private StudentDTO convertToStudentDTO(User student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getEmail());
        dto.setStatus(student.getStatus());
        dto.setCreatedAt(student.getCreatedAt());
        dto.setUpdatedAt(student.getUpdatedAt());
        return dto;
    }
}