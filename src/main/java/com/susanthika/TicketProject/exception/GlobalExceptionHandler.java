package com.susanthika.TicketProject.exception;

import com.susanthika.TicketProject.dto.response.ApiResponse;
import com.susanthika.TicketProject.util.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationErrors(MethodArgumentNotValidException exception){
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        log.error("validation fail: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponseUtil.error(
                                "Validation failed",
                                errors,
                                HttpStatus.BAD_REQUEST
                        )
                );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException exception){

        log.error("Resource not found: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        ApiResponseUtil.error(
                                exception.getMessage(),
                                HttpStatus.NOT_FOUND
                        )
                );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResource(DuplicateResourceException exception) {

        log.error("conflict: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponseUtil.error(
                        exception.getMessage(),
                        HttpStatus.CONFLICT
                ));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(BadRequestException exception){

        log.error("Bad Request: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponseUtil.error(
                                exception.getMessage(),
                                HttpStatus.BAD_REQUEST
                        )
                );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingBody(HttpMessageNotReadableException exception){

        log.error("Request body is required: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponseUtil.error(
                                "Request body is required",
                                HttpStatus.BAD_REQUEST
                        )
                );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception exception){

        //exception.printStackTrace();

        log.error("Internal server error: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ApiResponseUtil.error(
                                "An unexpected something occurred, please contact the administrator",
                                HttpStatus.INTERNAL_SERVER_ERROR
                        )
                );
    }

}
