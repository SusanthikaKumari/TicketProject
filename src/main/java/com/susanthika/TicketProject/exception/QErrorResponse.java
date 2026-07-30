package com.susanthika.TicketProject.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
public class QErrorResponse {
    Error error;

    public static QErrorResponse fromError(Error error) {
        return QErrorResponse.builder().error(error).build();
    }
}
