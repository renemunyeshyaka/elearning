package com.elearning.service;

import com.elearning.dto.StudentDTO;
import com.elearning.enums.UserStatus;

import java.util.List;
import java.util.Map;

public interface StudentService {
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