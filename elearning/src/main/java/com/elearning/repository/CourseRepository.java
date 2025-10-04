package com.elearning.repository;

import com.elearning.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    // Basic queries
    List<Course> findByInstructorId(Long instructorId);
    
    List<Course> findByCategory(String category);
    
    List<Course> findByLevel(String level);
    
    List<Course> findByIsPublishedTrue();
    
    List<Course> findByIsFeaturedTrue();
    
    // Search functionality
    @Query("SELECT c FROM Course c WHERE " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.category) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Course> searchCourses(@Param("keyword") String keyword);
    
    // Price range queries
    List<Course> findByPriceBetween(Double minPrice, Double maxPrice);
    
    List<Course> findByPriceLessThanEqual(Double maxPrice);
    
    List<Course> findByPriceGreaterThanEqual(Double minPrice);
    
    // Count queries
    Long countByCategory(String category);
    
    Long countByInstructorId(Long instructorId);
    
    Long countByIsPublishedTrue();
    
    // Popular courses (by enrollment count)
    @Query("SELECT c FROM Course c LEFT JOIN c.enrollments e GROUP BY c.id ORDER BY COUNT(e.id) DESC LIMIT :limit")
    List<Course> findPopularCourses(@Param("limit") int limit);
    
    // Highest rated courses
    @Query("SELECT c FROM Course c WHERE c.ratingCount > 0 ORDER BY (c.totalRating / c.ratingCount) DESC LIMIT :limit")
    List<Course> findTopRatedCourses(@Param("limit") int limit);
    
    // Newest courses
    List<Course> findTop10ByOrderByCreatedAtDesc();
    
    // Courses by instructor and category
    List<Course> findByInstructorIdAndCategory(Long instructorId, String category);
    
    // Courses by multiple categories
    @Query("SELECT c FROM Course c WHERE c.category IN :categories")
    List<Course> findByCategories(@Param("categories") List<String> categories);
}