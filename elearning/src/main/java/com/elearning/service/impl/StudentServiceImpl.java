package com.elearning.service.impl;

import com.elearning.dto.StudentDTO;
import com.elearning.entity.Enrollment;
import com.elearning.entity.User;
import com.elearning.enums.UserStatus;
import com.elearning.repository.EnrollmentRepository;
import com.elearning.repository.UserRepository;
import com.elearning.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ========== WORKAROUND METHODS FOR MISSING REPOSITORY METHODS ==========
    
    private List<User> findStudentsByCourseId(Long courseId) {
        List<User> allStudents = userRepository.findByUserType("STUDENT");
        return allStudents.stream()
                .filter(student -> enrollmentRepository.findAll().stream()
                        .anyMatch(enrollment -> 
                            courseId.equals(enrollment.getCourse().getId()) && 
                            student.getId().equals(enrollment.getStudent().getId())))
                .collect(Collectors.toList());
    }
    
    private List<User> findStudentsWithMinProgress(Double minProgress) {
        List<User> allStudents = userRepository.findByUserType("STUDENT");
        return allStudents.stream()
                .filter(student -> {
                    Double avgProgress = enrollmentRepository.findAll().stream()
                            .filter(enrollment -> student.getId().equals(enrollment.getStudent().getId()))
                            .mapToDouble(enrollment -> enrollment.getProgress() != null ? enrollment.getProgress() : 0.0)
                            .average()
                            .orElse(0.0);
                    return avgProgress >= minProgress;
                })
                .collect(Collectors.toList());
    }
    
    private List<User> findTopPerformingStudents(int limit) {
        List<User> allStudents = userRepository.findByUserType("STUDENT");
        return allStudents.stream()
                .sorted((s1, s2) -> {
                    Double progress1 = enrollmentRepository.findAll().stream()
                            .filter(enrollment -> s1.getId().equals(enrollment.getStudent().getId()))
                            .mapToDouble(enrollment -> enrollment.getProgress() != null ? enrollment.getProgress() : 0.0)
                            .average()
                            .orElse(0.0);
                    Double progress2 = enrollmentRepository.findAll().stream()
                            .filter(enrollment -> s2.getId().equals(enrollment.getStudent().getId()))
                            .mapToDouble(enrollment -> enrollment.getProgress() != null ? enrollment.getProgress() : 0.0)
                            .average()
                            .orElse(0.0);
                    return progress2.compareTo(progress1);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    // ========== SERVICE IMPLEMENTATION METHODS ==========

    @Override
    public List<StudentDTO> getAllStudents() {
        List<User> students = userRepository.findByUserType("STUDENT");
        return students.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StudentDTO getStudentById(Long id) {
        User student = userRepository.findStudentById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        return convertToDTO(student);
    }

    @Override
    public StudentDTO createStudent(StudentDTO studentDTO) {
        // Check if email already exists
        if (userRepository.findByEmail(studentDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + studentDTO.getEmail());
        }

        User student = convertToEntity(studentDTO);
        student.setUserType("STUDENT");
        student.setStatus(UserStatus.ACTIVE);
        
        // Encode password
        String password = studentDTO.getPassword() != null ? studentDTO.getPassword() : "defaultPassword123";
        student.setPassword(passwordEncoder.encode(password));
        
        User savedStudent = userRepository.save(student);
        return convertToDTO(savedStudent);
    }

    @Override
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        User existingStudent = userRepository.findStudentById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        
        // Update fields
        existingStudent.setFirstName(studentDTO.getFirstName());
        existingStudent.setLastName(studentDTO.getLastName());
        existingStudent.setEmail(studentDTO.getEmail());
        
        if (studentDTO.getStatus() != null) {
            existingStudent.setStatus(studentDTO.getStatus());
        }
        
        // Update password if provided
        if (studentDTO.getPassword() != null && !studentDTO.getPassword().isEmpty()) {
            existingStudent.setPassword(passwordEncoder.encode(studentDTO.getPassword()));
        }
        
        User updatedStudent = userRepository.save(existingStudent);
        return convertToDTO(updatedStudent);
    }

    @Override
    public void deleteStudent(Long id) {
        User student = userRepository.findStudentById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        userRepository.delete(student);
    }

    @Override
    public List<StudentDTO> getStudentsByStatus(UserStatus status) {
        List<User> students = userRepository.findByStatusAndUserType(status, "STUDENT");
        return students.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getStudentsByProgress(Double minProgress) {
        List<User> students = findStudentsWithMinProgress(minProgress);
        return students.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Double getAverageProgress() {
        List<Enrollment> allEnrollments = enrollmentRepository.findAll();
        if (allEnrollments.isEmpty()) {
            return 0.0;
        }
        
        return allEnrollments.stream()
                .mapToDouble(enrollment -> enrollment.getProgress() != null ? enrollment.getProgress() : 0.0)
                .average()
                .orElse(0.0);
    }

    @Override
    public Double getAverageProgressByCourse(Long courseId) {
        List<Enrollment> courseEnrollments = enrollmentRepository.findAll().stream()
                .filter(enrollment -> courseId.equals(enrollment.getCourse().getId()))
                .collect(Collectors.toList());
        
        if (courseEnrollments.isEmpty()) {
            return 0.0;
        }
        
        return courseEnrollments.stream()
                .mapToDouble(enrollment -> enrollment.getProgress() != null ? enrollment.getProgress() : 0.0)
                .average()
                .orElse(0.0);
    }

    @Override
    public Map<String, Object> getStudentCourseProgress(Long studentId) {
        // Verify student exists and is actually a student
        User student = userRepository.findStudentById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
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
            "studentName", student.getFirstName() + " " + student.getLastName(),
            "studentEmail", student.getEmail(),
            "overallProgress", Math.round(overallProgress * 100.0) / 100.0,
            "totalCourses", enrollments.size(),
            "completedCourses", completedCourses,
            "inProgressCourses", inProgressCourses,
            "notStartedCourses", notStartedCourses,
            "enrollments", enrollments.stream()
                    .map(this::convertEnrollmentToMap)
                    .collect(Collectors.toList())
        );
    }

    @Override
    public List<StudentDTO> getStudentsByCourse(Long courseId) {
        List<User> students = findStudentsByCourseId(courseId);
        return students.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long getTotalStudentCount() {
        return userRepository.countByUserType("STUDENT");
    }

    @Override
    public List<StudentDTO> searchStudents(String keyword) {
        List<User> students = userRepository.searchStudents(keyword);
        return students.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long getActiveStudentCount() {
        return userRepository.countByStatusAndUserType(UserStatus.ACTIVE, "STUDENT");
    }

    @Override
    public List<StudentDTO> getTopPerformingStudents(int limit) {
        List<User> students = findTopPerformingStudents(limit);
        return students.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getStudentStatistics() {
        Long totalStudents = userRepository.countByUserType("STUDENT");
        Long activeStudents = userRepository.countByStatusAndUserType(UserStatus.ACTIVE, "STUDENT");
        Long inactiveStudents = userRepository.countByStatusAndUserType(UserStatus.INACTIVE, "STUDENT");
        Long suspendedStudents = userRepository.countByStatusAndUserType(UserStatus.SUSPENDED, "STUDENT");
        
        // Count completed enrollments
        Long completedEnrollments = enrollmentRepository.findAll().stream()
                .filter(enrollment -> enrollment.getProgress() != null && enrollment.getProgress() >= 100.0)
                .count();
        
        // Count total enrollments for students
        Long totalEnrollments = enrollmentRepository.findAll().stream()
                .filter(enrollment -> {
                    User student = enrollment.getStudent();
                    return student != null && "STUDENT".equals(student.getUserType());
                })
                .count();
        
        return Map.of(
            "totalStudents", totalStudents,
            "activeStudents", activeStudents,
            "inactiveStudents", inactiveStudents,
            "suspendedStudents", suspendedStudents,
            "completedEnrollments", completedEnrollments,
            "totalEnrollments", totalEnrollments
        );
    }

    // ========== ADDITIONAL BUSINESS METHODS ==========

    public StudentDTO activateStudent(Long studentId) {
        User student = userRepository.findStudentById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
        student.setStatus(UserStatus.ACTIVE);
        User updatedStudent = userRepository.save(student);
        return convertToDTO(updatedStudent);
    }

    public StudentDTO deactivateStudent(Long studentId) {
        User student = userRepository.findStudentById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
        student.setStatus(UserStatus.INACTIVE);
        User updatedStudent = userRepository.save(student);
        return convertToDTO(updatedStudent);
    }

    public StudentDTO suspendStudent(Long studentId) {
        User student = userRepository.findStudentById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
        student.setStatus(UserStatus.SUSPENDED);
        User updatedStudent = userRepository.save(student);
        return convertToDTO(updatedStudent);
    }

    public Map<String, Object> getStudentDashboard(Long studentId) {
        User student = userRepository.findStudentById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
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
        
        Integer totalStudyTime = enrollments.stream()
                .mapToInt(enrollment -> enrollment.getTotalTimeSpent() != null ? enrollment.getTotalTimeSpent() : 0)
                .sum();
        
        // Get recent activity
        List<Enrollment> recentActivity = enrollments.stream()
                .filter(e -> e.getLastAccessed() != null)
                .sorted((e1, e2) -> e2.getLastAccessed().compareTo(e1.getLastAccessed()))
                .limit(5)
                .collect(Collectors.toList());
        
        return Map.of(
            "student", convertToBasicStudentInfo(student),
            "overallProgress", Math.round(overallProgress * 100.0) / 100.0,
            "totalCourses", enrollments.size(),
            "completedCourses", completedCourses,
            "inProgressCourses", inProgressCourses,
            "totalStudyTime", totalStudyTime, // in minutes
            "averageGrade", calculateAverageGrade(enrollments),
            "recentActivity", recentActivity.stream()
                    .map(this::convertEnrollmentToMap)
                    .collect(Collectors.toList())
        );
    }

    // ========== PRIVATE HELPER METHODS ==========

    private StudentDTO convertToDTO(User student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getEmail());
        dto.setStatus(student.getStatus());
        dto.setCreatedAt(student.getCreatedAt());
        dto.setUpdatedAt(student.getUpdatedAt());
        // Note: Removed setLastLoginAt and setPhoneNumber as they don't exist in StudentDTO
        return dto;
    }

    private Map<String, Object> convertToBasicStudentInfo(User student) {
        return Map.of(
            "id", student.getId(),
            "firstName", student.getFirstName(),
            "lastName", student.getLastName(),
            "email", student.getEmail(),
            "status", student.getStatus(),
            "phoneNumber", student.getPhoneNumber(), // Include phone number here
            "lastLoginAt", student.getLastLoginAt()  // Include last login here
        );
    }

    private User convertToEntity(StudentDTO studentDTO) {
        User student = new User();
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setEmail(studentDTO.getEmail());
        // Note: Removed setPhoneNumber as it doesn't exist in StudentDTO
        return student;
    }

    private Map<String, Object> convertEnrollmentToMap(Enrollment enrollment) {
        return Map.of(
            "enrollmentId", enrollment.getId(),
            "courseId", enrollment.getCourse() != null ? enrollment.getCourse().getId() : null,
            "courseName", enrollment.getCourse() != null ? enrollment.getCourse().getTitle() : "Unknown Course",
            "progress", enrollment.getProgress() != null ? enrollment.getProgress() : 0.0,
            "status", enrollment.getStatus(),
            "enrollmentDate", enrollment.getEnrollmentDate(),
            "lastAccessed", enrollment.getLastAccessed(),
            "totalTimeSpent", enrollment.getTotalTimeSpent() != null ? enrollment.getTotalTimeSpent() : 0,
            "grade", enrollment.getGrade(),
            "completed", enrollment.getProgress() != null && enrollment.getProgress() >= 100.0
        );
    }

    private Double calculateAverageGrade(List<Enrollment> enrollments) {
        List<Double> grades = enrollments.stream()
                .filter(e -> e.getGrade() != null && e.getGrade() > 0)
                .map(Enrollment::getGrade)
                .collect(Collectors.toList());
        
        if (grades.isEmpty()) {
            return 0.0;
        }
        
        return grades.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    // ========== BATCH OPERATIONS ==========

    public List<StudentDTO> createStudentsBatch(List<StudentDTO> studentDTOs) {
        return studentDTOs.stream()
                .map(this::createStudent)
                .collect(Collectors.toList());
    }

    public void updateStudentStatusBatch(List<Long> studentIds, UserStatus status) {
        List<User> students = studentIds.stream()
                .map(id -> userRepository.findStudentById(id)
                        .orElseThrow(() -> new RuntimeException("Student not found with id: " + id)))
                .collect(Collectors.toList());
        
        students.forEach(student -> student.setStatus(status));
        userRepository.saveAll(students);
    }

    public Map<String, Object> getStudentPerformanceReport(Long studentId) {
        User student = userRepository.findStudentById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
        List<Enrollment> enrollments = enrollmentRepository.findAll().stream()
                .filter(enrollment -> studentId.equals(enrollment.getStudent().getId()))
                .collect(Collectors.toList());
        
        Map<String, Long> statusDistribution = enrollments.stream()
                .collect(Collectors.groupingBy(
                    enrollment -> {
                        Double progress = enrollment.getProgress();
                        if (progress == null || progress == 0) return "Not Started";
                        else if (progress >= 100) return "Completed";
                        else if (progress >= 75) return "Almost Complete";
                        else if (progress >= 50) return "Halfway";
                        else if (progress >= 25) return "Quarter Complete";
                        else return "Just Started";
                    },
                    Collectors.counting()
                ));
        
        Double averageGrade = calculateAverageGrade(enrollments);
        Integer totalStudyTime = enrollments.stream()
                .mapToInt(e -> e.getTotalTimeSpent() != null ? e.getTotalTimeSpent() : 0)
                .sum();
        
        Long certificatesEarned = enrollments.stream()
                .filter(e -> e.getCertificateIssued() != null && e.getCertificateIssued())
                .count();
        
        return Map.of(
            "student", convertToBasicStudentInfo(student),
            "totalEnrollments", enrollments.size(),
            "averageGrade", averageGrade,
            "totalStudyTime", totalStudyTime,
            "certificatesEarned", certificatesEarned,
            "statusDistribution", statusDistribution,
            "enrollmentHistory", enrollments.stream()
                    .sorted((e1, e2) -> e2.getEnrollmentDate().compareTo(e1.getEnrollmentDate()))
                    .map(this::convertEnrollmentToMap)
                    .collect(Collectors.toList())
        );
    }
}