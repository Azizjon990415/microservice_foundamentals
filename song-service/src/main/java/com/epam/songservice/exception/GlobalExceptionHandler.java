package com.epam.songservice.exception;

import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), String.valueOf(HttpStatus.NOT_FOUND.value()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), String.valueOf(HttpStatus.BAD_REQUEST.value()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Error processing file", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleIOException(ConflictException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), String.valueOf(HttpStatus.CONFLICT.value()));

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse); }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ex.printStackTrace();
        if (ex instanceof HttpMediaTypeNotSupportedException) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), String.valueOf(HttpStatus.BAD_REQUEST.value()));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        if (ex instanceof MethodArgumentTypeMismatchException) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), String.valueOf(HttpStatus.BAD_REQUEST.value()));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        if (ex instanceof ConstraintViolationException) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), String.valueOf(HttpStatus.BAD_REQUEST.value()));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        if (ex instanceof HttpClientErrorException) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), String.valueOf(HttpStatus.BAD_REQUEST.value()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        if (ex instanceof HttpMessageNotReadableException) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), String.valueOf(HttpStatus.BAD_REQUEST.value()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        ErrorResponse errorResponse = new ErrorResponse("An unexpected error occurred", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> details = ex.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        ObjectError::getDefaultMessage,
                        (existing, replacement) -> existing.isEmpty() ? replacement : existing
                ));
        ErrorResponse errorResponse = new ErrorResponse("Validation error", String.valueOf(HttpStatus.BAD_REQUEST.value()), details);
        details.put("status", errorResponse.getErrorCode());
        details.put("message", errorResponse.getErrorMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(details);
    }



    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), String.valueOf(HttpStatus.BAD_REQUEST.value()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}