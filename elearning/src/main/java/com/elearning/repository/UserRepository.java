package com.elearning.repository;

import com.elearning.entity.User;
import com.elearning.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	 // User-specific methods - SIMPLIFIED VERSION
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.userType = 'USER'")
    Optional<User> findUserById(@Param("id") Long id);
    
    // Basic methods
    Optional<User> findByEmail(String email);
    List<User> findByStatus(UserStatus status);
    boolean existsByEmail(String email);
    
    // User type methods
    @Query("SELECT u FROM User u WHERE u.userType = :userType")
    List<User> findByUserType(@Param("userType") String userType);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = :userType")
    Long countByUserType(@Param("userType") String userType);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status AND u.userType = :userType")
    Long countByStatusAndUserType(@Param("status") UserStatus status, @Param("userType") String userType);
    
    @Query("SELECT u FROM User u WHERE u.status = :status AND u.userType = :userType")
    List<User> findByStatusAndUserType(@Param("status") UserStatus status, @Param("userType") String userType);
    
    // Search methods - SIMPLIFIED VERSION
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchUsers(@Param("keyword") String keyword);
    
    @Query("SELECT u FROM User u WHERE u.userType = 'STUDENT' AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> searchStudents(@Param("keyword") String keyword);
    
    // Student-specific methods - SIMPLIFIED VERSION
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.userType = 'STUDENT'")
    Optional<User> findStudentById(@Param("id") Long id);
    
    @Query("SELECT u FROM User u WHERE u.status = :status AND u.userType = 'STUDENT'")
    List<User> findStudentsByStatus(@Param("status") UserStatus status);
    
 // Default methods for convenience
    default List<User> findAllStudents() {
        return findByUserType("STUDENT");
    }
    
    default Optional<User> findStudentByEmail(String email) {
        return findByEmail(email)
                .filter(user -> "STUDENT".equals(user.getUserType()));
    }
    
    // Simplified course students query
    @Query("SELECT u FROM User u WHERE u.userType = 'STUDENT' AND u.id IN (SELECT e.student.id FROM Enrollment e WHERE e.course.id = :courseId)")
    List<User> findStudentsByCourseId(@Param("courseId") Long courseId);
    
    // Simplified progress query
    @Query("SELECT u FROM User u WHERE u.userType = 'STUDENT' AND u.id IN (SELECT e.student.id FROM Enrollment e WHERE e.progress >= :minProgress)")
    List<User> findStudentsWithMinProgress(@Param("minProgress") Double minProgress);
    
    // Native query for top performing students
    @Query(value = "SELECT u.* FROM users u WHERE u.user_type = 'STUDENT' AND u.id IN (SELECT e.student_id FROM enrollments e WHERE e.progress IS NOT NULL) ORDER BY (SELECT COALESCE(AVG(e2.progress), 0) FROM enrollments e2 WHERE e2.student_id = u.id) DESC LIMIT :limit", nativeQuery = true)
    List<User> findTopPerformingStudents(@Param("limit") int limit);
    
    // Count methods
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    Long countByStatus(@Param("status") UserStatus status);
    
    // Additional utility methods
    @Query("SELECT u FROM User u WHERE u.createdAt >= :date")
    List<User> findByCreatedAtAfter(@Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :date")
    Long countByCreatedAtAfter(@Param("date") LocalDateTime date);
    
    List<User> findByFailedLoginAttemptsGreaterThan(Integer attempts);
    
    
    
}