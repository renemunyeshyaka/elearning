package com.elearning.service;

import com.elearning.dto.EnrollmentDTO;
import com.elearning.enums.EnrollmentStatus;
import java.util.List;

public interface EnrollmentService {
    List<EnrollmentDTO> getAllEnrollments();
    EnrollmentDTO getEnrollmentById(Long id);
    EnrollmentDTO createEnrollment(EnrollmentDTO enrollmentDTO);
    EnrollmentDTO updateEnrollment(Long id, EnrollmentDTO enrollmentDTO);
    void deleteEnrollment(Long id);
    List<EnrollmentDTO> getEnrollmentsByStudent(Long studentId);
    List<EnrollmentDTO> getEnrollmentsByCourse(Long courseId);
    EnrollmentDTO getEnrollmentByStudentAndCourse(Long studentId, Long courseId);
    EnrollmentDTO updateProgress(Long enrollmentId, Double progress);
    EnrollmentDTO markAsCompleted(Long enrollmentId);
    Long getTotalEnrollmentsCount();
    List<EnrollmentDTO> getEnrollmentsByStatus(EnrollmentStatus status);
    Long getCompletedEnrollmentsCount();
    Long getInProgressEnrollmentsCount();
    Long getCompletedEnrollmentsCountByCourse(Long courseId);
}