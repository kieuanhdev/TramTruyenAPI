package com.tramtruyen.api.presentation.exceptions;

import com.tramtruyen.api.presentation.payloads.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // Đánh dấu đây là Trạm kiểm soát lỗi toàn cục cho mọi Controller
public class GlobalExceptionHandler {

    // 1. Bắt các lỗi nghiệp vụ chung (RuntimeException mà ta ném ra ở Service)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), // Trả về lỗi 400 (Bad Request)
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 2. Bắt các lỗi Validation (Do @NotBlank, @Email, @Size từ DTO ném ra)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        // Trả về một JSON chứa danh sách các trường bị lỗi. VD: {"email": "Email không hợp lệ"}
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}