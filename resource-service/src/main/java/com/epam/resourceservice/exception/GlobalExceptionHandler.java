package com.epam.resourceservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        if (ex instanceof HttpMediaTypeNotSupportedException) {
            throw new BadRequestException(ex.getMessage());
        }
        if (ex instanceof MethodArgumentTypeMismatchException) {
            throw new BadRequestException(ex.getMessage());
        }
        if (ex instanceof ConstraintViolationException) {
            throw new BadRequestException(ex.getMessage());
        }
        ErrorResponse errorResponse = new ErrorResponse("An unexpected error occurred", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> details = ex.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((ObjectError) error).getObjectName(),
                        ObjectError::getDefaultMessage
                ));
        ErrorResponse errorResponse = new ErrorResponse("Validation error", String.valueOf(HttpStatus.BAD_REQUEST.value()), details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(SongServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleSongServiceException(SongServiceUnavailableException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Error occurred in Song Service", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), String.valueOf(HttpStatus.BAD_REQUEST.value()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}