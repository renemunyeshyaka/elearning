package com.elearning.controller;

import com.elearning.dto.EnrollmentDTO;
import com.elearning.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "*")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping
    public ResponseEntity<List<EnrollmentDTO>> getAllEnrollments() {
        List<EnrollmentDTO> enrollments = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentDTO> getEnrollmentById(@PathVariable Long id) {
        EnrollmentDTO enrollment = enrollmentService.getEnrollmentById(id);
        return ResponseEntity.ok(enrollment);
    }

    @PostMapping
    public ResponseEntity<EnrollmentDTO> createEnrollment(@Valid @RequestBody EnrollmentDTO enrollmentDTO) {
        EnrollmentDTO createdEnrollment = enrollmentService.createEnrollment(enrollmentDTO);
        return ResponseEntity.ok(createdEnrollment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnrollmentDTO> updateEnrollment(@PathVariable Long id, @Valid @RequestBody EnrollmentDTO enrollmentDTO) {
        EnrollmentDTO updatedEnrollment = enrollmentService.updateEnrollment(id, enrollmentDTO);
        return ResponseEntity.ok(updatedEnrollment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsByStudent(@PathVariable Long studentId) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByStudent(studentId);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsByCourse(@PathVariable Long courseId) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<EnrollmentDTO> getEnrollmentByStudentAndCourse(
            @PathVariable Long studentId, 
            @PathVariable Long courseId) {
        EnrollmentDTO enrollment = enrollmentService.getEnrollmentByStudentAndCourse(studentId, courseId);
        return ResponseEntity.ok(enrollment);
    }

    @PatchMapping("/{id}/progress")
    public ResponseEntity<EnrollmentDTO> updateProgress(
            @PathVariable Long id, 
            @RequestParam double progress) {
        EnrollmentDTO updatedEnrollment = enrollmentService.updateProgress(id, progress);
        return ResponseEntity.ok(updatedEnrollment);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<EnrollmentDTO> markAsCompleted(@PathVariable Long id) {
        EnrollmentDTO updatedEnrollment = enrollmentService.markAsCompleted(id);
        return ResponseEntity.ok(updatedEnrollment);
    }
}