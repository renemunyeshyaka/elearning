package com.elearning.service.impl;

import com.elearning.dto.EnrollmentDTO;
import com.elearning.entity.Course;
import com.elearning.entity.Enrollment;
import com.elearning.enums.EnrollmentStatus;
import com.elearning.entity.User;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.EnrollmentRepository;
import com.elearning.repository.UserRepository;
import com.elearning.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public List<EnrollmentDTO> getAllEnrollments() {
        List<Enrollment> enrollments = enrollmentRepository.findAll();
        return enrollments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EnrollmentDTO getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + id));
        return convertToDTO(enrollment);
    }

    @Override
    public EnrollmentDTO createEnrollment(EnrollmentDTO enrollmentDTO) {
        // Check if enrollment already exists
        if (enrollmentRepository.findByStudentIdAndCourseId(enrollmentDTO.getStudentId(), enrollmentDTO.getCourseId()).isPresent()) {
            throw new RuntimeException("Student is already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        
        // Set student
        User student = userRepository.findById(enrollmentDTO.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + enrollmentDTO.getStudentId()));
        enrollment.setStudent(student);
        
        // Set course
        Course course = courseRepository.findById(enrollmentDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + enrollmentDTO.getCourseId()));
        enrollment.setCourse(course);
        
        enrollment.setProgress(enrollmentDTO.getProgress() != null ? enrollmentDTO.getProgress() : 0.0);
        enrollment.setStatus(enrollmentDTO.getStatus() != null ? enrollmentDTO.getStatus() : EnrollmentStatus.ACTIVE);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return convertToDTO(savedEnrollment);
    }

    @Override
    public EnrollmentDTO updateEnrollment(Long id, EnrollmentDTO enrollmentDTO) {
        Enrollment existingEnrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + id));
        
        existingEnrollment.setProgress(enrollmentDTO.getProgress());
        existingEnrollment.setStatus(enrollmentDTO.getStatus());
        
        // Update completion date if progress is 100%
        if (enrollmentDTO.getProgress() >= 100.0 && existingEnrollment.getCompletionDate() == null) {
            existingEnrollment.setCompletionDate(LocalDateTime.now());
            existingEnrollment.setStatus(EnrollmentStatus.COMPLETED);
        }
        
        Enrollment updatedEnrollment = enrollmentRepository.save(existingEnrollment);
        return convertToDTO(updatedEnrollment);
    }

    @Override
    public void deleteEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + id));
        enrollmentRepository.delete(enrollment);
    }

    @Override
    public List<EnrollmentDTO> getEnrollmentsByStudent(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        return enrollments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentDTO> getEnrollmentsByCourse(Long courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        return enrollments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EnrollmentDTO getEnrollmentByStudentAndCourse(Long studentId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found for student: " + studentId + " and course: " + courseId));
        return convertToDTO(enrollment);
    }

    @Override
    public EnrollmentDTO updateProgress(Long enrollmentId, Double progress) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + enrollmentId));
        
        enrollment.setProgress(progress);
        
        // Update completion date if progress is 100%
        if (progress >= 100.0 && enrollment.getCompletionDate() == null) {
            enrollment.setCompletionDate(LocalDateTime.now());
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
        }
        
        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return convertToDTO(updatedEnrollment);
    }

    @Override
    public EnrollmentDTO markAsCompleted(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + enrollmentId));
        
        enrollment.setProgress(100.0);
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        enrollment.setCompletionDate(LocalDateTime.now());
        
        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return convertToDTO(updatedEnrollment);
    }

    @Override
    public Long getTotalEnrollmentsCount() {
        return enrollmentRepository.count();
    }

    @Override
    public List<EnrollmentDTO> getEnrollmentsByStatus(EnrollmentStatus status) {
        List<Enrollment> enrollments = enrollmentRepository.findByStatus(status);
        return enrollments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long getCompletedEnrollmentsCount() {
        return enrollmentRepository.countCompletedEnrollments();
    }

    @Override
    public Long getInProgressEnrollmentsCount() {
        return enrollmentRepository.countInProgressEnrollments();
    }

    @Override
    public Long getCompletedEnrollmentsCountByCourse(Long courseId) {
        return enrollmentRepository.countCompletedEnrollmentsByCourse(courseId);
    }

    private EnrollmentDTO convertToDTO(Enrollment enrollment) {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(enrollment.getId());
        dto.setStudentId(enrollment.getStudent().getId());
        dto.setCourseId(enrollment.getCourse().getId());
        dto.setProgress(enrollment.getProgress());
        dto.setStatus(enrollment.getStatus());
        dto.setEnrollmentDate(enrollment.getEnrollmentDate());
        dto.setCompletionDate(enrollment.getCompletionDate());
        dto.setLastAccessed(enrollment.getLastAccessed());
        dto.setTotalTimeSpent(enrollment.getTotalTimeSpent());
        dto.setCurrentSection(enrollment.getCurrentSection());
        dto.setCurrentLesson(enrollment.getCurrentLesson());
        dto.setGrade(enrollment.getGrade());
        dto.setCertificateIssued(enrollment.getCertificateIssued());
        dto.setCertificateIssueDate(enrollment.getCertificateIssueDate());
        return dto;
    }
}