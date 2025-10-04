package com.elearning.constants;

public class ErrorCodes {
    
    // Validation Errors (400 series)
    public static final String ERR_400_VALIDATION = "ERR_400";
    public static final String ERR_401_UNAUTHORIZED = "ERR_401";
    public static final String ERR_403_FORBIDDEN = "ERR_403";
    public static final String ERR_404_NOT_FOUND = "ERR_404";
    public static final String ERR_409_CONFLICT = "ERR_409";
    
    // Server Errors (500 series)
    public static final String ERR_500_SERVER = "ERR_500";
    public static final String ERR_503_SERVICE_UNAVAILABLE = "ERR_503";
    
    // Business Logic Errors
    public static final String ERR_USER_EXISTS = "ERR_USER_001";
    public static final String ERR_USER_NOT_FOUND = "ERR_USER_002";
    public static final String ERR_INVALID_OTP = "ERR_AUTH_001";
    public static final String ERR_ACCOUNT_LOCKED = "ERR_AUTH_002";
    public static final String ERR_INVALID_CREDENTIALS = "ERR_AUTH_003";
    public static final String ERR_EMAIL_NOT_VERIFIED = "ERR_AUTH_004";
}