package com.ExploBD.exceptions;

public class ProfileException extends Exception {
    
    public static int nameRequired = 1;
    public static int emailRequired = 2;      
    public static int emailInvalid = 3;
    public static int phoneRequired = 4;      
    public static int phoneInvalid = 5;
    public static int phoneFormat = 6;         
    public static int genderRequired = 7;
    public static int nationalityRequired = 8; 
    public static int emergencyRequired = 9;   
    public static int budgetInvalid = 10;
    public static int dateInvalid = 11;
    public static int systemError = 12;
    
    private int errorCode;
    
    public ProfileException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public int getErrorCode() {
        return errorCode;
    }
}