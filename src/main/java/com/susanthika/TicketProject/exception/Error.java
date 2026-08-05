package com.susanthika.TicketProject.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

import static java.time.Instant.now;
import static lombok.AccessLevel.PRIVATE;


@Value
@AllArgsConstructor(access = PRIVATE)
public class Error {
    ErrorType type;
    String message;
    int status;
    String path;
    Instant timestamp;
    Map<String, Object> data;

    public Error(AppException ex, String path) {
        ErrorType errorType = ex.getType();
        this.type = errorType;
        this.message = ex.getMessage();
        this.status = errorType.getStatus();
        this.path = path;
        this.timestamp = now();
        this.data = ex.getData();
    }

    public Error(ErrorType type, int status, String message, String path, Map<String, Object> data) {
        this.type = type;
        this.message = message;
        this.status = status;
        this.path = path;
        this.timestamp = now();
        this.data = data;
    }
}
