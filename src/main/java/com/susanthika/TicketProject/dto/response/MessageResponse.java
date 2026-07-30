package com.susanthika.TicketProject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageResponse {
    private Long id;
    private String ticketCode;
    private String senderName;
    private String senderRole;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
