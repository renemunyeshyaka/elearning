package com.elearning.repository;

import com.elearning.entity.Admin;
import com.elearning.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
	
	List<Admin> findByAccessLevel(String accessLevel);
	
	List<Admin> findByAdminLevel(String adminLevel);
    
    Optional<Admin> findByAdminId(String adminId);      
    
    List<Admin> findBySuperAdminTrue();
    
    List<Admin> findByDepartment(String department);
    
    List<Admin> findByStatus(UserStatus status);
    
    Optional<Admin> findByEmail(String email);    
    
    // Additional useful methods
    
    List<Admin> findByCanManageCoursesTrue();
    
    List<Admin> findByCanViewReportsTrue();
    
    @Query("SELECT a.department, COUNT(a) FROM Admin a GROUP BY a.department")
    List<Object[]> countAdminsByDepartment();
    
    @Query("SELECT a.accessLevel, COUNT(a) FROM Admin a GROUP BY a.accessLevel")
    List<Object[]> countAdminsByAccessLevel();
    
    long countBySuperAdminTrue();
    
    long countByDepartment(String department);
}