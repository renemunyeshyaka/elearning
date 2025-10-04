package com.elearning.controller;

import com.elearning.dto.UserDTO;
import com.elearning.enums.UserRole;
import com.elearning.enums.UserStatus;
import com.elearning.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        userDTO.setId(id);
        UserDTO updatedUser = userService.updateUser(userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserDTO> updateUserStatus(@PathVariable Long id, @RequestParam UserStatus status) {
        UserDTO updatedUser = userService.updateUserStatus(id, status);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserDTO> changeUserRole(@PathVariable Long id, @RequestParam UserRole role) {
        UserDTO updatedUser = userService.changeUserRole(id, role);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/unlock")
    public ResponseEntity<UserDTO> unlockAccount(@PathVariable Long id) {
        UserDTO updatedUser = userService.unlockAccount(id);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String keyword) {
        List<UserDTO> users = userService.searchUsers(keyword);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable UserRole role) {
        List<UserDTO> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<UserDTO>> getUsersByStatus(@PathVariable UserStatus status) {
        List<UserDTO> users = userService.getUsersByStatus(status);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getUserCount() {
        Long count = userService.getUserCount();
        return ResponseEntity.ok(Map.of("totalUsers", count));
    }

    @GetMapping("/count/role/{role}")
    public ResponseEntity<Map<String, Long>> getUserCountByRole(@PathVariable UserRole role) {
        Long count = userService.getUserCountByRole(role);
        return ResponseEntity.ok(Map.of("userCount", count));
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getUserStatistics() {
        Map<String, Long> statistics = userService.getUserStatistics();
        return ResponseEntity.ok(statistics);
    }

    // Student-specific endpoints (delegate to UserService)
    @GetMapping("/students")
    public ResponseEntity<List<com.elearning.dto.StudentDTO>> getAllStudents() {
        List<com.elearning.dto.StudentDTO> students = userService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<com.elearning.dto.StudentDTO> getStudentById(@PathVariable Long id) {
        com.elearning.dto.StudentDTO student = userService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    @PostMapping("/students")
    public ResponseEntity<com.elearning.dto.StudentDTO> createStudent(@Valid @RequestBody com.elearning.dto.StudentDTO studentDTO) {
        com.elearning.dto.StudentDTO createdStudent = userService.createStudent(studentDTO);
        return ResponseEntity.ok(createdStudent);
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<com.elearning.dto.StudentDTO> updateStudent(@PathVariable Long id, @Valid @RequestBody com.elearning.dto.StudentDTO studentDTO) {
        com.elearning.dto.StudentDTO updatedStudent = userService.updateStudent(id, studentDTO);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        userService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/students/status/{status}")
    public ResponseEntity<List<com.elearning.dto.StudentDTO>> getStudentsByStatus(@PathVariable UserStatus status) {
        List<com.elearning.dto.StudentDTO> students = userService.getStudentsByStatus(status);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/progress/{minProgress}")
    public ResponseEntity<List<com.elearning.dto.StudentDTO>> getStudentsByProgress(@PathVariable Double minProgress) {
        List<com.elearning.dto.StudentDTO> students = userService.getStudentsByProgress(minProgress);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/average-progress")
    public ResponseEntity<Map<String, Double>> getAverageProgress() {
        Double averageProgress = userService.getAverageProgress();
        return ResponseEntity.ok(Map.of("averageProgress", averageProgress));
    }

    @GetMapping("/students/course/{courseId}/average-progress")
    public ResponseEntity<Map<String, Double>> getAverageProgressByCourse(@PathVariable Long courseId) {
        Double averageProgress = userService.getAverageProgressByCourse(courseId);
        return ResponseEntity.ok(Map.of("averageProgress", averageProgress));
    }

    @GetMapping("/students/{studentId}/course-progress")
    public ResponseEntity<Map<String, Object>> getStudentCourseProgress(@PathVariable Long studentId) {
        Map<String, Object> progress = userService.getStudentCourseProgress(studentId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/students/course/{courseId}")
    public ResponseEntity<List<com.elearning.dto.StudentDTO>> getStudentsByCourse(@PathVariable Long courseId) {
        List<com.elearning.dto.StudentDTO> students = userService.getStudentsByCourse(courseId);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/count")
    public ResponseEntity<Map<String, Long>> getTotalStudentCount() {
        Long count = userService.getTotalStudentCount();
        return ResponseEntity.ok(Map.of("totalStudents", count));
    }
}