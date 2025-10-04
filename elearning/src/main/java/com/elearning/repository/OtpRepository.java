package com.elearning.repository;

import com.elearning.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    
    // Find OTP by email
    Optional<Otp> findByEmail(String email);
    
    // Find OTP by email and OTP code
    Optional<Otp> findByEmailAndOtpCode(String email, String otpCode);
    
    // Find valid OTP by email and OTP code (not used and not expired)
    @Query("SELECT o FROM Otp o WHERE o.email = :email AND o.otpCode = :otpCode AND o.used = false AND o.expiryDate > :currentTime")
    Optional<Otp> findValidOtpByEmailAndCode(@Param("email") String email, 
                                            @Param("otpCode") String otpCode,
                                            @Param("currentTime") LocalDateTime currentTime);
    
    // Find all OTPs by email
    List<Otp> findAllByEmail(String email);
    
    // Find OTP by email and purpose
    Optional<Otp> findByEmailAndPurpose(String email, String purpose);
    
    // Count OTPs by email
    Long countByEmail(String email);
    
    // Find all expired OTPs
    @Query("SELECT o FROM Otp o WHERE o.expiryDate < :currentTime")
    List<Otp> findAllExpiredOtps(@Param("currentTime") LocalDateTime currentTime);
    
    // Find all unused OTPs
    List<Otp> findByUsedFalse();
    
    // Delete all expired OTPs
    @Modifying
    @Query("DELETE FROM Otp o WHERE o.expiryDate < :currentTime")
    void deleteAllExpiredOtps(@Param("currentTime") LocalDateTime currentTime);
    
    // Delete all OTPs by email
    @Modifying
    @Query("DELETE FROM Otp o WHERE o.email = :email")
    void deleteAllByEmail(@Param("email") String email);
    
    // Delete OTP by email and OTP code
    @Modifying
    @Query("DELETE FROM Otp o WHERE o.email = :email AND o.otpCode = :otpCode")
    void deleteByEmailAndOtpCode(@Param("email") String email, @Param("otpCode") String otpCode);
    
    // Check if valid OTP exists for email
    @Query("SELECT COUNT(o) > 0 FROM Otp o WHERE o.email = :email AND o.used = false AND o.expiryDate > :currentTime")
    boolean existsValidOtpByEmail(@Param("email") String email, @Param("currentTime") LocalDateTime currentTime);
}