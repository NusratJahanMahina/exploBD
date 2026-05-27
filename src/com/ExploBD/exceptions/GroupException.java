package com.ExploBD.exceptions;

public class GroupException extends Exception {
    
    
    public static int nameRequired = 1;
    public static int destinationRequired = 2;
    public static int planTooLong = 3;
    public static int budgetInvalid = 4;
    public static int dateConflict = 5;
    public static int pastDate = 6;
    public static int systemError = 7;
    public static int dateInvalid = 8;
    
    private int errorCode;
    
    public GroupException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public int getErrorCode() {
        return errorCode;
    }
}