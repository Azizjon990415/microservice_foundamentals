package com.epam.resourceprocessor.exception;

import java.util.Map;

public class ErrorResponse {
    private String errorMessage;
    private String errorCode;
    private Map<String, String> details;

    // Default constructor
    public ErrorResponse() {}

    // Constructor with fields
    public ErrorResponse(String errorMessage, String errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    // Constructor with fields including details
    public ErrorResponse(String errorMessage, String errorCode, Map<String, String> details) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.details = details;
    }

    // Getters and setters
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }
}