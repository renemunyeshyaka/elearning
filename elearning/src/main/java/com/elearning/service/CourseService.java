package com.elearning.service;

import com.elearning.dto.CourseDTO;
import java.util.List;

public interface CourseService {
    List<CourseDTO> getAllCourses();
    CourseDTO getCourseById(Long id);
    CourseDTO createCourse(CourseDTO courseDTO);
    CourseDTO updateCourse(Long id, CourseDTO courseDTO);
    void deleteCourse(Long id);
    List<CourseDTO> getCoursesByInstructor(Long instructorId);
    List<CourseDTO> getCoursesByCategory(String category);
    List<CourseDTO> searchCourses(String keyword);
    List<CourseDTO> getCoursesByLevel(String level);
    Long getTotalCourseCount();
    List<CourseDTO> getPopularCourses(int limit);
    List<CourseDTO> getCoursesByPriceRange(Double minPrice, Double maxPrice);
    Long getCourseCountByCategory(String category);
}