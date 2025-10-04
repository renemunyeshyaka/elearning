package com.elearning.repository;

import com.elearning.entity.Enrollment;
import com.elearning.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    // Basic queries
    List<Enrollment> findByStudentId(Long studentId);
    
    List<Enrollment> findByCourseId(Long courseId);
    
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    List<Enrollment> findByStatus(EnrollmentStatus status);
    
    // Progress queries
    @Query("SELECT AVG(e.progress) FROM Enrollment e")
    Double findAverageProgressAcrossAllEnrollments();
    
    @Query("SELECT AVG(e.progress) FROM Enrollment e WHERE e.course.id = :courseId")
    Double findAverageProgressByCourseId(@Param("courseId") Long courseId);
    
    // Count queries
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.progress >= 100.0")
    Long countCompletedEnrollments();
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.progress > 0 AND e.progress < 100")
    Long countInProgressEnrollments();
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.progress >= 100.0")
    Long countCompletedEnrollmentsByCourse(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.student.id = :studentId")
    Long countByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId")
    Long countByCourseId(@Param("courseId") Long courseId);
    
    // Advanced queries
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.status = :status")
    List<Enrollment> findByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") EnrollmentStatus status);
    
    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId AND e.status = :status")
    List<Enrollment> findByCourseIdAndStatus(@Param("courseId") Long courseId, @Param("status") EnrollmentStatus status);
    
    @Query("SELECT e FROM Enrollment e WHERE e.progress >= :minProgress AND e.progress <= :maxProgress")
    List<Enrollment> findByProgressBetween(@Param("minProgress") Double minProgress, @Param("maxProgress") Double maxProgress);
    
    @Query("SELECT e FROM Enrollment e WHERE e.certificateIssued = true")
    List<Enrollment> findEnrollmentsWithCertificates();
    
    @Query("SELECT e FROM Enrollment e WHERE e.completionDate IS NOT NULL AND e.completionDate BETWEEN :startDate AND :endDate")
    List<Enrollment> findCompletedEnrollmentsBetweenDates(@Param("startDate") java.time.LocalDateTime startDate, 
                                                         @Param("endDate") java.time.LocalDateTime endDate);   
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.progress >= 100.0")
    Long countByProgressGreaterThanEqual(Double progress);   
    
}