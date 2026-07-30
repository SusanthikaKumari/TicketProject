package com.susanthika.TicketProject.exception;

public enum ErrorType {
    BAD_REQUEST(400),
    AUTHENTICATION_FAILED(401),
    ACCESS_DENIED(403),
    NOT_FOUND(404),

    USER_NOT_FOUND(404),
    CONFLICT(409),
    USER_NAME_DUPLICATED(409),
    SYSTEM_ERROR(500);

    private final int status;

    ErrorType(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
