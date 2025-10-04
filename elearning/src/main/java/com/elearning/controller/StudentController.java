package com.elearning.controller;

import com.elearning.dto.StudentDTO;
import com.elearning.enums.UserStatus;
import com.elearning.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<StudentDTO> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        StudentDTO student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody StudentDTO studentDTO) {
        StudentDTO createdStudent = studentService.createStudent(studentDTO);
        return ResponseEntity.ok(createdStudent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentDTO studentDTO) {
        StudentDTO updatedStudent = studentService.updateStudent(id, studentDTO);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<StudentDTO>> getStudentsByStatus(@PathVariable UserStatus status) {
        List<StudentDTO> students = studentService.getStudentsByStatus(status);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/progress/{minProgress}")
    public ResponseEntity<List<StudentDTO>> getStudentsByProgress(@PathVariable Double minProgress) {
        List<StudentDTO> students = studentService.getStudentsByProgress(minProgress);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/average-progress")
    public ResponseEntity<Map<String, Double>> getAverageProgress() {
        Double averageProgress = studentService.getAverageProgress();
        return ResponseEntity.ok(Map.of("averageProgress", averageProgress));
    }

    @GetMapping("/course/{courseId}/average-progress")
    public ResponseEntity<Map<String, Double>> getAverageProgressByCourse(@PathVariable Long courseId) {
        Double averageProgress = studentService.getAverageProgressByCourse(courseId);
        return ResponseEntity.ok(Map.of("averageProgress", averageProgress));
    }

    @GetMapping("/{studentId}/course-progress")
    public ResponseEntity<Map<String, Object>> getStudentCourseProgress(@PathVariable Long studentId) {
        Map<String, Object> progress = studentService.getStudentCourseProgress(studentId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalStudentCount() {
        Long count = studentService.getTotalStudentCount();
        return ResponseEntity.ok(Map.of("totalStudents", count));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<StudentDTO>> getStudentsByCourse(@PathVariable Long courseId) {
        List<StudentDTO> students = studentService.getStudentsByCourse(courseId);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/search")
    public ResponseEntity<List<StudentDTO>> searchStudents(@RequestParam String keyword) {
        List<StudentDTO> students = studentService.searchStudents(keyword);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/count/active")
    public ResponseEntity<Map<String, Long>> getActiveStudentCount() {
        Long count = studentService.getActiveStudentCount();
        return ResponseEntity.ok(Map.of("activeStudents", count));
    }

    @GetMapping("/top-performing")
    public ResponseEntity<List<StudentDTO>> getTopPerformingStudents(@RequestParam(defaultValue = "10") int limit) {
        List<StudentDTO> students = studentService.getTopPerformingStudents(limit);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getStudentStatistics() {
        Map<String, Long> statistics = studentService.getStudentStatistics();
        return ResponseEntity.ok(statistics);
    }
}